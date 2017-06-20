/*
 * Created for Innovation.
 * 
 */
package com.accenture.spring.batch.test;

import java.lang.reflect.InvocationTargetException;
import java.util.Date;

import com.accenture.spring.batch.transform.Annotate;

/**
 * Creates the object of POJO class and calls the Annotate class
 * 
 * @author aparna.satpathy
 * 
 */
public class AppTest {

	/*
	 * @Autowired Annotate annotate;
	 */

/*	public static void main(String[] ar) {

		Employee employee = new Employee();
		employee.setName("Shri\"tu");
		employee.setEmail("    hdghsgds ksjf    ");
		employee.setPayDate("2017-03-21");
		employee.setPayTimestamp("2017-03-21 17:50:10.123");
		employee.setUtilDate(new Date());
		employee.setUtilTimestampDate(new Date());

		
		 * ApplicationContext context = new
		 * ClassPathXmlApplicationContext("applicationContext.xml"); Annotate
		 * annotate = (Annotate) context.getBean("annotate");
		 
		Annotate annotate = new Annotate();
		try {
			employee = (Employee) annotate.transform(employee);
			employee = (Employee) annotate.fileTransfer(employee);
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		}

	}*/
}
