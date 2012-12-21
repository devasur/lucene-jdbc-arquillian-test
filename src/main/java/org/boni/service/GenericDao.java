package org.boni.service;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;

public interface GenericDao<T, ID extends Serializable> {

	public abstract T save(T entity);

	public abstract T merge(T entity);

	public abstract void delete(T entity);

	public abstract T findById(ID id);

	public abstract List<T> finadAll();
	
	public void setEntityManager(EntityManager emf);

}