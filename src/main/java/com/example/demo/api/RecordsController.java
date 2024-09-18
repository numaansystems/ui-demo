package com.example.demo.api;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.Records;
import com.example.demo.sevice.RecordsService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

@RestController
@Slf4j
public class RecordsController {

    @Autowired
    private RecordsService service;



    @GetMapping("/api/records")
    public ResponseEntity<Collection<Records>> getRecords(){
        Collection<Records> records = service.getAllRecords();
        return ResponseEntity.ok(records);
    }

    @GetMapping("/api/records/{id}")
    public ResponseEntity<Records> create(@PathVariable Long id){
        if(!service.exists(id)){
            return ResponseEntity.notFound().build();
        }

        Records existing = service.findOne(id);
        return ResponseEntity.ok(existing);
    }

    @PostMapping(value =  "/api/records", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Records> create(@RequestBody Records newRecord){

        log.info("received object to create a new record: {}",newRecord);
        Records saved = service.createNew(newRecord);
        return ResponseEntity.ok(saved);
    }

    @PutMapping( value =  "/api/records/{id}", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<Records> update(@Valid @RequestBody Records update, @PathVariable Long id){
        if(!service.exists(id)){
            return ResponseEntity.notFound().build();
        }
        Records updated = service.update(id,update);
        return ResponseEntity.ok(updated);
    }

    @DeleteMapping("/api/records/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id){
        if(!service.exists(id)){
            return ResponseEntity.notFound().build();
        }
        service.delete(id);
        return ResponseEntity.ok().build();
    }


}
