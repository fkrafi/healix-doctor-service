package com.healix.doctorservice.service;

import com.healix.doctorservice.model.Doctor;
import com.healix.doctorservice.dto.DoctorSearchResult;
import com.healix.doctorservice.dto.SearchResponse;
import org.bson.Document;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.aggregation.FacetOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class DoctorService {
    public DoctorSearchResult searchDoctors(String query, Map<String, List<String>> filters, int page, int size, String sortBy, String sortDir) {
        List<AggregationOperation> operations = new ArrayList<>();

        // Atlas $search fulltext (only if query is not null/empty)
        if (query != null && !query.trim().isEmpty()) {
            Document search = new Document("index", "default")
                    .append("text", new Document("query", query)
                            .append("path", Arrays.asList("name", "departments", "locations", "nationality", "languages", "introduction", "areas_of_expertise", "experience")));
            operations.add(context -> new Document("$search", search));
        }

        // Filtering by facets
        if (filters != null && !filters.isEmpty()) {
            List<Criteria> criteriaList = new ArrayList<>();
            filters.forEach((key, values) -> {
                if (values != null && !values.isEmpty()) {
                    criteriaList.add(Criteria.where(key).in(values));
                }
            });
            if (!criteriaList.isEmpty()) {
                operations.add(Aggregation.match(new Criteria().andOperator(criteriaList.toArray(new Criteria[0]))));
            }
        }

        // Sorting
        if (sortBy != null && !sortBy.isEmpty()) {
            AggregationOperation sortOp = Aggregation.sort("desc".equalsIgnoreCase(sortDir) ? org.springframework.data.domain.Sort.Direction.DESC : org.springframework.data.domain.Sort.Direction.ASC, sortBy);
            operations.add(sortOp);
        }

        // Facet aggregation for department, locations, nationality, languages (before pagination)
        FacetOperation facetOp = Aggregation.facet(
                Aggregation.unwind("$departments", true),
                Aggregation.group("$departments").count().as("count")
        ).as("departments")
         .and(
                Aggregation.unwind("$locations", true),
                Aggregation.group("$locations").count().as("count")
        ).as("locations")
         .and(
                Aggregation.unwind("$nationality", true),
                Aggregation.group("$nationality").count().as("count")
        ).as("nationality")
         .and(
                Aggregation.unwind("$languages", true),
                Aggregation.group("$languages").count().as("count")
        ).as("languages");

        // Build two pipelines: one for facets, one for paged data
        List<AggregationOperation> facetPipeline = new ArrayList<>(operations);
        facetPipeline.add(facetOp);

        List<AggregationOperation> pagePipeline = new ArrayList<>(operations);
        int skip = Math.max(page, 0) * Math.max(size, 1);
        pagePipeline.add(Aggregation.skip(skip));
        pagePipeline.add(Aggregation.limit(size));

        // Run facet aggregation
        Aggregation facetAggregation = Aggregation.newAggregation(facetPipeline);
        AggregationResults<Document> facetResults = mongoTemplate.aggregate(facetAggregation, "doctors", Document.class);
        List<Document> facetMappedResults = facetResults.getMappedResults();

        // Run paged data aggregation
        Aggregation pageAggregation = Aggregation.newAggregation(pagePipeline);
        List<Doctor> doctors = mongoTemplate.aggregate(pageAggregation, "doctors", Doctor.class).getMappedResults();

        Map<String, Map<String, Long>> facets = new HashMap<>();
        if (!facetMappedResults.isEmpty()) {
            Document doc = facetMappedResults.get(0);
            for (String facet : Arrays.asList("departments", "locations", "nationality", "languages")) {
                Object facetObj = doc.get(facet);
                Map<String, Long> facetMap = new HashMap<>();
                if (facetObj instanceof List<?>) {
                    @SuppressWarnings("unchecked")
                    List<Document> facetList = (List<Document>) facetObj;
                    for (Document f : facetList) {
                        String key = Objects.toString(f.get("_id"), "");
                        Object countObj = f.get("count");
                        Long count = (countObj instanceof Number number) ? number.longValue() : null;
                        facetMap.put(key, count);
                    }
                }
                facets.put(facet, facetMap);
            }
        }
        return new DoctorSearchResult(doctors, facets);
    }
    private final MongoTemplate mongoTemplate;

    public DoctorService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public SearchResponse<Doctor> searchDoctors(String query, Pageable pageable, Map<String, String> allParams) {
        // Extract facet filters (department, locations, nationality, languages)
        Map<String, List<String>> filters = new HashMap<>();
        for (String facet : Arrays.asList("departments", "locations", "nationality", "languages")) {
            if (allParams != null && allParams.containsKey(facet)) {
                String[] vals = allParams.get(facet).split(",");
                filters.put(facet, Arrays.asList(vals));
            }
        }
        DoctorSearchResult result = searchDoctors(
            query,
            filters,
            pageable.getPageNumber(),
            pageable.getPageSize(),
            pageable.getSort().isSorted() ? pageable.getSort().iterator().next().getProperty() : null,
            pageable.getSort().isSorted() ? pageable.getSort().iterator().next().getDirection().name().toLowerCase() : "asc"
        );
        // Compose meta info (for OpenSearch/JSON-API style)
        Map<String, Object> meta = new HashMap<>();
        meta.put("query", query);
        meta.put("filters", filters);
        meta.put("page", pageable.getPageNumber());
        meta.put("size", pageable.getPageSize());
        meta.put("sort", pageable.getSort().toString());
        meta.put("total", result.getDoctors().size());
        return new SearchResponse<>(result.getDoctors(), result.getFacets(), meta);
    }
    // ...existing code...
}
