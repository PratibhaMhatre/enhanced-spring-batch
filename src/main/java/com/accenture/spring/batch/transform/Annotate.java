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
import org.springframework.stereotype.Component;

import com.accenture.spring.batch.annotation.CopyFile;
import com.accenture.spring.batch.annotation.DeleteFile;
import com.accenture.spring.batch.annotation.MoveFile;
import com.accenture.spring.batch.annotation.PurgeData;
import com.accenture.spring.batch.annotation.RenameFile;
import com.accenture.spring.batch.annotation.ReplaceQuoteWithSpace;
import com.accenture.spring.batch.annotation.StringToDate;
import com.accenture.spring.batch.annotation.StringToTimestamp;
import com.accenture.spring.batch.annotation.Trim;
import com.accenture.spring.batch.annotation.UtilDateToSqlDate;
import com.accenture.spring.batch.annotation.UtilDateToSqlTimestamp;
import com.accenture.spring.batch.util.FileUtils;

/**
 * Implementation of Annotated Fields
 * 
 * @author aparna.satpathy,shruti.mukesh.sethia
 * 
 */
@Component
public class Annotate {

	public Object transform(Object obj) throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {

		Method[] methods = obj.getClass().getDeclaredMethods();

		Map<String, Method> getterMap = new HashMap<String, Method>();
		Map<String, Method> setterMap = new HashMap<String, Method>();
		Field[] fields = obj.getClass().getDeclaredFields();

		for (Method method : methods) {
			for (Field field : fields) {

				String methodName = method.getName();
				String fieldName = field.getName();

				if (methodName.equalsIgnoreCase("get" + fieldName)) {
					getterMap.put(field.getName(), method);
				}
				if (methodName.equalsIgnoreCase("set" + fieldName)) {
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
					Date formattedDate = null;

					SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
					if (value == null || "".equals(value)) {
						return null;
					}
					try {

						formattedDate = formatter.parse(value);

					} catch (ParseException e) {
					}
					String dateName = ((StringToDate) annotation).value();

					Method setMethod1 = setterMap.get(dateName);
					setMethod1.invoke(obj, formattedDate);

				} else if (annotation instanceof StringToTimestamp) {
					String value = (String) getMethod.invoke(obj);
					SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
					Date parsedDate = null;
					Timestamp timestamp = null;
					if (StringUtils.isBlank(value)) {
						return null;
					}
					try {
						parsedDate = dateFormat.parse(value);
						timestamp = new java.sql.Timestamp(parsedDate.getTime());
					} catch (ParseException e) {
					}
					String timestampName = ((StringToTimestamp) annotation).value();

					Method setMethod2 = setterMap.get(timestampName);
					setMethod2.invoke(obj, timestamp);

				} else if (annotation instanceof UtilDateToSqlDate) {
					java.sql.Date sqlDate = null;
					Date value = (Date) getMethod.invoke(obj);
					if (value != null) {
						sqlDate = new java.sql.Date(value.getTime());
					}
					String utilDateName = ((UtilDateToSqlDate) annotation).value();
					Method setMethod3 = setterMap.get(utilDateName);
					setMethod3.invoke(obj, sqlDate);

				} else if (annotation instanceof UtilDateToSqlTimestamp) {
					Timestamp sqlTimestamp = null;
					Date value = (Date) getMethod.invoke(obj);
					if (value != null) {
						sqlTimestamp = new java.sql.Timestamp(value.getTime());

					}
					String utilTimestampName = ((UtilDateToSqlTimestamp) annotation).value();
					Method setMethod4 = setterMap.get(utilTimestampName);
					setMethod4.invoke(obj, sqlTimestamp);
				}
			}
		}
		return obj;

	}

	public Object fileTransfer(Object obj) throws IllegalAccessException, IllegalArgumentException,
			InvocationTargetException, NoSuchMethodException, SecurityException {

		Field[] fields = obj.getClass().getDeclaredFields();

		for (Field field : fields) {

			Annotation[] annotations = field.getDeclaredAnnotations();

			for (Annotation annotation : annotations) {
				// String name = field.getName();
				if (annotation instanceof MoveFile) {
					String sourcePath = ((MoveFile) annotation).source();
					String destinationPath = ((MoveFile) annotation).destination();
					String extention = ((MoveFile) annotation).regexpression();

					FileUtils.moveFiles(sourcePath, destinationPath, extention);

				} else if (annotation instanceof CopyFile) {
					String sourcePath = ((CopyFile) annotation).source();
					String destinationPath = ((CopyFile) annotation).destination();
					String extention = ((CopyFile) annotation).regexpression();

					FileUtils.copyFiles(sourcePath, destinationPath, extention);

				} else if (annotation instanceof DeleteFile) {
					String sourcePath = ((DeleteFile) annotation).source();
					String extention = ((DeleteFile) annotation).regexpression();

					FileUtils.deleteFiles(sourcePath, extention);

				} else if (annotation instanceof RenameFile) {
					String sourcePath = ((RenameFile) annotation).source();
					String rename = ((RenameFile) annotation).rename();
					String extention = ((RenameFile) annotation).regexpression();

					FileUtils.renameFiles(sourcePath, rename, extention);

				} else if (annotation instanceof PurgeData) {
					String archivePath = ((PurgeData) annotation).archiveDir();
					int purgeDuration = ((PurgeData) annotation).purgeDuration();
					String extention = ((PurgeData) annotation).regexpression();

					FileUtils.purgeNdayOldFiles(archivePath, extention, purgeDuration);

				}
			}

		}

		// }
		return obj;

	}

}
