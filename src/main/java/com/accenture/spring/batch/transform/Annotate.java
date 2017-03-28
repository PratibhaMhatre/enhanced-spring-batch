/*
 * Created for Innovation.
 * 
 */
package com.accenture.spring.batch.transform;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.accenture.spring.batch.annotation.CopyFile;
import com.accenture.spring.batch.annotation.DeleteFile;
import com.accenture.spring.batch.annotation.FileTransfer;
import com.accenture.spring.batch.annotation.MoveFile;
import com.accenture.spring.batch.annotation.PurgeData;
import com.accenture.spring.batch.annotation.RenameFile;
import com.accenture.spring.batch.annotation.ReplaceQuoteWithSpace;
import com.accenture.spring.batch.annotation.StringToDate;
import com.accenture.spring.batch.annotation.StringToTimestamp;
import com.accenture.spring.batch.annotation.Transform;
import com.accenture.spring.batch.annotation.Trim;
import com.accenture.spring.batch.utils.FileUtils;

/**
 * Implementation of Annotated Fields
 * 
 * @author aparna.satpathy,shruti.mukesh.sethia
 * 
 */
@Component
public class Annotate {

	private ApplicationContext context;

	public Object transform(Object obj) throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {

		context = new ClassPathXmlApplicationContext("applicationContext.xml");

		final Map<String, Object> annotatedClasses = context.getBeansWithAnnotation(Transform.class);

		for (final Object annotatedClass : annotatedClasses.values()) {

			Method[] methods = annotatedClass.getClass().getDeclaredMethods();

			Map<String, Method> getterMap = new HashMap<String, Method>();
			Map<String, Method> setterMap = new HashMap<String, Method>();
			Field[] fields = annotatedClass.getClass().getDeclaredFields();

			for (Method method : methods) {
				for (Field field : fields) {
					
					String methodName=method.getName();
					String fieldName=field.getName();

					if (methodName.equalsIgnoreCase("get"+fieldName)) {
						getterMap.put(field.getName(), method);
					}
					if (methodName.equalsIgnoreCase("set"+fieldName)) {
						setterMap.put(field.getName(), method);
					}
				}
			}

			for (Field field : fields) {

				Annotation[] annotations = field.getDeclaredAnnotations();

				for (Annotation annotation : annotations) {
					String name = field.getName();
					Method getMethod = getterMap.get(name);
					Method setMethod = setterMap.get(name);
					if (annotation instanceof Trim) {

						String value = (String) getMethod.invoke(obj);
						setMethod.invoke(obj, StringUtils.trimToEmpty(value));
					} else if (annotation instanceof ReplaceQuoteWithSpace) {
						String value = (String) getMethod.invoke(obj);
						setMethod.invoke(obj, StringUtils.replace(value, "\"", " "));
					} else if (annotation instanceof StringToDate) {
						String value = (String) getMethod.invoke(obj);
						System.out.println("value: " + value);
						Date formattedDate = null;

						SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
						if (value == null || "".equals(value)) {
							return null;
						}
						try {

							formattedDate = formatter.parse(value);
							System.out.println("formattedDate: " + formattedDate);

						} catch (ParseException e) {
							System.out.println("INVALID_DATE_VALUE");
						}
						String dateName = ((StringToDate) annotation).value();
						System.out.println("DateName: " + dateName);

						Method setMethod1 = setterMap.get(dateName);
						System.out.println("setMethod1: " + setMethod1);
						setMethod1.invoke(obj, formattedDate);

					} else if (annotation instanceof StringToTimestamp) {
						String value = (String) getMethod.invoke(obj);
						System.out.println("value1: " + value);
						SimpleDateFormat dateFormat = new SimpleDateFormat(
								"yyyy-MM-dd HH:mm:ss.SSS");
						Date parsedDate = null;
						Timestamp timestamp = null;
						if (StringUtils.isBlank(value)) {
							return null;
						}
						try {
							parsedDate = dateFormat.parse(value);
							timestamp = new java.sql.Timestamp(parsedDate.getTime());
						} catch (ParseException e) {
							System.out.println("INVALID_DATE_VALUE");
						}
						String timestampName = ((StringToTimestamp) annotation).value();
						System.out.println("timestamp: " + timestampName);

						Method setMethod1 = setterMap.get(timestampName);
						System.out.println("setMethod1: " + setMethod1);
						setMethod1.invoke(obj, timestamp);
						

					}
				}

			}

		}
		return obj;

	}
	
	public Object fileTransfer(Object obj) throws IllegalAccessException, IllegalArgumentException,
	InvocationTargetException, NoSuchMethodException, SecurityException {

context = new ClassPathXmlApplicationContext("applicationContext.xml");

final Map<String, Object> annotatedClasses = context.getBeansWithAnnotation(FileTransfer.class);

for (final Object annotatedClass : annotatedClasses.values()) {

	
	Field[] fields = annotatedClass.getClass().getDeclaredFields();

	
	

	for (Field field : fields) {

		Annotation[] annotations = field.getDeclaredAnnotations();

		for (Annotation annotation : annotations) {
			String name = field.getName();
		System.out.println("Name: "+name );
			if (annotation instanceof MoveFile) {
				String sourcePath = ((MoveFile) annotation).source();
				String destinationPath = ((MoveFile) annotation).destination();
				String fileName = ((MoveFile) annotation).filename();
				String extention = ((MoveFile) annotation).regexpression();
				System.out.println("sourcePath: "+sourcePath );
				System.out.println("destinationPath: "+destinationPath );
				System.out.println("fileName: "+fileName );
				System.out.println("extention: "+extention );
				
				FileUtils.moveFiles(sourcePath, destinationPath, extention);
				
			}
			else if(annotation instanceof CopyFile){
				String sourcePath = ((CopyFile) annotation).source();
				String destinationPath = ((CopyFile) annotation).destination();
				String fileName = ((CopyFile) annotation).filename();
				String extention = ((CopyFile) annotation).regexpression();
				System.out.println("sourcePath: "+sourcePath );
				System.out.println("destinationPath: "+destinationPath );
				System.out.println("fileName: "+fileName );
				System.out.println("extention: "+extention );
				
				FileUtils.copyFiles(sourcePath, destinationPath, extention);
				
			}
			else if(annotation instanceof DeleteFile){
				String sourcePath = ((DeleteFile) annotation).source();
				String fileName = ((DeleteFile) annotation).filename();
				String extention = ((DeleteFile) annotation).regexpression();
				System.out.println("sourcePath: "+sourcePath );
				System.out.println("fileName: "+fileName );
				System.out.println("extention: "+extention );
				
				FileUtils.deleteFiles(sourcePath, extention);
				
			}
			else if(annotation instanceof RenameFile){
				String sourcePath = ((RenameFile) annotation).source();
				String fileName = ((RenameFile) annotation).filename();
				String rename =((RenameFile) annotation).rename();
				String extention = ((RenameFile) annotation).regexpression();
				System.out.println("sourcePath: "+sourcePath );
				System.out.println("fileName: "+fileName );
				System.out.println("rename: "+rename );
				System.out.println("extention: "+extention );
				
				FileUtils.renameFiles(sourcePath,rename, extention);
				
			}
			else if(annotation instanceof PurgeData){
				String archivePath = ((PurgeData) annotation).archiveDir();
				String fileName = ((PurgeData) annotation).filename();
				int purgeDuration =((PurgeData) annotation).purgeDuration();
				String extention = ((PurgeData) annotation).regexpression();
				System.out.println("archivePath: "+archivePath );
				System.out.println("fileName: "+fileName );
				System.out.println("purgeDuration: "+purgeDuration );
				System.out.println("extention: "+extention );
				
				FileUtils.purgeNdayOldFiles(archivePath,extention, purgeDuration);
				
			}
		}

	}

}
return obj;

}

}
