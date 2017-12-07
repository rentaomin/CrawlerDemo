package com.rtm.cralwer;

import org.apache.commons.lang3.StringUtils;
import org.springframework.jdbc.core.JdbcTemplate;
import com.rtm.util.JDBCHelper;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;

/***
 * ��ȡCSDN����
 * 1��ȥ��titleΪ�յ�url
 * 2��ȥ��url���ƻ�title��ͬ�Ĳ���
 * 3����ѯ�������ݵĴ�С
 * 4����ѯ�����ؼ��ֵ�url
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
				System.out.println("��ǰ���ʵĵ�ַΪ��"+url +"\n"+"���ͱ��⣺"+title);
			}
		}
	}

	public static void main(String[] args) throws Exception {
		ExtractBlog extractBlog = new ExtractBlog("crawl",true);
		//���öϵ㣬��ʷ��¼
		//extractBlog.setResumable(true);
		//��ȡ��ҳ�����
		extractBlog.start(10);
	}
}
