/*
 * Copyright (C) 2015 hu
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package com.rtm.cralwer;

import java.util.Date;
import java.util.Vector;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jsoup.select.Elements;
import com.rtm.entity.Blog;
import com.rtm.util.HibernateUtil;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.net.HttpRequest;
import cn.edu.hfut.dmic.webcollector.net.Proxys;
import cn.edu.hfut.dmic.webcollector.plugin.ram.RamCrawler;



/**
 * ��ȡcsdn������Ϣ
 * @author Dell
 *
 */
public class ExtractCsdnBlog extends RamCrawler {

	private static final Logger logger = Logger.getLogger(ExtractCsdnBlog.class);
	
	//ÿ�ν��б������ݿ�ļ���
	private Vector<Blog> blogVector = new Vector<Blog>();
	
	//���������ȡ��Ҫ�������
	private Vector<Blog> totalBlogVector = new Vector<Blog>();
	
	
	/**
	 * ��������
	 */
	@Override
	public Page getResponse(CrawlDatum crawlDatum) throws Exception {
		Proxys proxys=new Proxys();
		proxys.add("proxy.piccnet.com.cn", 3128);
		/*proxys.add("183.172.131.147",8118);
		proxys.add("61.4.184.180",3128);
		proxys.add("218.241.234.48",8080);*/
	   HttpRequest request = new HttpRequest(crawlDatum);
	   request.setProxy(proxys.nextRandom());
	  int code =  request.response().code();
	  Page page = null;
	  if ( code == 200 || code == 544) {
		  System.out.println("��Ӧ״̬��"+code);
		  page = request.responsePage();
	  } else{
		  page = null;
		  System.out.println("����ʧ�ܣ�");
	  }
	  return page;
	}
	
	@Override
    public void visit(Page page, CrawlDatums next) {
		if (page.matchUrl("http://blog.csdn.net/.*")) {
			logger.info("������ȡ����ҳ�棺");
			String title = page.select("title").first().text();
			logger.info("��ǰ���ʵĵ�ַΪ:"+page.url()+"�������⣺"+title);
			String author = page.select(".right_box.user_info dl dd h3 a").text();
			String  blogCatogory = page.select("div[class=article_bar clearfix] ul[data-mod] li a").text();
			logger.info("�������ߣ�"+author+""+"���ͷ��ࣺ"+blogCatogory);
			Elements  userInfo = page.select("div[class=inf_number_box clearfix] dl dd");
			String originNum  = "";
			String fans = "";
			String likes = "";
			String yun = "";
			if (userInfo.size() > 0) {
				 originNum  = userInfo.get(0).text();
				 fans = userInfo.get(1).text();
				 likes = userInfo.get(2).text();
				  yun =  userInfo.get(3).text();
			}
			String  read = page.select("div[class=article_bar clearfix] ul[class=right_bar] li button span ").text();
			logger.info("����ԭ��������"+originNum+"��˿����:"+fans+"ϲ��������"+likes+"���ƣ�"+yun+"�Ķ�����"+read);
			String content = page.select("#article_content").text();
			next.add(page.links("div[class=hotarticls] ul li a"));
			
			if (StringUtils.isNotEmpty(content)) {
				Blog blog = new Blog();
				blog.setTitle(title);
				blog.setAuthor(author);
				blog.setBrowseNum(read);
				blog.setOriginalNum(originNum);
				blog.setFansNum(fans);
				blog.setLikeNum(likes);
				blog.setMaYun(yun);
				blog.setBlogCatagory(blogCatogory);
				blog.setBlogContent(content);
				blog.setBlogUrl(page.url());
				blog.setInsertTime(new Date());
				blog.setOperateTime(new Date());
				logger.info("blogVectorȥ��ǰ��"+this.blogVector.size());
				System.out.println("��ǰ�ܹ���"+this.blogVector.size());
				if (!this.totalBlogVector.contains(blog) ) {
					this.totalBlogVector.add(blog);
					if  ( !this.blogVector.contains(blog) ) {
						this.blogVector.add(blog);
						System.out.println("ȥ�غ󱣴�����ݣ�"+this.blogVector.size());
					}
					logger.info("ȥ�غ�"+this.blogVector.size());
					System.out.println("==========");
					this.saveBlogInfoWithSynchronized(this.blogVector);
				}
			}
		}
    }
	
	
	/**
	 * ���沩����Ϣ
	 * 
	 */
    private synchronized void saveBlogInfoWithSynchronized(Vector<Blog> blogVec) {
    	int blogLen = blogVec.size();
    	logger.info("����ǰ���ݣ�"+blogLen);
    	if (blogVec != null && blogLen > 200) {
    		Session session = HibernateUtil.getSession();  
			Transaction tx = session.beginTransaction();
			try {
				if (blogLen > 0) {
					for (int i = 0;i < blogLen;i++) {
						Blog entity = blogVec.get(i);
						if ( entity != null && StringUtils.isNotEmpty(entity.getBlogContent() )) {
							session.save(entity);
						}
					}
					tx.commit();
					logger.info("�����ύ�ɹ����ɹ��������ݺ�"+blogLen);
					System.out.println("�����ύ�ɹ����ɹ��������ݺ�"+blogLen);
				}
			} catch (Exception e) {
				logger.info("����ʧ�ܣ�"+blogLen);
				e.printStackTrace();
			} finally {
				HibernateUtil.closeSession();
				this.blogVector = new Vector<Blog>();
				logger.info("�ر�session"+"blogVector���������Ϊ��"+this.blogVector.size()+"�ܹ����ݣ�"+this.totalBlogVector.size());
			}
    	}
	}

	public static void main(String[] args) throws Exception {
        ExtractCsdnBlog crawler = new ExtractCsdnBlog();
        crawler.addSeed("http://blog.csdn.net/");
        crawler.addRegex("http://blog.csdn.net/.*");
        crawler.setThreads(30);
        crawler.start(50);
    }

	public Vector<Blog> getBlogVector() {
		return blogVector;
	}

	public void setBlogVector(Vector<Blog> blogVector) {
		this.blogVector = blogVector;
	}

	public Vector<Blog> getTotalBlogVector() {
		return totalBlogVector;
	}

	public void setTotalBlogVector(Vector<Blog> totalBlogVector) {
		this.totalBlogVector = totalBlogVector;
	}
	
	
}
