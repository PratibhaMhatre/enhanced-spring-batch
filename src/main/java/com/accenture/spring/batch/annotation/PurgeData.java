package com.accenture.spring.batch.annotation;
/**
 * @author Shruti Sethia
 * 
 */
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface PurgeData {
	String archiveDir();
	int purgeDuration();
	String filename();
	String regexpression();


}