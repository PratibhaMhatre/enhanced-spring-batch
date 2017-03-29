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
 * Converts utilDate To SqlDate 
 * It takes input as utilDate and converts it to SqlDate and
 * stores it in a variable which is passed in the parameter value.
 * 
 * 
 * @author aparna.satpathy
 * @param value
 */
@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface UtilDateToSqlDate {
	String value();
}
