package com.accenture.spring.batch.annotation;
/**
 * It renames the file with the name given in parameter rename.
 * 
 * @author Shruti Sethia
 * @param source,filename,regexpression,rename
 */
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.FIELD })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RenameFile {
	
	String source();
	String filename();
	String regexpression();
	String rename();

}
