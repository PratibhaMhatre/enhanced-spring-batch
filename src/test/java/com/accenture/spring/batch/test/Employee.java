package com.accenture.spring.batch.test;

import java.util.Date;

import org.springframework.stereotype.Component;

import com.accenture.spring.batch.annotation.ReplaceQuoteWithSpace;
import com.accenture.spring.batch.annotation.Transform;
import com.accenture.spring.batch.annotation.Trim;

@Component
@Transform
public class Employee {

	@ReplaceQuoteWithSpace
	private String name;

	@Trim
	private String email;
	
	
	private String payDate;
	
	private Date dtePayDate;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	@Override
	public String toString() {
		return "Employee [name=" + name + ", email=" + email + "]";
	}
	
	

}
