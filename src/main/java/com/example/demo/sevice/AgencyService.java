package com.example.demo.sevice;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.example.demo.model.Agency;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionType;
import com.fasterxml.jackson.databind.type.TypeFactory;

@Service
public class AgencyService {
    private ObjectMapper mapper = new ObjectMapper();
    public List<Agency> getAgencies(){
        try {
            String json = Files.readString(Paths.get("src/main/resources/agencies.json"));
            CollectionType typeReference =
                TypeFactory.defaultInstance().constructCollectionType(List.class, Agency.class);
            List<Agency> agencies =  mapper.readValue(json, typeReference);

            return agencies;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyList();
    }
    public Map<String, Set<Agency>> getAgenciesAndCategories(){
        try {
            System.out.println(Paths.get(".").toAbsolutePath());
            System.out.println(Paths.get("/Users/pavan_nonadm/Projects/ui-demo/src/main/resources/agencies.json").toAbsolutePath());
            String json = Files.readString(Paths.get("/Users/pavan_nonadm/Projects/ui-demo/src/main/resources/agencies.json"));
            System.out.println(json);
            CollectionType typeReference =
                TypeFactory.defaultInstance().constructCollectionType(List.class, Agency.class);
            List<Agency> agencies =  mapper.readValue(json, typeReference);
            Map<String, Set<Agency>> agenciesWithCategories = agencies.stream().collect(
                Collectors.groupingBy(Agency::getCategory, Collectors.toSet()   ));            
            return agenciesWithCategories;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Collections.emptyMap();
    }
}
