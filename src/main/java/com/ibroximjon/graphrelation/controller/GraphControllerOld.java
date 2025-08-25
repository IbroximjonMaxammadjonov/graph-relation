//package com.ibroximjon.graphrelation.controller;
//
//import com.ibroximjon.graphrelation.dto.GraphResponse;
//import com.ibroximjon.graphrelation.service.GraphServiceOld;
//import org.springframework.web.bind.annotation.*;
//
//@RestController
//@RequestMapping("/api/graph")
//public class GraphControllerOld {
//
//    private final GraphServiceOld graphService;
//
//    public GraphControllerOld(GraphServiceOld graphService) {
//        this.graphService = graphService;
//    }
//
//    // Kompaniya INN bo‘yicha graph
//    @GetMapping("/company/{inn}")
//    public GraphResponse getCompanyGraph(@PathVariable String inn) {
//        return graphService.buildGraphFromCompanyInn(inn);
//    }
//
//    // Shaxs PINFL bo‘yicha graph
//    @GetMapping("/person/{pinfl}")
//    public GraphResponse getPersonGraph(@PathVariable String pinfl) {
//        return graphService.buildGraphFromPersonPinfl(pinfl);
//    }
//}
