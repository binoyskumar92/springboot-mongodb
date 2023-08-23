package com.dailycodebuffer.springbootmongodb.collection;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@Document(collection = "person")
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Person {

    @Id
    @Getter(AccessLevel.NONE) // Exclude the default getter
    @Setter(AccessLevel.NONE) // Exclude the default setter
    private UUID personId;
    private String firstName;
    private String lastName;
    private Integer age;
    private List<String> hobbies;
    private List<Address> addresses;

    // Custom getter for personId that returns it as a String
    public String getPersonId() {
        return personId != null ? personId.toString() : null;
    }

    // Custom setter for personId that accepts a String and converts it to a UUID
    public void setPersonId(String personId) {
        this.personId = personId != null ? UUID.fromString(personId) : null;
    }
}
