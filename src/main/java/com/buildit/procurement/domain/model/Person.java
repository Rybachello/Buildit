package com.buildit.procurement.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by stepan on 27/03/2017.
 */
@Entity
@Getter
@AllArgsConstructor
public class Person {
    @Id
    String id;
    String name;
}
