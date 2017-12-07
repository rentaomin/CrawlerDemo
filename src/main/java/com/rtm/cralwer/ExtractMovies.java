package com.rtm.cralwer;

import java.io.IOException;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.net.HttpRequest;
import cn.edu.hfut.dmic.webcollector.net.HttpResponse;
import cn.edu.hfut.dmic.webcollector.net.Proxys;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;

public class ExtractMovies extends BreadthCrawler {

	public ExtractMovies(String crawlPath, boolean autoParse) throws IOException {
		super(crawlPath, autoParse);
		this.addSeed("http://www.dygang.net/");
		this.addRegex("http://www.dygang.net/(.*)");
		this.setThreads(20);
		this.setConf(getConf());
		getConf().setTopN(100);
	}
	
	//��д������ʵ��ͨ����������
	@Override
	public Page getResponse(CrawlDatum crawlDatum) throws Exception {
		Proxys proxys=new Proxys();
		//proxys.add("proxy.piccnet.com.cn", 3128);
		proxys.add("61.135.217.7",80);
	   HttpRequest request = new HttpRequest(crawlDatum);
	   request.setProxy(proxys.nextRandom());
	   return request.responsePage();
	}
	
	public void visit(Page page, CrawlDatums next) {
		String url = page.url();
		//http://www.dygang.net/gy/20171017/38770.htm
		//"."���ʾ��\n֮��������ַ���".?"�Ǿ������ٵ�ƥ�䣬"*" ��ʾƥ��0-���� +��ʾƥ��1-����
		String title = page.select("title").text();
		String[] str = null;
		if (page.matchUrl(" http://www.dygang.net/(.*) ")) {
			List<Element> links = page.select("table tbody tr td[ bgcolor=\"#ffffbb\"]");
			if (links != null && links.size()>0) {
				System.out.println("��ǰ���ʵĵ�Ӱ��ַ��"+url);
				System.out.println("��Ӱ���ƣ�"+title);
				for (Element e :links) {
					String loadUrl = e.select("a").attr("href");
					String loadText = e.select("a").text();
					if (loadUrl.startsWith("magnet:?xt")) {
						System.out.println("�������أ�");
					}
					if (loadUrl.startsWith("ed2k://")) {
						System.out.println("��¿��ַ");
					}
					if (loadUrl.startsWith("http://pan")) {
						System.out.println("�ٶ����̣�");
						if (loadText.contains("���룺")) {
							String password = loadText.split("��")[str.length-1];
							System.out.println("���룺"+password);
						}
					}
					if (loadUrl.endsWith(".torrent")) {
						System.out.println("�������ص�ַ��"+loadUrl);
					}
					
				  }
			} else {
				System.out.println("�����ص�ַ");
			}
			
		}
	}
	
	public static void main(String[] args) throws Exception {
		ExtractMovies extractMovies = new ExtractMovies("cralwer", true);
		extractMovies.start(20);
	}

}
