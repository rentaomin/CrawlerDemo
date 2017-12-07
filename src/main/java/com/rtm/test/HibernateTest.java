package com.rtm.test;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.rtm.entity.User;
import com.rtm.util.HibernateUtil;

public class HibernateTest {
	
	public static void main(String[] args) {
		/*		 Session session = HibernateUtil.getSession();  
	     
		 Transaction tx = session.beginTransaction();  
	     for(int i = 0;i<10;i++) {
	    	 User user = new User();
	    	 user.setName("李四"+i);
	    	 user.setPassword("root"+i);
	    	 session.save(user);
	     }
	     try {
	    	 List list = session.createNativeQuery("select u.name from t_user u where u.name like '%张三%'").list();
	    	 for (Object str:list) {
	    		 System.out.println(str);
	    	 }
			//User u = session.find(User.class, 1);
			 tx.commit();
			 System.out.println("保存成功!");
		} catch (Exception e) {
			  tx.rollback();  
			e.printStackTrace();
			System.out.println("保存失败");
		} finally{
			HibernateUtil.closeSession();
		}*/
		
		/**
		 * 测试使用连接池每次打开关闭session 提交事务是否会断开连接，报错
		 */
	     for(int i = 0;i<10000;i++) {
	    	 Session session = HibernateUtil.getSession(); 
	    	 Transaction tx = session.beginTransaction();  
	    	 User user = new User();
	    	 user.setName("test"+i);
	    	 user.setPassword("root"+i);
	    	 
	    	 try {
				session.save(user);
				 tx.commit();
			} catch (Exception e) {
				tx.rollback();  
				e.printStackTrace();
			} finally{
				HibernateUtil.closeSession();
			}
	     }
	}
}
