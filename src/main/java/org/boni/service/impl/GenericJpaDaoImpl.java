package org.boni.service.impl;

import java.io.Serializable;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.boni.service.GenericDao;



/**
 * JPA implementation of the GenericRepository using Hibernate as the Basis.
 * 
 * @author Boni Gopalan
 * @param <T>
 *            The persistent type
 * @param <ID>
 *            The primary key type
 */
public class GenericJpaDaoImpl<T, ID extends Serializable> implements GenericDao<T, ID>{

	public GenericJpaDaoImpl(final Class<T> persistentClass, Class<ID> idClass){
		this(persistentClass);
	}
	// ~ Instance fields
	// --------------------------------------------------------

	private final Class<T> persistentClass;
	
	@PersistenceContext(unitName = "arquilianPU")
	protected EntityManager entityManager;

	// ~ Constructors
	// -----------------------------------------------------------


	public GenericJpaDaoImpl(final Class<T> persistentClass) {
		super();
		this.persistentClass = persistentClass;
	}
	
	/* (non-Javadoc)
	 * @see org.boni.test.service.GenericDao#save(T)
	 */
	public T save(T entity){
		return merge(entity);
	}
	
	/* (non-Javadoc)
	 * @see org.boni.test.service.GenericDao#merge(T)
	 */
	public T merge(T entity){
		return entityManager.merge(entity);
	}
	
	/* (non-Javadoc)
	 * @see org.boni.test.service.GenericDao#delete(T)
	 */
	public void delete(T entity){
		entityManager.remove(entity);
	}
	
	/* (non-Javadoc)
	 * @see org.boni.test.service.GenericDao#findById(ID)
	 */
	public T findById(ID id){
		return entityManager.find(persistentClass, id);
	}
	
	/* (non-Javadoc)
	 * @see org.boni.test.service.GenericDao#finadAll()
	 */
	public List<T> finadAll(){
		Query q = entityManager.createQuery("SELECT P FROM " + persistentClass.getSimpleName() + " P");
		return (List<T>)q.getResultList();
	}
	
	public void setEntityManager(EntityManager emf){
		this.entityManager = emf;
	}
	
}