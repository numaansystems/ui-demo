package com.example.demo.sevice;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

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
}
