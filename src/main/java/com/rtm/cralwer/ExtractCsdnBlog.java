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
 * 爬取csdn博客信息
 * @author Dell
 *
 */
public class ExtractCsdnBlog extends RamCrawler {

	private static final Logger logger = Logger.getLogger(ExtractCsdnBlog.class);
	
	//每次进行保存数据库的集合
	private Vector<Blog> blogVector = new Vector<Blog>();
	
	//存放所有爬取需要存的数据
	private Vector<Blog> totalBlogVector = new Vector<Blog>();
	
	
	/**
	 * 代理设置
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
		  System.out.println("响应状态："+code);
		  page = request.responsePage();
	  } else{
		  page = null;
		  System.out.println("请求失败！");
	  }
	  return page;
	}
	
	@Override
    public void visit(Page page, CrawlDatums next) {
		if (page.matchUrl("http://blog.csdn.net/.*")) {
			logger.info("进入爬取数据页面：");
			String title = page.select("title").first().text();
			logger.info("当前访问的地址为:"+page.url()+"博客主题："+title);
			String author = page.select(".right_box.user_info dl dd h3 a").text();
			String  blogCatogory = page.select("div[class=article_bar clearfix] ul[data-mod] li a").text();
			logger.info("博客作者："+author+""+"博客分类："+blogCatogory);
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
			logger.info("博客原创数量："+originNum+"粉丝数量:"+fans+"喜欢人数："+likes+"码云："+yun+"阅读量："+read);
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
				logger.info("blogVector去重前："+this.blogVector.size());
				System.out.println("当前总共："+this.blogVector.size());
				if (!this.totalBlogVector.contains(blog) ) {
					this.totalBlogVector.add(blog);
					if  ( !this.blogVector.contains(blog) ) {
						this.blogVector.add(blog);
						System.out.println("去重后保存的数据："+this.blogVector.size());
					}
					logger.info("去重后："+this.blogVector.size());
					System.out.println("==========");
					this.saveBlogInfoWithSynchronized(this.blogVector);
				}
			}
		}
    }
	
	
	/**
	 * 保存博客信息
	 * 
	 */
    private synchronized void saveBlogInfoWithSynchronized(Vector<Blog> blogVec) {
    	int blogLen = blogVec.size();
    	logger.info("保存前数据："+blogLen);
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
					logger.info("事务提交成功，成功保存数据后："+blogLen);
					System.out.println("事务提交成功，成功保存数据后："+blogLen);
				}
			} catch (Exception e) {
				logger.info("保存失败："+blogLen);
				e.printStackTrace();
			} finally {
				HibernateUtil.closeSession();
				this.blogVector = new Vector<Blog>();
				logger.info("关闭session"+"blogVector清楚后数据为："+this.blogVector.size()+"总共数据："+this.totalBlogVector.size());
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
