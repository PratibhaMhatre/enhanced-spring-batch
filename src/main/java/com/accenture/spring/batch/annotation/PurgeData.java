package com.accenture.spring.batch.annotation;
/**
 * It purges all the files older than the time(days) given in parameter purgeDuration. 
 * 
 * @param archiveDir,purgeDuration,filename,regexpression
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
