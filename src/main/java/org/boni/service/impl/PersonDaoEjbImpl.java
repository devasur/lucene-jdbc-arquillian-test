package org.boni.service.impl;

import javax.ejb.Stateless;

import org.boni.domain.Person;
import org.boni.service.PersonDao;

@Stateless
public class PersonDaoEjbImpl extends GenericJpaDaoImpl<Person, Long> implements PersonDao {
 
    public PersonDaoEjbImpl() {
		super(Person.class);
	}

}