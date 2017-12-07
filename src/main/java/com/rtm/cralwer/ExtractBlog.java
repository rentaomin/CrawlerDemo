package com.rtm.cralwer;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import com.rtm.util.JDBCHelper;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;

/***
 * 爬取CSDN博客
 * 1、去除title为空的url
 * 2、去除url相似或title相同的博客
 * 3、查询博客内容的大小
 * 4、查询包含关键字的url
 */
public class ExtractBlog extends  BreadthCrawler{

	public ExtractBlog(String crawlPath, boolean autoParse) {
		super(crawlPath, autoParse);
		this.addSeed("https://www.csdn.net/");
		this.addRegex("http://blog.csdn.net/.*/article/details/.*");
		setThreads(50);
		this.setConf(getConf());
		getConf().setTopN(50);
	}

	public void visit(Page page, CrawlDatums next) {
		String url = page.url();
		JdbcTemplate jdbcTemplate = null;
		if (url.matches("http://blog.csdn.net/.*/article/details/.*")) {
			String title = page.select(".csdn_top").text();
			String content = page.select("#article_content").text();
			if (StringUtils.isNotEmpty(title) && StringUtils.isNotEmpty(content)) {
				jdbcTemplate = JDBCHelper.createMysqlTempalte();
				String sql = "insert into  t_content(id,titile,html,url) values (?,?,?,?)";
				jdbcTemplate.update(sql, new Object[] {null,title,content,url});
				System.out.println("当前访问的地址为："+url +"\n"+"博客标题："+title);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		ExtractBlog extractBlog = new ExtractBlog("crawl",true);
		//设置断点，历史记录
		//extractBlog.setResumable(true);
		//爬取网页的深度
		extractBlog.start(10);
	}
}
