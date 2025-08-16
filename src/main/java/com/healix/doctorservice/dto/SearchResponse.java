package com.healix.doctorservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
public class SearchResponse<T> {
    private List<T> data;
    private Map<String, Map<String, Long>> facets;
    private Map<String, Object> meta;

    public SearchResponse(List<T> data, Map<String, Map<String, Long>> facets, Map<String, Object> meta) {
        this.data = data;
        this.facets = facets;
        this.meta = meta;
    }
}
