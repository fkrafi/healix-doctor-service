
package com.healix.doctorservice.dto;

import com.healix.doctorservice.model.Doctor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DoctorSearchResult {
    private List<Doctor> doctors;
    private Map<String, Map<String, Long>> facets;

    public DoctorSearchResult() {}

    public DoctorSearchResult(List<Doctor> doctors, Map<String, Map<String, Long>> facets) {
        this.doctors = doctors;
        this.facets = facets;
    }

    public List<Doctor> getDoctors() {
        return doctors;
    }

    public Map<String, Map<String, Long>> getFacets() {
        return facets;
    }
}
