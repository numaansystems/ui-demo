package com.example.demo.sevice;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.example.demo.model.Records;

@Service
public class RecordsService {

    private Map<Long, List<Records>> recordsData = new ConcurrentHashMap<>();

    public RecordsService() {
        init();
    }

    public Collection<List<Records>> getAllRecords() {
        return recordsData.values();
    }

    public void init() {
        Records pavan = createRecords(11L, "PP", "Space", "Piscataway");
        Records kp = createRecords(11L, "KP", "Ground", "Edison");
        Records ramakant = createRecords(12L, "Ram", "Divine", "Ayodhya");
        Records kittu = createRecords(13L, "KK", "Spiritual", "Dwaraka");
        Records cnu = createRecords(14L, "Cnu", "Gifts", "Tirupathi");
        Records vnu = createRecords(15L, "Vnu", "Music", "Karimnagar");
        Records rnu = createRecords(16L, "Rnu", "dummy", "dummy");
        List<Records> records = Arrays.asList(pavan, kp, ramakant, kittu, cnu, vnu, rnu);
        records.forEach(r -> {
            long id = r.getId();
            List<Records> recs = new ArrayList<Records>();
            if (recordsData.containsKey(id)) {
                recs = recordsData.get(id);
            }
            recs.add(r);
            recordsData.put(id, recs);
        });
    }

    public Records createRecords(Long id, String name, String dept, String city) {
        return Records.builder().id(id).name(name).city(city).department(dept).build();
    }

    public List<Records> findOne(Long id) {
        return recordsData.get(id);
    }

    public Records createNew(Records newRecord) {
        /*
         * if (recordsData.containsKey(newRecord.getId())) {
         * throw new RuntimeException("Record already exists!");
         * }
         */
        long id = newRecord.getId();
        List<Records> recs = new ArrayList<Records>();
        if (recordsData.containsKey(id)) {
            recs = recordsData.get(id);
        }
        recs.add(newRecord);
        recordsData.put(id, recs);
        return newRecord;
    }
    /*
     * public Records update(Long id, Records update) {
     * if (!recordsData.containsKey(update.getId())) {
     * throw new RuntimeException("Record DOES NOT exist!");
     * }
     * Records existing = recordsData.get(id);
     * existing.setCity(update.getCity());
     * existing.setDepartment(update.getDepartment());
     * existing.setName(update.getName());
     * existing.setId(id);
     * recordsData.remove(id);
     * 
     * recordsData.put(id, existing);
     * return recordsData.get(update.getId());
     * }
     */

    public void delete(Long id) {
        if (recordsData.containsKey(id)) {
            recordsData.remove(id);
        }
    }

    public boolean exists(Long id) {
        return recordsData.containsKey(id);
    }
}
