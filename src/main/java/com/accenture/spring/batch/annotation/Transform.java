/*
 * Created for Innovation.
 * 
 */
package com.accenture.spring.batch.annotation;
/**
 * @author Shruti Sethia
 * 
 */
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Class level Annotation. 
 * Determines Class has used Custom Annotated fields
 * 
 */
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface Transform {

}
