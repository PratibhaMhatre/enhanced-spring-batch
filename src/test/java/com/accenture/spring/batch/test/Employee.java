/*
 * Created for Innovation.
 * 
 */
package com.accenture.spring.batch.test;

import java.sql.Timestamp;
import java.util.Date;

import org.springframework.stereotype.Component;

import com.accenture.spring.batch.annotation.CopyFile;
import com.accenture.spring.batch.annotation.DeleteFile;
import com.accenture.spring.batch.annotation.FileTransfer;
import com.accenture.spring.batch.annotation.MoveFile;
import com.accenture.spring.batch.annotation.PurgeData;
import com.accenture.spring.batch.annotation.RenameFile;
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
@FileTransfer
public class Employee {

	@ReplaceQuoteWithSpace
	private String name;

	@Trim
	private String email;

	@StringToDate(value = "dtePayDate")
	private String payDate;

	private Date dtePayDate;
	
	@StringToTimestamp(value = "dteTimestamp")
	private String payTimestamp;

	private Timestamp dteTimestamp;
	
	@MoveFile(source="C:\\Users\\shruti.mukesh.sethia\\FileMovementTest\\Move\\File1\\", destination="C:\\Users\\shruti.mukesh.sethia\\FileMovementTest\\Move\\File2\\" ,filename="abc" ,regexpression="*.txt")
	private String moveList;
	
	@CopyFile(source="C:\\Users\\shruti.mukesh.sethia\\FileMovementTest\\Copy\\File1\\", destination="C:\\Users\\shruti.mukesh.sethia\\FileMovementTest\\Copy\\File2\\" ,filename="abc" ,regexpression="*.txt")
	private String copyList;
	
	@DeleteFile(source="C:\\Users\\shruti.mukesh.sethia\\FileMovementTest\\Delete\\File1\\",filename="abc" ,regexpression="*.txt")
	private String delList;
	
	@RenameFile(source="C:\\Users\\shruti.mukesh.sethia\\FileMovementTest\\Rename\\File1\\",filename="abc", rename="pqr" ,regexpression="*.txt")
	private String renameList;
	
	@PurgeData(archiveDir="C:\\Users\\shruti.mukesh.sethia\\FileMovementTest\\Purge\\File1\\",filename="abc", purgeDuration=1 ,regexpression="*.txt")
	private String purgeList;


	public String getName() {
		return name;
	}

	/**
	 * @return the moveList
	 */
	public String getMoveList() {
		return moveList;
	}

	/**
	 * @param moveList the moveList to set
	 */
	public void setMoveList(String moveList) {
		this.moveList = moveList;
	}

	/**
	 * @return the copyList
	 */
	public String getCopyList() {
		return copyList;
	}

	/**
	 * @param copyList the copyList to set
	 */
	public void setCopyList(String copyList) {
		this.copyList = copyList;
	}

	/**
	 * @return the delList
	 */
	public String getDelList() {
		return delList;
	}

	/**
	 * @param delList the delList to set
	 */
	public void setDelList(String delList) {
		this.delList = delList;
	}

	/**
	 * @return the renameList
	 */
	public String getRenameList() {
		return renameList;
	}

	/**
	 * @param renameList the renameList to set
	 */
	public void setRenameList(String renameList) {
		this.renameList = renameList;
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
