package com.accenture.spring.batch.test;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionException;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class App {
	public static void main(String[] args) {


		ApplicationContext context = new ClassPathXmlApplicationContext("context.xml");

		 Job job = (Job) context.getBean("reportJob");
		//Job job = (Job) context.getBean("reportJob1");
		
		try {

			JobLauncher jobLauncher = (JobLauncher) context.getBean("jobLauncher");
			JobExecution execution = jobLauncher.run(job, new JobParameters());
			
			System.out.println(execution.getAllFailureExceptions());

		} catch (JobExecutionException e1) {
			e1.printStackTrace();
		} finally {
			((AbstractApplicationContext) context).close();
		}

	}
}