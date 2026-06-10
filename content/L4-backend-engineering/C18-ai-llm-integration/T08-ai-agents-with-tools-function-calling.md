---
title: "AI Agents with Tools / Function Calling — Multi-Step Workflows"
slug: ai-agents-with-tools-function-calling
level: L4
module: "Backend Engineering"
section: "AI/LLM Integration"
type: concept
difficulty: advanced
order: 8
tags: [ai-agent, function-calling, tool-use, react, mcp, model-context-protocol, planning, multi-step, structured-output, sandbox, evaluation, agent-loop, langgraph]
prerequisites: [llm-api-fundamentals, langchain4j-framework, spring-ai-framework, prompt-engineering]
status: complete
estimated_minutes: 55
last_updated: 2026-06-10
---

# AI Agents with Tools / Function Calling — Multi-Step Workflows

An LLM that can call your Java code is an **agent**. Instead of just generating text, it can look up a customer, query a database, send an email, call an API — whatever you expose as a tool. The LLM plans, calls tools, observes results, and iterates until it reaches the user's goal.

By 2026 agentic systems have moved from research demos to production: customer support agents that resolve tickets end-to-end, code review agents that comment on PRs, ops agents that diagnose incidents. This topic covers the architecture, safety, evaluation, and operational discipline for building agents that actually work reliably in production.

> [!NOTE]
> Prerequisites: [LLM API Fundamentals](T01-llm-api-fundamentals.md), [LangChain4j](T02-langchain4j-framework.md) or [Spring AI](T03-spring-ai-framework.md), [Prompt Engineering](T04-prompt-engineering-for-backend-engineers.md).

## What Is an Agent (Concretely)

An agent is a loop:

```
1. LLM receives user goal + tool descriptions
2. LLM decides: "I'll call get_ticket('TK-1234')"
3. Your code executes get_ticket, returns result
4. LLM receives result, decides next step or final answer
5. Repeat until LLM produces a non-tool-call response
```

```
┌─────────────┐
│ User asks   │
│ "Resolve    │
│ TK-1234"    │
└──────┬──────┘
       │
       ▼
┌─────────────┐         ┌──────────────┐
│  LLM thinks │────────▶│ Tool: get_   │
│             │         │ ticket()     │
│             │◀────────│ → ticket data│
│             │         └──────────────┘
│             │
│             │         ┌──────────────┐
│             │────────▶│ Tool: search_│
│             │         │ kb()         │
│             │◀────────│ → 3 articles │
│             │         └──────────────┘
│             │
│             │         ┌──────────────┐
│             │────────▶│ Tool: reply_ │
│             │         │ to_ticket()  │
│             │◀────────│ → ok         │
│             │         └──────────────┘
│             │
│ Final reply │
│ "Done"      │
└─────────────┘
```

## When to Use an Agent vs a Chain

| Use a Chain (deterministic pipeline) | Use an Agent (LLM decides) |
|---|---|
| Steps known in advance | Steps depend on intermediate results |
| Single LLM call sufficient | Multi-step reasoning required |
| Quality must be predictable | Some variance acceptable |
| Cost matters | Higher cost OK for outcome quality |
| Audit trail critical | Audit trail also needed (just larger) |
| Examples: extract + classify + email | Examples: triage ticket, debug error, write+test code |

In practice, most production "AI features" are chains, not agents. Agents are the right tool for genuinely open-ended tasks.

## Function Calling Mechanics

Every major LLM provider supports function calling with similar mechanics. The model receives tool schemas, decides to call a tool, your code executes, you send the result back.

### OpenAI Tool Schema

```json
{
  "tools": [
    {
      "type": "function",
      "function": {
        "name": "get_ticket",
        "description": "Retrieve a support ticket by ID",
        "parameters": {
          "type": "object",
          "properties": {
            "ticket_id": {
              "type": "string",
              "description": "Ticket ID like 'TK-1234'"
            }
          },
          "required": ["ticket_id"]
        }
      }
    }
  ]
}
```

The LLM responds with:
```json
{
  "tool_calls": [
    {
      "id": "call_abc123",
      "function": {
        "name": "get_ticket",
        "arguments": "{\"ticket_id\": \"TK-1234\"}"
      }
    }
  ]
}
```

You execute, then send back:
```json
{
  "role": "tool",
  "tool_call_id": "call_abc123",
  "content": "{\"status\": \"open\", \"customer\": \"Alice\", ...}"
}
```

### Tool Definition with LangChain4j

```java
class TicketTools {
    @Tool("Retrieve a support ticket by ID")
    public Ticket getTicket(@P("Ticket ID like 'TK-1234'") String ticketId) {
        return ticketRepository.findById(ticketId)
            .orElseThrow(() -> new TicketNotFoundException(ticketId));
    }

    @Tool("Search the knowledge base")
    public List<Article> searchKnowledge(
            @P("Search query") String query,
            @P("Max results, default 5") int limit) {
        return knowledgeBase.search(query, Math.min(limit, 10));
    }

    @Tool("Reply to a support ticket")
    public String replyToTicket(
            @P("Ticket ID") String ticketId,
            @P("Reply message body") String body) {
        ticketService.addReply(ticketId, body);
        return "Reply sent to " + ticketId;
    }

    @Tool("Escalate ticket to human agent")
    public String escalate(@P("Ticket ID") String ticketId, @P("Reason") String reason) {
        escalationService.create(ticketId, reason);
        return "Escalated " + ticketId;
    }
}
```

### Wire as Spring AI Tools

```java
@Service
public class TicketService {

    @AiTool(description = "Retrieve a support ticket")
    public Ticket getTicket(@AiToolParam(description = "Ticket ID") String ticketId) {
        return repository.findById(ticketId).orElseThrow();
    }

    // ... more tools
}

@RestController
public class SupportAgent {

    @PostMapping("/agent/resolve")
    public String resolve(@RequestBody ResolveRequest req) {
        return chatClient.prompt()
            .system("You are a support agent. Resolve the user's request using tools.")
            .user(req.message())
            .tools(ticketService, knowledgeService, escalationService)
            .call().content();
    }
}
```

## The Agent Loop — What Frameworks Do for You

Behind every `.tools()` call, frameworks run a loop:

```java
public String agentLoop(String userMessage, List<Tool> tools, int maxIterations) {
    List<Message> messages = new ArrayList<>();
    messages.add(systemMessage);
    messages.add(new UserMessage(userMessage));

    for (int i = 0; i < maxIterations; i++) {
        ChatResponse response = llm.chat(messages, tools);

        if (response.getFinishReason() == FinishReason.STOP) {
            return response.getContent();
        }

        if (response.getFinishReason() == FinishReason.TOOL_CALLS) {
            messages.add(response.toAssistantMessage());
            for (ToolCall call : response.getToolCalls()) {
                String result = executeTools(call, tools);
                messages.add(new ToolMessage(call.getId(), result));
            }
            continue;
        }
    }

    throw new MaxIterationsExceededException();
}
```

Key safety mechanics:
- **`maxIterations`**: Hard cap to prevent infinite loops (typically 10-25)
- **Tool execution timeout**: Each tool call has its own timeout
- **Tool failure handling**: Errors go back to LLM as observations, not exceptions

## Designing Good Tools

The single biggest lever in agent quality is tool design.

### Tools Should Be Atomic and Composable

**Bad** (too coarse):
```java
@Tool public String resolveTicketFully(String ticketId) { ... }
```

**Good** (atomic):
```java
@Tool public Ticket getTicket(String ticketId) { ... }
@Tool public List<Article> searchKnowledge(String query) { ... }
@Tool public String replyToTicket(String ticketId, String body) { ... }
@Tool public String escalate(String ticketId, String reason) { ... }
```

The LLM composes the atomic tools into the right sequence. You're not pre-deciding the algorithm.

### Descriptions Are Programs

Every tool description and parameter name is part of the "program" the LLM executes. Be explicit:

**Bad**:
```java
@Tool("Get info") public Customer getInfo(String id) { ... }
```

**Good**:
```java
@Tool("Retrieve a customer record including subscription status, billing tier, and account creation date. Use this when the user mentions a customer ID or asks about a specific account.")
public Customer getCustomer(@P("Customer ID format CUST-NNNN") String customerId) { ... }
```

### Idempotent and Safe by Default

Tools may be called multiple times due to retries or LLM confusion. Bad:
```java
@Tool public String chargeCard(String customerId, double amount) { ... }
```

Without idempotency keys, the LLM calling this twice charges twice. Better:
```java
@Tool public String chargeCard(
    @P("Customer ID") String customerId,
    @P("Amount in USD") double amount,
    @P("Idempotency key UUID") String idempotencyKey) {
    return paymentService.charge(customerId, amount, idempotencyKey);
}
```

### Return Structured Data

LLMs parse JSON better than prose:

**Bad**:
```java
@Tool public String getCustomer(String id) {
    return "Customer Alice, tier Premium, joined 2020";
}
```

**Good**:
```java
@Tool public Customer getCustomer(String id) {
    return customerService.find(id);
}
// LangChain4j JSON-serializes Customer automatically
```

### Errors Are Tool Results, Not Exceptions

```java
@Tool public String chargeCard(String customerId, double amount, String idempotencyKey) {
    try {
        return paymentService.charge(customerId, amount, idempotencyKey);
    } catch (InsufficientFundsException e) {
        return "{\"error\": \"insufficient_funds\", \"message\": \"" + e.getMessage() + "\"}";
    } catch (Exception e) {
        return "{\"error\": \"system_error\", \"message\": \"" + e.getMessage() + "\"}";
    }
}
```

The LLM reasons over the error and decides what to do (retry, ask user, escalate). Throwing breaks the loop.

## The Model Context Protocol (MCP)

In late 2024 Anthropic released MCP (Model Context Protocol) — a standard for connecting LLMs to tools, data sources, and prompts. By 2026 it's the emerging standard, supported by Claude, OpenAI, and major Java frameworks.

### Why MCP

Before MCP, every framework had its own tool format. MCP standardizes:

- **Server-side**: tools, resources (data), and prompts
- **Client-side**: any MCP client (Claude Desktop, Cursor, custom)
- **Transport**: stdio, SSE, WebSocket

### MCP Server in Java (Spring AI)

```xml
<dependency>
    <groupId>org.springframework.ai</groupId>
    <artifactId>spring-ai-mcp-server-spring-boot-starter</artifactId>
</dependency>
```

```java
@Configuration
public class McpConfig {

    @Bean
    public List<McpServerFeatures.SyncToolRegistration> tools(
            TicketService ticketService) {
        return List.of(
            McpServerFeatures.SyncToolRegistration.builder()
                .name("get_ticket")
                .description("Retrieve a ticket by ID")
                .schema(Map.of(
                    "type", "object",
                    "properties", Map.of(
                        "ticket_id", Map.of("type", "string")),
                    "required", List.of("ticket_id")))
                .call(args -> {
                    String id = (String) args.get("ticket_id");
                    return new CallToolResult(
                        List.of(new TextContent(ticketService.getTicket(id).toJson())),
                        false);
                })
                .build()
        );
    }
}
```

### MCP Client in Java

```java
@Bean
public ChatClient chatClient(ChatClient.Builder builder, McpAsyncClient mcpClient) {
    return builder
        .defaultTools(new McpToolsCallback(mcpClient))
        .build();
}
```

MCP is becoming the de facto cross-language standard for agent tooling. Build new agent tools as MCP servers when possible — they work across Claude Desktop, Spring AI, LangChain4j, and any future MCP-aware client.

## Planning Patterns

### ReAct (Reason + Act)

The classic pattern (covered in [T04 Prompt Engineering](T04-prompt-engineering-for-backend-engineers.md)): the LLM alternates thinking and acting. Implicit in modern function-calling APIs.

### Plan-Then-Execute

For complex multi-step tasks, ask for a plan first:

```text
User: Set up a new customer account for Alice Smith at acme.com,
      tier Premium, with billing in EUR.

LLM (planning):
1. Create customer record (alice@acme.com, name "Alice Smith")
2. Set tier to Premium
3. Configure billing in EUR
4. Send welcome email

Now executing step 1...
```

```java
record Plan(List<Step> steps) {
    record Step(int number, String description, String tool) {}
}

Plan plan = chatClient.prompt()
    .system("Plan the steps needed. Don't execute yet.")
    .user(userRequest)
    .call().entity(Plan.class);

for (Step step : plan.steps()) {
    chatClient.prompt()
        .system("Execute this step using tools.")
        .user(step.description())
        .tools(allTools)
        .call().content();
}
```

Plan-then-execute is more predictable and easier to audit, but less adaptive.

### Reflexion / Self-Critique

After executing, the LLM evaluates its own work:

```java
String result = agentLoop(userMessage);

String critique = chatClient.prompt()
    .system("Did this action correctly accomplish the user's goal? Reply YES or list issues.")
    .user("Goal: " + userMessage + "\nAction: " + result)
    .call().content();

if (!critique.startsWith("YES")) {
    result = agentLoop("Previous attempt had these issues: " + critique
                    + ". Retry: " + userMessage);
}
```

Costly but improves quality on harder tasks.

### Multi-Agent Patterns

For complex domains, multiple specialized agents collaborate:

```
                User Request
                     │
                     ▼
              ┌─────────────┐
              │  Router     │  (decides which agent)
              └──────┬──────┘
                     │
       ┌─────────────┼─────────────┐
       ▼             ▼             ▼
  ┌─────────┐  ┌──────────┐  ┌──────────┐
  │ Billing │  │ Technical│  │ Account  │
  │ Agent   │  │ Agent    │  │ Agent    │
  └─────────┘  └──────────┘  └──────────┘
```

```java
@Service
public class MultiAgentRouter {

    private final ChatClient billingAgent, techAgent, accountAgent;

    public String handle(String userMessage) {
        AgentType type = chatClient.prompt()
            .system("Classify the user query as BILLING, TECHNICAL, or ACCOUNT")
            .user(userMessage)
            .call().entity(AgentType.class);

        return switch (type) {
            case BILLING -> billingAgent.prompt().user(userMessage).call().content();
            case TECHNICAL -> techAgent.prompt().user(userMessage).call().content();
            case ACCOUNT -> accountAgent.prompt().user(userMessage).call().content();
        };
    }
}
```

Patterns like CrewAI, AutoGen, and LangGraph formalize multi-agent collaboration.

## Safety Rails

Agents that take actions in your system need defense in depth.

### 1. Tool Allow-Listing Per User

```java
@Service
public class ScopedAgent {

    public String handle(String userId, String message) {
        UserScope scope = scopeService.forUser(userId);
        List<Object> allowedTools = new ArrayList<>();
        if (scope.canRead()) allowedTools.add(readTools);
        if (scope.canWrite()) allowedTools.add(writeTools);
        if (scope.canRefund()) allowedTools.add(refundTools);

        return chatClient.prompt()
            .user(message)
            .tools(allowedTools.toArray())
            .call().content();
    }
}
```

### 2. Human-in-the-Loop for Destructive Actions

```java
@Tool("Issue a refund (REQUIRES HUMAN APPROVAL)")
public String issueRefund(String orderId, double amount) {
    String approvalId = approvalQueue.enqueue(
        ApprovalRequest.refund(orderId, amount));
    return "Refund pending approval. Approval ID: " + approvalId
        + ". The customer will be informed once a human approves.";
}
```

LLM cannot bypass; humans review before money moves.

### 3. Audit Every Tool Call

```java
public class AuditingToolWrapper {

    public Object invoke(String toolName, Map<String, Object> args, String userId) {
        String invocationId = UUID.randomUUID().toString();
        auditLog.record(AuditEvent.builder()
            .id(invocationId)
            .userId(userId)
            .tool(toolName)
            .args(args)
            .timestamp(Instant.now())
            .build());

        try {
            Object result = actualInvoke(toolName, args);
            auditLog.recordResult(invocationId, "success", result);
            return result;
        } catch (Exception e) {
            auditLog.recordResult(invocationId, "error", e.getMessage());
            throw e;
        }
    }
}
```

For compliance (SOC2, HIPAA), full audit trail of agent actions is mandatory.

### 4. Rate Limits Per Agent Run

```java
public String agentLoop(String message, String userId, int maxIterations) {
    RateLimiter limiter = limiterFor(userId);
    int toolCalls = 0;

    for (int i = 0; i < maxIterations; i++) {
        if (toolCalls > 20) {
            throw new AgentBudgetExceededException("Too many tool calls");
        }
        if (!limiter.tryAcquire()) {
            throw new RateLimitedException();
        }
        // ... agent step
        toolCalls += response.getToolCalls().size();
    }
    throw new MaxIterationsExceededException();
}
```

### 5. Cost Caps

```java
public String agentLoop(String message, double maxCostUsd) {
    double spent = 0;
    while (spent < maxCostUsd) {
        ChatResponse r = llm.chat(messages, tools);
        spent += calculateCost(r);
        // ... continue
    }
    throw new AgentBudgetExceededException("Spent: $" + spent);
}
```

### 6. Sandboxed Tool Execution

For agent-written code (the most powerful pattern, also most dangerous):

```java
@Tool("Execute Python code in a sandboxed environment")
public String runPython(String code) {
    return sandboxClient.execute(
        SandboxRequest.builder()
            .language("python")
            .code(code)
            .timeoutSeconds(10)
            .memoryLimitMB(256)
            .networkAllowed(false)
            .filesystemReadonly(true)
            .build());
}
```

Use Firecracker microVMs, Wasm runtimes, or container-per-execution. Never `exec()` agent-generated code on your application server.

## Observability for Agents

Every tool call, every LLM round-trip, every cost. Distributed tracing is non-negotiable.

```java
@Component
public class TracingAgentAdvisor {

    private final Tracer tracer;

    public String runAgent(String userMessage) {
        Span agentSpan = tracer.spanBuilder("agent.run")
            .setAttribute("user.message", userMessage)
            .startSpan();
        try (Scope s = agentSpan.makeCurrent()) {
            return agentLoop(userMessage);
        } finally {
            agentSpan.end();
        }
    }

    private String agentLoop(String message) {
        int iteration = 0;
        while (iteration++ < MAX_ITERATIONS) {
            Span iterSpan = tracer.spanBuilder("agent.iteration")
                .setAttribute("iteration", iteration)
                .startSpan();
            try (Scope s = iterSpan.makeCurrent()) {
                ChatResponse r = llm.chat(...);
                iterSpan.setAttribute("tokens.prompt", r.getUsage().getPromptTokens());
                iterSpan.setAttribute("tokens.completion", r.getUsage().getCompletionTokens());
                iterSpan.setAttribute("finish_reason", r.getFinishReason().toString());

                for (ToolCall call : r.getToolCalls()) {
                    Span toolSpan = tracer.spanBuilder("agent.tool")
                        .setAttribute("tool.name", call.getName())
                        .setAttribute("tool.args", call.getArguments())
                        .startSpan();
                    try (Scope ts = toolSpan.makeCurrent()) {
                        executeTools(call);
                    } finally {
                        toolSpan.end();
                    }
                }
            } finally {
                iterSpan.end();
            }
        }
    }
}
```

Result: a trace tree showing every LLM call, every tool call, latency, cost, tokens. Critical for debugging.

## Agent Evaluation

Far harder than chain evaluation because the agent's path is non-deterministic.

### Task-Outcome Evaluation

```java
record AgentTestCase(String userGoal, Function<AgentState, Boolean> success) {}

List<AgentTestCase> tests = List.of(
    new AgentTestCase(
        "Refund order ORD-1234",
        state -> orderService.getById("ORD-1234").status() == REFUNDED),
    new AgentTestCase(
        "Find a high-priority bug for customer Alice",
        state -> state.getReplyContains("BUG-")),
    // ... more
);

double passRate = tests.stream()
    .mapToInt(t -> {
        runAgent(t.userGoal());
        return t.success().apply(currentState) ? 1 : 0;
    }).average().orElse(0);
```

### Path Evaluation

Are agents taking sensible paths?

```java
AgentTrace trace = runAgentWithTracing(goal);

assert trace.toolCalls().size() < 10;  // Not wandering
assert trace.distinctTools().size() <= 5;  // Focused
assert trace.totalCost() < 0.50;  // Under budget
assert !trace.calledTool("escalate");  // Resolved without escalation
```

### Replay-from-Trace Testing

Capture real production traces; replay against new code/prompt:

```java
List<AgentTrace> productionTraces = traceStore.recent(1000);

for (AgentTrace t : productionTraces) {
    AgentResult newResult = runAgent(t.userGoal());
    if (!equivalentOutcome(newResult, t.originalResult())) {
        regressions.add(t);
    }
}
```

## Common Pitfalls

> [!WARNING]
> **No `maxIterations` cap.** Stuck loops burn money and block resources. Always cap (typically 10-25).

> [!WARNING]
> **Tools that throw exceptions.** Breaks the agent loop. Return error JSON instead.

> [!WARNING]
> **Coarse tools.** "doEverything()" tools confuse the LLM. Atomic + composable.

> [!WARNING]
> **No idempotency keys.** Retries cause duplicate charges, double-sent emails.

> [!WARNING]
> **No human approval for destructive actions.** Agents WILL make wrong decisions. Gate refunds, deletions, comms.

> [!WARNING]
> **Letting agent-written code run on production.** Code generation tools must sandbox. Period.

> [!WARNING]
> **No cost cap.** Agent loops can spend $100 on one user request. Cap per-run.

> [!WARNING]
> **Insufficient observability.** Can't debug what you can't see. Trace every call.

> [!WARNING]
> **Single LLM owning everything.** For complex domains, multi-agent decomposition.

> [!WARNING]
> **Treating "it worked once" as "it works."** Agent quality is statistical. Eval over many cases.

## Practice

1. **Build a basic ticket-resolution agent.** Tools: get_ticket, search_kb, reply, escalate. Verify on 20 test scenarios.
2. **Add safety rails.** Tool allow-listing per user role. Human approval for refunds. Audit log of every tool call.
3. **Implement cost capping.** Track token spend per run; abort over $1.
4. **Add tracing.** OpenTelemetry spans for agent run, each iteration, each tool call. View in Jaeger.
5. **Build an evaluation harness.** 50 task-outcome tests + path quality tests. Run on every prompt change.
6. **Compare ReAct vs Plan-then-Execute.** Same task, measure success rate and cost.
7. **MCP server.** Convert your tools to an MCP server. Verify it works in Claude Desktop and your Spring AI app.
8. **Multi-agent decomposition.** Split your support agent into billing/technical/account. Add a router.
9. **Implement Reflexion.** After every run, have the LLM critique. Retry if critique fails. Measure quality gain vs cost.
10. **Replay-from-trace testing.** Capture 100 real agent traces. Replay against a prompt change. Detect regressions.
11. **Sandbox a code-execution tool.** Use Firecracker or Wasmtime. Verify no escape on adversarial inputs.
12. **The skeptic conversation.** A teammate says "let's just use the agent for everything." Write a 200-word case for when agents are WRONG and chains/deterministic logic are right.

## Recap

You should now be able to:

- Build production agents using LangChain4j Tools or Spring AI Functions
- Design tools that are atomic, idempotent, well-described, and return structured data
- Operate the agent loop safely with iteration caps, timeouts, and cost limits
- Apply planning patterns: ReAct, Plan-then-Execute, Reflexion, multi-agent
- Use MCP for cross-framework tool portability
- Add safety rails: scoped tools, human-in-the-loop, sandboxing, audit trails
- Trace and observe every LLM call, tool call, and decision
- Evaluate agents via task-outcome, path quality, and replay testing
- Avoid the common pitfalls that cause agents to burn money or take wrong actions

Agents are the highest-leverage and highest-risk pattern in LLM engineering. Done right, they automate work that would otherwise need human judgment; done wrong, they're a vector for runaway costs, compliance violations, and customer harm. The disciplines in this topic — careful tool design, safety rails, observability, evaluation — are what separate the two.

## Next

Continue to [Streaming LLM Responses](T09-streaming-llm-responses-sse-websocket.md) — token-by-token streaming for chat UIs.
