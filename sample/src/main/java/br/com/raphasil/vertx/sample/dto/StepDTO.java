package br.com.raphasil.vertx.sample.dto;

import java.io.Serializable;
import java.util.Date;

public class StepDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -8530625032371732815L;

	private long id;
	
	private String message;
	
	private Date date;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
}
