package com.akvelon.awstest.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Redirection to OpenAPI api documentation
 */
@Controller
public class HomeController {
    /**
     * Redirects to the Swagger UI page.
     *
     * @return The redirect view name.
     */
    @RequestMapping("/")
    public String index() {
        return "redirect:swagger-ui.html";
    }
}
