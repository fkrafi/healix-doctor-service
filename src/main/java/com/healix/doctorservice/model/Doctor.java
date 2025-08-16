package com.healix.doctorservice.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.Date;
import java.util.List;

@Document(collection = "doctors")
@Data
public class Doctor {
    @Id
    private String id;

    @TextIndexed
    private String name;

    private List<String> departments;

    private List<String> locations;

    private String nationality;

    private List<String> languages;

    private String introduction;

    private List<String> areas_of_expertise;

    private List<String> experience;

    private String avatar;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date createdAt;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date updatedAt;
}
