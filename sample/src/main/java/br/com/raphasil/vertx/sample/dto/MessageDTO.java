package br.com.raphasil.vertx.sample.dto;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class MessageDTO implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 6819174993514137786L;
	
	private Date start;
	private Date finish;
	private List<StepDTO> steps;
	private String result;
	
	public Date getStart() {
		return start;
	}
	public void setStart(Date start) {
		this.start = start;
	}
	public Date getFinish() {
		return finish;
	}
	public void setFinish(Date finish) {
		this.finish = finish;
	}
	public List<StepDTO> getSteps() {
		return steps;
	}
	public void setSteps(List<StepDTO> steps) {
		this.steps = steps;
	}
	public String getResult() {
		return result;
	}
	public void setResult(String result) {
		this.result = result;
	}
	
	
}
