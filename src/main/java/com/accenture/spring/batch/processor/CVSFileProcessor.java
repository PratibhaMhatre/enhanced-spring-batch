package com.accenture.spring.batch.processor;

import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.accenture.spring.batch.test.Report;
import com.accenture.spring.batch.transform.Annotate;

@Component(value = "cvsFileProcessor")
public class CVSFileProcessor implements
ItemProcessor<Report,Object>{
	
	@Autowired
	Annotate annotate;

	@Override
	public Object process(Report item) throws Exception {
		System.out.println("Item:: "+item);
		Object o = annotate.transform(item);
		System.out.println(o);
		// TODO Auto-generated method stub
		return o;
	}

}
