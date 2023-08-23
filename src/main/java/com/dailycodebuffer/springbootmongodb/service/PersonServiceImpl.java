package com.dailycodebuffer.springbootmongodb.service;

import com.dailycodebuffer.springbootmongodb.collection.Person;
import com.dailycodebuffer.springbootmongodb.repository.PersonRepository;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.*;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.support.PageableExecutionUtils;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Service
public class PersonServiceImpl implements PersonService{

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private MongoTemplate mongoTemplate;
    @Override
    public String save(Person person) {
        return personRepository.save(person).getPersonId();
    }

    @Override
    public List<Person> getPersonStartWith(String name) {
        return personRepository.findByFirstNameStartsWith(name);
    }

   @Override
    public Optional<Person> getPersonByPersonId(String id) {
        UUID uuid = UUID.fromString(id); // Convert the String to a UUID
        return personRepository.findById(uuid); // Pass the UUID to findById    
    }

    @Override
    public void delete(String id) {
        UUID uuid = UUID.fromString(id);
        personRepository.deleteById(uuid);
    }

    @Override
    public List<Person> getByPersonAge(Integer minAge, Integer maxAge) {
        return personRepository.findPersonByAgeBetween(minAge,maxAge);
    }

    @Override
    public Page<Person> search(String name, Integer minAge, Integer maxAge, String city, Pageable pageable) {

        Query query = new Query().with(pageable);
        List<Criteria> criteria = new ArrayList<>();

        if(name !=null && !name.isEmpty()) {
            criteria.add(Criteria.where("firstName").regex(name,"i"));
        }

        if(minAge !=null && maxAge !=null) {
            criteria.add(Criteria.where("age").gte(minAge).lte(maxAge));
        }

        if(city !=null && !city.isEmpty()) {
            criteria.add(Criteria.where("addresses.city").is(city));
        }

        if(!criteria.isEmpty()) {
            query.addCriteria(new Criteria()
                    .andOperator(criteria.toArray(new Criteria[0])));
        }

        Page<Person> people = PageableExecutionUtils.getPage(
                mongoTemplate.find(query, Person.class
                ), pageable, () -> mongoTemplate.count(query.skip(0).limit(0),Person.class));
        return people;
    }

    @Override
    public List<Document> getOldestPersonByCity() {
        UnwindOperation unwindOperation
                = Aggregation.unwind("addresses");
        SortOperation sortOperation
                = Aggregation.sort(Sort.Direction.DESC,"age");
        GroupOperation groupOperation
                = Aggregation.group("addresses.city")
                .first(Aggregation.ROOT)
                .as("oldestPerson");

        Aggregation aggregation
                = Aggregation.newAggregation(unwindOperation,sortOperation,groupOperation);

        List<Document> person
                = mongoTemplate.aggregate(aggregation, Person.class,Document.class).getMappedResults();
        return person;
    }

    @Override
    public List<Document> getPopulationByCity() {

        UnwindOperation unwindOperation
                = Aggregation.unwind("addresses");
        GroupOperation groupOperation
                = Aggregation.group("addresses.city")
                .count().as("popCount");
        SortOperation sortOperation
                = Aggregation.sort(Sort.Direction.DESC, "popCount");

        ProjectionOperation projectionOperation
                = Aggregation.project()
                .andExpression("_id").as("city")
                .andExpression("popCount").as("count")
                .andExclude("_id");

        Aggregation aggregation
                = Aggregation.newAggregation(unwindOperation,groupOperation,sortOperation,projectionOperation);

        List<Document> documents
                = mongoTemplate.aggregate(aggregation,
                Person.class,
                Document.class).getMappedResults();
        return  documents;
    }

    @Override
    public List<Person> findPersons(Map<String, Object> queryParams, Map<String, Integer> projectionMap) {
        Query query = new Query();

        // Adding query parameters
        for (Map.Entry<String, Object> param : queryParams.entrySet()) {
            query.addCriteria(Criteria.where(param.getKey()).is(param.getValue()));
        }

        // Adding projection fields
        for (Map.Entry<String, Integer> entry : projectionMap.entrySet()) {
            String field = entry.getKey();
            Integer value = entry.getValue();

            if ("_id".equals(field) && value == 0) {
                query.fields().exclude("_id");
            } else if (value == 1) {
                query.fields().include(field);
            }
        }

        return mongoTemplate.find(query, Person.class);
    }

}
