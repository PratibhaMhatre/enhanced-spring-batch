package com.accenture.spring.batch.annotation;
/**
 * It moves the all the files from source to destination which satisfies the regular expression. 
 * 
 * @param source,destination,filename, regexpression
 */
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MoveFile {

	String source();
	String destination();
	String filename();
	String regexpression();

}
