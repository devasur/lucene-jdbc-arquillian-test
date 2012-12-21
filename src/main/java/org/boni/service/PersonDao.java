package org.boni.service;

import javax.ejb.Local;

import org.boni.domain.Person;


@Local
public interface PersonDao extends GenericDao<Person, Long> {}