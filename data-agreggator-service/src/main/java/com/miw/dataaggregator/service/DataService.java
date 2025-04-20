package com.miw.dataaggregator.service;

import com.miw.dataaggregator.model.DataInput;
import com.miw.dataaggregator.model.DataItem;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class DataService {
    private final Map<String, DataItem> dataStore = new ConcurrentHashMap<>();

    public DataItem getDataById(String id) {
        return dataStore.get(id);
    }

    public List<DataItem> getAllData() {
        return new ArrayList<>(dataStore.values());
    }

    public DataItem createData(DataInput input) {
        String id = UUID.randomUUID().toString();
        DataItem dataItem = DataItem.builder()
                .id(id)
                .name(input.getName())
                .value(input.getValue())
                .source(input.getSource())
                .timestamp(LocalDateTime.now())
                .build();
        
        dataStore.put(id, dataItem);
        return dataItem;
    }

    public DataItem updateData(String id, DataInput input) {
        DataItem existingItem = dataStore.get(id);
        if (existingItem == null) {
            return null;
        }

        existingItem.setName(input.getName());
        existingItem.setValue(input.getValue());
        existingItem.setSource(input.getSource());
        existingItem.setTimestamp(LocalDateTime.now());
        
        dataStore.put(id, existingItem);
        return existingItem;
    }

    public boolean deleteData(String id) {
        return dataStore.remove(id) != null;
    }
}
