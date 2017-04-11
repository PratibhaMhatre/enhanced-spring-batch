package com.accenture.spring.batch.annotation;

/**
 * Copies the file To the requested destination path 
 * 
 * 
 * @author Shruti Sethia
 * @param source,destination,filename,regexpression
 */
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface CopyFile {
	String source();
	String destination();
	String filename();
	String regexpression();

}
