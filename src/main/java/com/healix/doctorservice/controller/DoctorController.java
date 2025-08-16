package com.healix.doctorservice.controller;

import com.healix.doctorservice.model.Doctor;
import com.healix.doctorservice.service.DoctorService;
import com.healix.doctorservice.dto.SearchResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/doctors")
public class DoctorController {
    private final DoctorService doctorService;

    public DoctorController(DoctorService doctorService) {
        this.doctorService = doctorService;
    }

    /**
     * Generic JSON-API/OpenSearch style endpoint for doctor search with fulltext, facets, and filter support.
     * Example: /doctors/search?query=smith&department=Cardiology&locations=NYC&nationality=US
     */
    @GetMapping("/search")
    public SearchResponse<Doctor> searchDoctors(
            @RequestParam(required = false, defaultValue = "") String query,
            @PageableDefault(size = 20) Pageable pageable,
            @RequestParam(required = false) Map<String, String> allParams
    ) {
        return doctorService.searchDoctors(query, pageable, allParams);
    }
}
