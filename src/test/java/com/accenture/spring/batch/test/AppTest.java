package com.accenture.spring.batch.test;


import java.lang.reflect.InvocationTargetException;

import com.accenture.spring.batch.transform.Annotate;

/**
 * Unit test for simple App.
 */
public class AppTest {
	
	/*@Autowired
	Annotate annotate;*/
	
	
  
	public static void main (String[] ar){
		
		Employee employee = new Employee();
		employee.setName("Shri\"tu");
		employee.setEmail("    hdghsgds ksjf    ");
		
		/*ApplicationContext context = new ClassPathXmlApplicationContext("applicationContext.xml");
		Annotate annotate = (Annotate) context.getBean("annotate");*/
		Annotate annotate = new Annotate();
		try {
			System.out.println("Employee is "+employee);
			employee =(Employee) annotate.transform(employee);
			System.out.println("Employee is "+employee);
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
	}
}
