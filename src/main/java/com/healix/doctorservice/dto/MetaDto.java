package com.healix.doctorservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetaDto {
    private String query;
    private Map<String, java.util.List<String>> filters;
    private int total;
}
