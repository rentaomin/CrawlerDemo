package com.rtm.cralwer;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.jsoup.nodes.Element;

import com.rtm.entity.Movie;
import com.rtm.util.HibernateUtil;

import cn.edu.hfut.dmic.webcollector.model.CrawlDatum;
import cn.edu.hfut.dmic.webcollector.model.CrawlDatums;
import cn.edu.hfut.dmic.webcollector.model.Page;
import cn.edu.hfut.dmic.webcollector.net.HttpRequest;
import cn.edu.hfut.dmic.webcollector.net.Proxys;
import cn.edu.hfut.dmic.webcollector.plugin.berkeley.BreadthCrawler;

/*	
 *爬取电影岗网站的电影下载地址
 *默认编码为gbk 
 *正则 "."点表示除\n之外的任意字符，".?"是尽可能少的匹配，"*" 表示匹配0-无穷 +表示匹配1-无穷
 */
public class ExtractMovies extends BreadthCrawler {
	
	private static final Logger logger = Logger.getLogger(ExtractMovies.class);  
	
	//多线程中共用这个变量，用于存放爬取的电影信息
	private  Vector<Movie> movieVector = new Vector<Movie>();

	public ExtractMovies(String crawlPath, boolean autoParse) throws IOException {
		super(crawlPath, autoParse);
		this.addSeed("http://www.dygang.net/");
		//+为满足正面正则 - 为反面正则 ，webCollector默认必须满足一个正面正则，否则不能爬虫
		this.addRegex("+http://www.dygang.net/.*");
		this.setThreads(10);
		this.setConf(getConf());
		getConf().setTopN(100);
	}
	
	//重写方法，实现通过代理爬虫
	@Override
	public Page getResponse(CrawlDatum crawlDatum) throws Exception {
		Proxys proxys=new Proxys();
		//proxys.add("proxy.piccnet.com.cn", 3128);
		proxys.add("183.172.131.147",8118);
		proxys.add("61.4.184.180",3128);
		proxys.add("218.241.234.48",8080);
	   HttpRequest request = new HttpRequest(crawlDatum);
	   request.setProxy(proxys.nextRandom());
	   return request.responsePage();
	}
	
	
	public void visit(Page page, CrawlDatums next) {
		String url = page.url();
		//获取电影名称
		String movieName = page.select("title").text();
		if (StringUtils.isNotEmpty(movieName)) {
			movieName = movieName.split("_")[0];
		}
		logger.info("当前访问的地址为："+url+" 电影名称为："+movieName);
		if (page.matchUrl("http://www.dygang.net/.*")) {
			List<Element> links = page.select("table tbody tr td[ bgcolor=\"#ffffbb\"]");
			 if (links != null && links.size() > 0) {
				 Vector<Movie> mvVector = this.extractMovieLoadUrlInfoByLinksHtml(links,url,movieName);
				 this.saveExtractorMovieInfoWithSynchronized(mvVector);
			 }
		}
	}
	
	/**
	 * 采用同步机制保存爬取的电影信息
	 * @param mvVector 爬取的需要保存的电影信息
	 */
	private synchronized void  saveExtractorMovieInfoWithSynchronized(Vector<Movie> mvVector) {
		int movieVectorSize = mvVector.size();
		System.out.println("准备进行保存操作1：共："+movieVectorSize);
		if ( mvVector != null && movieVectorSize > 100) {
			Session session = HibernateUtil.getSession();  
			Transaction tx = session.beginTransaction();
	 	    int movieVectorLen = this.movieVector.size();
	 	   System.out.println("准备进行保存操作2：共："+movieVectorLen);
	 	    logger.info("开启事务，准备保存数据，此次保存的数据共："+movieVectorLen+"条数据");
			try {
				if (movieVectorLen > 0) {
					for (int i = 0;i < movieVectorSize;i++) {
						Movie entity = this.movieVector.get(i);
						if (entity != null) {
							session.save(entity);
						}
						tx.commit();
						logger.info("事务提交成功，成功保存数据"+movieVectorSize);
					}
					System.out.println("事务提交成功，成功保存数据2"+movieVectorLen);
					System.out.println("事务提交成功，成功保存数据1"+movieVectorSize);
				}
			} catch (Exception e) {
				logger.error("数据保存失败"+e);
			} finally {
				HibernateUtil.closeSession();
				logger.info("关闭session成功!");
			}
			this.movieVector = new Vector<Movie>();
			logger.info("清楚之前保存的数据,清空之后的大小为："+this.movieVector.size());
		}		
	}

	/**
	 * 从提取的下载html获取电影下载信息
	 * @param links 获取的电影下载table的html内容
	 * @param title  电影名称
	 * @param url 电影在线地址
	 */
	private Vector<Movie> extractMovieLoadUrlInfoByLinksHtml(List<Element> links, String url, String title) {
		if (links != null && links.size()>0) {
			StringBuffer magnetSb = new StringBuffer();
			StringBuffer ed2kSb = new StringBuffer();
			StringBuffer torrentSb = new StringBuffer();
			String type  = "";
			for (Element e :links) {
				String loadUrl = e.select("a").attr("href");
				String loadText = e.select("a").text();
				String[] typeArr = loadText.split("\\.");
				if (typeArr !=null && typeArr.length>0) {
						type = typeArr[typeArr.length-1];
				}
				if ( loadUrl.startsWith("magnet:?xt") ) {
					magnetSb = this.extractMovieFiledsValueByDownloadHref(loadUrl, magnetSb);
				} else if (loadUrl.startsWith("ed2k://")) {
					ed2kSb = this.extractMovieFiledsValueByDownloadHref(loadUrl, ed2kSb);
				} else if (loadUrl.endsWith(".torrent")) {
					torrentSb = this.extractMovieFiledsValueByDownloadHref(loadUrl, torrentSb);
				}
			  }
			if (magnetSb.length()>0 || ed2kSb.length()>0 || torrentSb.length()>0)  {
				Movie movie = new Movie();
				movie.setMovieName(title);
				movie.setMovieUrl(url);
				movie.setMovieType(type);
				movie.setEd2kUrl(ed2kSb.toString());
				movie.setMagnetUrl(magnetSb.toString());
				movie.setTorrentUrl(torrentSb.toString());
				movie.setInsertTime(new Date());
				if(!this.movieVector.contains(movie)){
					this.movieVector.add(movie);
					logger.info("电影保存到vector成功，大小为："+this.movieVector.size());
				}
				this.clearStringBuffer(ed2kSb,magnetSb,torrentSb);
			}
		} else {
			logger.info("当前访问的地址为："+url+"电影名称为："+title+"没有找到下载地址");
		}
		return this.movieVector;
	}
	
	
	/**
	 * 根据下载地址拼接对应属性的值
	 * @param url 获取到的下载链接
	 * @param fields 电影的属性字段的值
	 * @return 对应电影属性的下载地址值
	 */
	private StringBuffer extractMovieFiledsValueByDownloadHref(String url,StringBuffer fields) {
		if ( StringUtils.isNotEmpty(url) ) {
				fields = this.appendSplitCharacter(fields);
				fields.append(url);
		}
		return fields;
	}
	
	
	
	/**
	 * 拼接分隔符
	 * @param sb StringBuffer 要拼接的StringBuffer字符串
	 * @return 拼接后的StringBuffer字符串
	 */
	private StringBuffer appendSplitCharacter(StringBuffer sb) {
		if (sb != null && sb.length() > 0) {
			sb.append("-->");
		}
		return sb;
	}
	
	/**
	 * 清空定义存储电影信息字段的StringBuffer变量
	 * @param args 传入的StringBuffer类型的参数
	 */
	private void clearStringBuffer(StringBuffer ...args) {
		if ( args != null && args.length > 0) {
			for (StringBuffer sb:args) {
				if (sb != null && sb.length() > 0) {
					sb.setLength(0);
				}
			}
		}
	} 
	
	
	public static void main(String[] args) throws Exception {
		//传入的test为框架默认爬取的文件保存名称
		ExtractMovies extractMovies = new ExtractMovies("cralwer", true);
		//爬取网页的深度
		extractMovies.start(10);
	}

	
	public Vector<Movie> getMovieVector() {
		return movieVector;
	}

	public void setMovieVector(Vector<Movie> movieVector) {
		this.movieVector = movieVector;
	}

		
}
