package com.javamastery.examples.cqrs.api;

import com.javamastery.examples.cqrs.api.ProductRequests.AdjustStockRequest;
import com.javamastery.examples.cqrs.api.ProductRequests.ChangePriceRequest;
import com.javamastery.examples.cqrs.api.ProductRequests.CreateProductRequest;
import com.javamastery.examples.cqrs.command.Commands.AdjustStock;
import com.javamastery.examples.cqrs.command.Commands.ChangePrice;
import com.javamastery.examples.cqrs.command.Commands.CreateProduct;
import com.javamastery.examples.cqrs.query.ProductQueryService;
import com.javamastery.examples.cqrs.query.ProductReadDtos.ProductSummary;
import com.javamastery.examples.cqrs.write.ProductCommandService;
import java.net.URI;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST entry point demonstrating the CQRS split at the HTTP layer.
 *
 * <p>The endpoints fall cleanly into two groups, wired to two different services:
 *
 * <ul>
 *   <li><b>Commands (writes)</b> — the {@code POST} endpoints delegate to {@link ProductCommandService}.
 *       They return only an id / 202-style acknowledgement; they do NOT return the resulting read
 *       view, because under eventual consistency that view may not have been projected yet.
 *   <li><b>Queries (reads)</b> — the {@code GET} endpoints delegate to {@link ProductQueryService},
 *       which reads exclusively from the denormalized read model.
 * </ul>
 *
 * <p>Notice the controller depends on BOTH services but they never depend on each other. That is the
 * separation CQRS buys you: the two sides can be deployed, scaled, and optimized independently.
 */
@RestController
@RequestMapping("/products")
public class ProductController {

    private final ProductCommandService commands;
    private final ProductQueryService queries;

    public ProductController(ProductCommandService commands, ProductQueryService queries) {
        this.commands = commands;
        this.queries = queries;
    }

    // ----- WRITE side (commands) -----

    @PostMapping
    public ResponseEntity<IdResponse> create(@RequestBody CreateProductRequest body) {
        int stock = body.initialStock() == null ? 0 : body.initialStock();
        Long id =
                commands.handle(new CreateProduct(body.sku(), body.name(), body.price(), stock));
        // 201 Created with a Location pointing at the query endpoint. The body may be momentarily
        // empty (404) under eventual consistency until the projection catches up — by design.
        return ResponseEntity.created(URI.create("/products/" + id)).body(new IdResponse(id));
    }

    @PostMapping("/{id}/price")
    public ResponseEntity<Void> changePrice(
            @PathVariable Long id, @RequestBody ChangePriceRequest body) {
        commands.handle(new ChangePrice(id, body.newPrice()));
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/{id}/stock")
    public ResponseEntity<Void> adjustStock(
            @PathVariable Long id, @RequestBody AdjustStockRequest body) {
        commands.handle(new AdjustStock(id, body.delta() == null ? 0 : body.delta()));
        return ResponseEntity.accepted().build();
    }

    // ----- READ side (queries) -----

    @GetMapping
    public List<ProductSummary> list(
            @RequestParam(name = "inStock", defaultValue = "false") boolean inStockOnly) {
        return inStockOnly ? queries.findInStock() : queries.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductSummary> getOne(@PathVariable Long id) {
        return queries.findById(id).map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    /** Bad commands surface as 400s rather than 500s. */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> onBadCommand(IllegalArgumentException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    /** Tiny response record for the create endpoint. */
    public record IdResponse(Long id) {}
}
