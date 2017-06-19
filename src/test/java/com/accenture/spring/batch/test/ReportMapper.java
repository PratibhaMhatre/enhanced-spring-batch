package com.accenture.spring.batch.test;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

public class ReportMapper implements RowMapper<Report> {

	@Override
    public Report mapRow(ResultSet rs, int rowNum) throws SQLException {
    	Report report = new Report();
    	report.setRefId(rs.getInt("refid"));
    	report.setName(rs.getString("name"));
    	report.setAge(rs.getInt("age"));
    	report.setIncome(rs.getString("income"));
             
        return report;
    }
}
