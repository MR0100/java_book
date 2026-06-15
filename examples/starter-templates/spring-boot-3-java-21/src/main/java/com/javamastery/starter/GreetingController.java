package com.javamastery.starter;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST endpoint for greetings.
 *
 * <p>TEACHING POINT: {@code @RestController} = {@code @Controller} + {@code @ResponseBody}, so the
 * returned {@link GreetingResponse} is automatically serialized to JSON by Jackson (no view layer).
 * {@code @RequestMapping("/api")} sets a common path prefix for every handler in this class.
 *
 * <p>TEACHING POINT (constructor injection): Spring injects the {@link GreetingService} through the
 * constructor. Constructor injection is preferred over field injection because it makes
 * dependencies explicit, allows the field to be {@code final}, and works without Spring in tests.
 */
@RestController
@RequestMapping("/api")
public class GreetingController {

    private final GreetingService greetingService;

    public GreetingController(GreetingService greetingService) {
        this.greetingService = greetingService;
    }

    /**
     * GET /api/greeting?name=Ada -> {"message":"Hello, Ada!","language":"en"}
     *
     * <p>TEACHING POINT: the parameter is {@code required = false} (no {@code defaultValue}), so a
     * missing {@code ?name=} arrives as {@code null}. We deliberately keep the "default visitor"
     * fallback in {@link GreetingService#greet(String)} — one source of truth — rather than
     * splitting that decision between the controller and the service.
     *
     * @param name optional query parameter; {@code null} when absent
     * @return the greeting payload, serialized to JSON
     */
    @GetMapping("/greeting")
    public GreetingResponse greeting(@RequestParam(name = "name", required = false) String name) {
        return greetingService.greet(name);
    }
}
