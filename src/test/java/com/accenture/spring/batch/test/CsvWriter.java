package com.accenture.spring.batch.test;

import java.util.List;

import org.springframework.batch.item.ItemWriter;

public class CsvWriter<T> implements ItemWriter<T>{

	@Override
	public void write(List<? extends T> items) throws Exception {
		System.out.println(items);
		
	}

	

}
