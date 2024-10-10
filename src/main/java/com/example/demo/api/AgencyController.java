package com.example.demo.api;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Agency;
import com.example.demo.sevice.AgencyService;

import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class AgencyController {

    @Autowired
    private AgencyService service;
    @GetMapping("/agencies")
    public ResponseEntity<List<Agency>> getAgencies(){
        return ResponseEntity.ok().body(service.getAgencies());
    }

    @GetMapping("/agencies-categories")
    public ResponseEntity<Map<String, Set<Agency>>> getAgenciesWithCategories(){
        return ResponseEntity.ok().body(service.getAgenciesAndCategories());
    }
}
