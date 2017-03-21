/*
 * Created for Innovation.
 * 
 */
package com.accenture.spring.batch.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Converts String To Timestamp 
 * It takes input as String and converts it to
 * Timestamp and stores it in a variable which is passed in the parameter value.
 * 
 * 
 * @author aparna.satpathy,shruti.mukesh.sethia
 * @param value
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface StringToTimestamp {
	String value1();
}
