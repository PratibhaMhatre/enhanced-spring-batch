
package com.accenture.spring.batch.annotation;
/**
 * It allows you to use annotations(PurgeData,CopyFile,DeleteFile,MoveFile,RenameFile)
 * 
 * @author Shruti Sethia
 * 
 */
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface FileTransfer {

}
