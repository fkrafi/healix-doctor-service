package com.healix.doctorservice.repository;

import com.healix.doctorservice.model.Doctor;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DoctorRepository extends MongoRepository<Doctor, String> {
    @Query("{ $text: { $search: ?0 } }")
    List<Doctor> searchByText(String text);
}
