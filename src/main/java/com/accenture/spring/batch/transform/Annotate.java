package com.accenture.spring.batch.transform;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

import com.accenture.spring.batch.annotation.ReplaceQuoteWithSpace;
import com.accenture.spring.batch.annotation.Transform;
import com.accenture.spring.batch.annotation.Trim;

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

					if (StringUtils.containsIgnoreCase(method.getName(), field.getName())
							&& method.getName().startsWith("get")) {
						getterMap.put(field.getName(), method);
					}
					if (StringUtils.containsIgnoreCase(method.getName(), field.getName())
							&& method.getName().startsWith("set")) {
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
					}
				}

			}

		}
		return obj;

	}

}
