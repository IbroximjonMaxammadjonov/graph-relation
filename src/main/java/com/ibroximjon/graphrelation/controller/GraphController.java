package com.ibroximjon.graphrelation.controller;

import com.ibroximjon.graphrelation.dto.GraphResponse;
import com.ibroximjon.graphrelation.service.GraphService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/graph")
public class GraphController {

    private final GraphService graphService;

    public GraphController(GraphService graphService) {
        this.graphService = graphService;
    }

    /**
     * INN yoki PINFL — ikkalasini ham qabul qiladi.
     * Masalan: /api/graph/REG10001 yoki /api/graph/12345678901234
     */
    @GetMapping("/{id}")
    public GraphResponse getFullGraph(@PathVariable String id) {
        return graphService.buildFullGraph(id);
    }

    // (xohlasangiz orqaga moslik uchun eski yo‘llar ham qolsin)
    @GetMapping("/company/{inn}")
    public GraphResponse getByCompany(@PathVariable String inn) {
        System.out.println("inn bo'yicha izlash boshlandi....");
        return graphService.buildFullGraph(inn);
    }

    @GetMapping("/person/{pinfl}")
    public GraphResponse getByPerson(@PathVariable String pinfl) {
        System.out.println("pinfl bo'yicha izlash boshlandi....");
        return graphService.buildFullGraph(pinfl);
    }


    //307527532
}

