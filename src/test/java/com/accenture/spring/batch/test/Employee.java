/*
 * Created for Innovation.
 * 
 */
package com.accenture.spring.batch.test;

import java.sql.Timestamp;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.accenture.spring.batch.annotation.ReplaceQuoteWithSpace;
import com.accenture.spring.batch.annotation.StringToDate;
import com.accenture.spring.batch.annotation.StringToTimestamp;
import com.accenture.spring.batch.annotation.Transform;
import com.accenture.spring.batch.annotation.Trim;

/**
 * POJO class
 * Contains Getter and Setter of fields
 * 
 * @author aparna.satpathy,shruti.mukesh.sethia
 * 
 */
@Component
@Transform
public class Employee {

	@ReplaceQuoteWithSpace
	private String name;

	@Trim
	private String email;

	@StringToDate(value = "dtePayDate")
	private String payDate;

	private Date dtePayDate;
	
	@StringToTimestamp(value1 = "dteTimestamp")
	private String payTimestamp;

	private Timestamp dteTimestamp;

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

	/**
	 * @return the payDate
	 */
	public String getPayDate() {
		return payDate;
	}

	/**
	 * @param payDate
	 *            the payDate to set
	 */
	public void setPayDate(String payDate) {
		this.payDate = payDate;
	}

	/**
	 * @return the dtePayDate
	 */
	public Date getDtePayDate() {
		return dtePayDate;
	}

	/**
	 * @param dtePayDate
	 *            the dtePayDate to set
	 */
	public void setDtePayDate(Date dtePayDate) {
		this.dtePayDate = dtePayDate;
	}

	/**
	 * @return the payTimestamp
	 */
	public String getPayTimestamp() {
		return payTimestamp;
	}

	/**
	 * @param payTimestamp the payTimestamp to set
	 */
	public void setPayTimestamp(String payTimestamp) {
		this.payTimestamp = payTimestamp;
	}

	
	/**
	 * @return the dteTimestamp
	 */
	public Timestamp getDteTimestamp() {
		return dteTimestamp;
	}

	/**
	 * @param dteTimestamp the dteTimestamp to set
	 */
	public void setDteTimestamp(Timestamp dteTimestamp) {
		this.dteTimestamp = dteTimestamp;
	}

	@Override
	public String toString() {
		return "Employee [name=" + name + ", email=" + email + ", dtePayDate=" + dtePayDate + ", dteTimestamp=" + dteTimestamp + "]";
	}

}
