package com.example.demo.sevice;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


import org.springframework.stereotype.Service;

import com.example.demo.model.Records;

@Service
public class RecordsService {

    private Map<Long, Records> recordsData = new ConcurrentHashMap<>();

    public RecordsService(){
        init();
    }

    public Collection<Records> getAllRecords(){
        return recordsData.values();
    }

    public void init(){
        Records pavan = createRecords(1L, "PP", "Space", "Piscataway");
        Records ramakant = createRecords(2L, "Ram", "Divine", "Ayodhya");
        Records kittu = createRecords(3L, "KK", "Spiritual", "Dwaraka");
        Records cnu = createRecords(4L, "Cnu", "Gifts", "Tirupathi");
        Records vnu = createRecords(5L, "Vnu", "Music", "Karimnagar");
        Records rnu = createRecords(6L, "Rnu", "dummy", "dummy");
        List<Records> records =  Arrays.asList(pavan,ramakant,kittu,cnu,vnu,rnu);
        records.forEach(r->{
            recordsData.put(r.getId(), r);
        });
    }

        
    public Records createRecords(Long id, String name, String dept, String city) {
        return Records.builder().id(id).name(name).city(city).department(dept).build();
    }

    public Records findOne(Long id){
        return recordsData.get(id);
    }
    public Records createNew(Records newRecord) {
        if(recordsData.containsKey(newRecord.getId())){
            throw new RuntimeException("Record already exists!");
        }
        recordsData.put(newRecord.getId(), newRecord);
        return recordsData.get(newRecord.getId());
    }

    public Records update(Long id, Records update) {
        if(!recordsData.containsKey(update.getId())){
            throw new RuntimeException("Record DOES NOT exist!");
        }
        Records existing = recordsData.get(id);
        existing.setCity(update.getCity());
        existing.setDepartment(update.getDepartment());
        existing.setName(update.getName());
        existing.setId(id);
        recordsData.remove(id);

        recordsData.put(id, existing);
        return recordsData.get(update.getId());
    }

    public void delete(Long id){
        if(recordsData.containsKey(id)){
            recordsData.remove(id);
        }
    }

    public boolean exists(Long id){
        return recordsData.containsKey(id);
    }
}
