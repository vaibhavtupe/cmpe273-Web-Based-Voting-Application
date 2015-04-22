package demo;

import org.hibernate.validator.constraints.NotEmpty;



public class Poll {
	
	private String poll_id;
	
	@NotEmpty(message="poll question can not be empty")
	private String question;
	
	@NotEmpty(message="please enter poll start time")
	private String started_at;
	
	@NotEmpty(message="please enter poll end time")
	private String expired_at;
	private String[] choice;
	//private int [] results;
	
	
	
	public String getId() {
		return poll_id;
	}
	public void setId(String poll_id) {
		this.poll_id = poll_id;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getStarted_at() {
		return started_at;
	}
	public void setStarted_at(String started_at) {
		this.started_at = started_at;
	}
	public String getExpired_at() {
		return expired_at;
	}
	public void setExpired_at(String expired_at) {
		this.expired_at = expired_at;
	}
	public String[] getChoice() {
		return choice;
	}
	public void setChoice(String[] choice) {
		this.choice = choice;
	}
	/*
	public int[] getResults() {
		return results;
	}
	public void setResults(int[] results) {
		this.results = results;
	}
	
	*/
	
	
	
	
	

}
 