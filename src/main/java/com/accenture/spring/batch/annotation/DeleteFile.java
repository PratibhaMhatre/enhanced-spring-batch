package com.accenture.spring.batch.annotation;
/**
 * It deletes the source files from the source which satisfies the regular expression
 * 
 * @param source,filename,regexpression
 */
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface DeleteFile {
	String source();
	String filename();
	String regexpression();


}
