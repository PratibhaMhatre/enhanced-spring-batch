package com.accenture.spring.batch.test;

import org.springframework.stereotype.Component;

import com.accenture.spring.batch.annotation.ReplaceQuoteWithSpace;
import com.accenture.spring.batch.annotation.Transform;
import com.accenture.spring.batch.annotation.Trim;

@Component
@Transform
public class Report {

	
	private int refId;
	
	@ReplaceQuoteWithSpace
	private String name;
	
	private int age;
	/* private String dob; */
	@Trim
	private String income;

	public int getRefId() {
		return refId;
	}

	public void setRefId(int refId) {
		this.refId = refId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getAge() {
		return age;
	}

	public void setAge(int age) {
		this.age = age;
	}

	/*
	 * public String getDob() { return dob; }
	 * 
	 * public void setDob(String dob) { this.dob = dob; }
	 */

	public String getIncome() {
		return income;
	}

	public void setIncome(String income) {
		this.income = income;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return refId + "," + name + ", " + age + "," + income;
	}

}
