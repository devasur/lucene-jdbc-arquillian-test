package org.boni.domain;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQuery;

@Entity
@NamedQuery(name = "findAllPersonsByCountry", query = "select t from Person t where t.country = :country")
public class Person implements Serializable {
 
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String name;
    private String country;
    @Lob
    private ArrayList<String> hobbies;
    public Person(){}
    public Person(final String name, final String country, final String[] hobbies){
    	setName(name);
    	setCountry(country);
    	if (null != hobbies){
    		setHobbies(new ArrayList<String>(){{
    			for (String aHobby : hobbies){
    				add(aHobby);
    			}
    		}});
    	}
    }
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public ArrayList<String> getHobbies() {
		return hobbies;
	}
	public void setHobbies(ArrayList<String> hobbies) {
		this.hobbies = hobbies;
	}
	
    
}