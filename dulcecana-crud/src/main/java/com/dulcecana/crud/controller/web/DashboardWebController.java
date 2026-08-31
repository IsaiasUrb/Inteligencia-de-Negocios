package com.dulcecana.crud.controller.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardWebController {

    @GetMapping("/dashboards")
    public String dashboards() {
        return "dashboards/index";
    }

    @GetMapping("/dashboards/fact-ventas")
    public String factVentas() {
        return "dashboards/fact-ventas";
    }
}
