package org.boni.domain;

import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.Table;

@Entity
@Table(name="SEARCH_INDEX")
public class SearchIndex {
	@Id
	@Column(name="NAME_")
	String name;
	@Lob
	@Column(name="VALUE_")
	Serializable value;
	@Column(name="SIZE_")
	Integer size;
	@Column(name="LF_")
	Timestamp timeStamp;
	@Column(name="DELETED_")
	String deleted;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Serializable getValue() {
		return value;
	}
	public void setValue(Serializable value) {
		this.value = value;
	}
	public Integer getSize() {
		return size;
	}
	public void setSize(Integer size) {
		this.size = size;
	}
	public Timestamp getTimeStamp() {
		return timeStamp;
	}
	public void setTimeStamp(Timestamp timeStamp) {
		this.timeStamp = timeStamp;
	}
	public String getDeleted() {
		return deleted;
	}
	public void setDeleted(String deleted) {
		this.deleted = deleted;
	}
}
