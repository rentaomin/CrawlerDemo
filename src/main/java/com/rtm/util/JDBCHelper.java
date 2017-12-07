package com.rtm.util;

import java.util.HashMap;

import org.apache.commons.dbcp.BasicDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

public class JDBCHelper {
	private static final String DEFAUL_TMYSQL_JDBCTEMPLATE = "mysql";
	private static final String MYSQL_DRIVERCLASS ="com.mysql.jdbc.Driver";
	private static final String MYSQL_URL = "jdbc:mysql://localhost:3306/test?useUnicode=true&characterEncoding=utf8";
	private static final String USERNAME = "root";
	private static final String PASSWORD = "root";
	private static final int INITIALSIZE  = 1;
	private static final int INITMAXACTIVE = 5;

		public static HashMap<String,JdbcTemplate> templateMap = new HashMap<String,JdbcTemplate>(); 
		
		/**
		 *  ��ȡjdbcTemplateʵ��
		 * @param temlateName
		 * @param url mysql������
		 * @param userName �û���
		 * @param password ����
		 * @param initialSize ��ʼ����С
		 * @param maxActive ����̳߳�
		 * @return
		 */
		public static JdbcTemplate createMysqlTempalte() {
				BasicDataSource dataSource = new BasicDataSource();
				dataSource.setDriverClassName(MYSQL_DRIVERCLASS);
				dataSource.setUrl(MYSQL_URL);
				dataSource.setUsername(USERNAME);
				dataSource.setPassword(PASSWORD);
				dataSource.setInitialSize(INITIALSIZE);
				dataSource.setMaxActive(INITMAXACTIVE);
		        JdbcTemplate template = new JdbcTemplate(dataSource);
		        templateMap.put(DEFAUL_TMYSQL_JDBCTEMPLATE, template);
		        return template;	
		}
		
		 public static JdbcTemplate getJdbcTemplate(String templateName){
		        return templateMap.get(templateName);
		    }

}
