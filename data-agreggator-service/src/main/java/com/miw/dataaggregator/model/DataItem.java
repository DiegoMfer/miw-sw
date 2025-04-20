package com.miw.dataaggregator.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DataItem {
    private String id;
    private String name;
    private String value;
    private LocalDateTime timestamp;
    private String source;
}
