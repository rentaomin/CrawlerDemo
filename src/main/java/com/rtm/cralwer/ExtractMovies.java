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
 *��ȡ��Ӱ����վ�ĵ�Ӱ���ص�ַ
 *Ĭ�ϱ���Ϊgbk 
 *���� "."���ʾ��\n֮��������ַ���".?"�Ǿ������ٵ�ƥ�䣬"*" ��ʾƥ��0-���� +��ʾƥ��1-����
 */
public class ExtractMovies extends BreadthCrawler {
	
	private static final Logger logger = Logger.getLogger(ExtractMovies.class);  
	
	//���߳��й���������������ڴ����ȡ�ĵ�Ӱ��Ϣ
	private  Vector<Movie> movieVector = new Vector<Movie>();

	public ExtractMovies(String crawlPath, boolean autoParse) throws IOException {
		super(crawlPath, autoParse);
		this.addSeed("http://www.dygang.net/");
		//+Ϊ������������ - Ϊ�������� ��webCollectorĬ�ϱ�������һ���������򣬷���������
		this.addRegex("+http://www.dygang.net/.*");
		this.setThreads(10);
		this.setConf(getConf());
		getConf().setTopN(100);
	}
	
	//��д������ʵ��ͨ����������
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
		//��ȡ��Ӱ����
		String movieName = page.select("title").text();
		if (StringUtils.isNotEmpty(movieName)) {
			movieName = movieName.split("_")[0];
		}
		logger.info("��ǰ���ʵĵ�ַΪ��"+url+" ��Ӱ����Ϊ��"+movieName);
		if (page.matchUrl("http://www.dygang.net/.*")) {
			List<Element> links = page.select("table tbody tr td[ bgcolor=\"#ffffbb\"]");
			 if (links != null && links.size() > 0) {
				 Vector<Movie> mvVector = this.extractMovieLoadUrlInfoByLinksHtml(links,url,movieName);
				 this.saveExtractorMovieInfoWithSynchronized(mvVector);
			 }
		}
	}
	
	/**
	 * ����ͬ�����Ʊ�����ȡ�ĵ�Ӱ��Ϣ
	 * @param mvVector ��ȡ����Ҫ����ĵ�Ӱ��Ϣ
	 */
	private synchronized void  saveExtractorMovieInfoWithSynchronized(Vector<Movie> mvVector) {
		int movieVectorSize = mvVector.size();
		System.out.println("׼�����б������1������"+movieVectorSize);
		if ( mvVector != null && movieVectorSize > 100) {
			Session session = HibernateUtil.getSession();  
			Transaction tx = session.beginTransaction();
	 	    int movieVectorLen = this.movieVector.size();
	 	   System.out.println("׼�����б������2������"+movieVectorLen);
	 	    logger.info("��������׼���������ݣ��˴α�������ݹ���"+movieVectorLen+"������");
			try {
				if (movieVectorLen > 0) {
					for (int i = 0;i < movieVectorSize;i++) {
						Movie entity = this.movieVector.get(i);
						if (entity != null) {
							session.save(entity);
						}
						tx.commit();
						logger.info("�����ύ�ɹ����ɹ���������"+movieVectorSize);
					}
					System.out.println("�����ύ�ɹ����ɹ���������2"+movieVectorLen);
					System.out.println("�����ύ�ɹ����ɹ���������1"+movieVectorSize);
				}
			} catch (Exception e) {
				logger.error("���ݱ���ʧ��"+e);
			} finally {
				HibernateUtil.closeSession();
				logger.info("�ر�session�ɹ�!");
			}
			this.movieVector = new Vector<Movie>();
			logger.info("���֮ǰ���������,���֮��Ĵ�СΪ��"+this.movieVector.size());
		}		
	}

	/**
	 * ����ȡ������html��ȡ��Ӱ������Ϣ
	 * @param links ��ȡ�ĵ�Ӱ����table��html����
	 * @param title  ��Ӱ����
	 * @param url ��Ӱ���ߵ�ַ
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
					logger.info("��Ӱ���浽vector�ɹ�����СΪ��"+this.movieVector.size());
				}
				this.clearStringBuffer(ed2kSb,magnetSb,torrentSb);
			}
		} else {
			logger.info("��ǰ���ʵĵ�ַΪ��"+url+"��Ӱ����Ϊ��"+title+"û���ҵ����ص�ַ");
		}
		return this.movieVector;
	}
	
	
	/**
	 * �������ص�ַƴ�Ӷ�Ӧ���Ե�ֵ
	 * @param url ��ȡ������������
	 * @param fields ��Ӱ�������ֶε�ֵ
	 * @return ��Ӧ��Ӱ���Ե����ص�ֵַ
	 */
	private StringBuffer extractMovieFiledsValueByDownloadHref(String url,StringBuffer fields) {
		if ( StringUtils.isNotEmpty(url) ) {
				fields = this.appendSplitCharacter(fields);
				fields.append(url);
		}
		return fields;
	}
	
	
	
	/**
	 * ƴ�ӷָ���
	 * @param sb StringBuffer Ҫƴ�ӵ�StringBuffer�ַ���
	 * @return ƴ�Ӻ��StringBuffer�ַ���
	 */
	private StringBuffer appendSplitCharacter(StringBuffer sb) {
		if (sb != null && sb.length() > 0) {
			sb.append("-->");
		}
		return sb;
	}
	
	/**
	 * ��ն���洢��Ӱ��Ϣ�ֶε�StringBuffer����
	 * @param args �����StringBuffer���͵Ĳ���
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
		//�����testΪ���Ĭ����ȡ���ļ���������
		ExtractMovies extractMovies = new ExtractMovies("cralwer", true);
		//��ȡ��ҳ�����
		extractMovies.start(10);
	}

	
	public Vector<Movie> getMovieVector() {
		return movieVector;
	}

	public void setMovieVector(Vector<Movie> movieVector) {
		this.movieVector = movieVector;
	}

		
}
