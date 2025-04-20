package com.miw.dataaggregator.controller;

import com.miw.dataaggregator.model.DataInput;
import com.miw.dataaggregator.model.DataItem;
import com.miw.dataaggregator.service.DataService;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.stereotype.Controller;

import java.util.List;

@Controller
public class DataController {
    
    private final DataService dataService;
    
    public DataController(DataService dataService) {
        this.dataService = dataService;
    }
    
    @QueryMapping
    public DataItem dataById(@Argument String id) {
        return dataService.getDataById(id);
    }
    
    @QueryMapping
    public List<DataItem> allData() {
        return dataService.getAllData();
    }
    
    @MutationMapping
    public DataItem createData(@Argument DataInput input) {
        return dataService.createData(input);
    }
    
    @MutationMapping
    public DataItem updateData(@Argument String id, @Argument DataInput input) {
        return dataService.updateData(id, input);
    }
    
    @MutationMapping
    public Boolean deleteData(@Argument String id) {
        return dataService.deleteData(id);
    }
}
