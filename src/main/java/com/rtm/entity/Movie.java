package com.rtm.entity;

import java.io.Serializable;
import java.util.Date;

import javax.print.DocFlavor.URL;

/**
 * ��Ӱʵ����
 * @author Dell
 *
 */
public class Movie implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//����
	private int id;
	
	//��Ӱ����
	private String movieName;
	
	//��Ӱ����
	private String movieType;
	
	//��Ӱ��ַ
	private String movieUrl;
	
	//Ѹ�׵�ַ
	private String magnetUrl;
	
	//��¿��ַ
	private String ed2kUrl;
	
	//���ӵ�ַ
	private String torrentUrl;
	
	//����ʱ��
	private Date insertTime;
	
	//����ʱ��
	private Date operateTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getMovieName() {
		return movieName;
	}

	public void setMovieName(String movieName) {
		this.movieName = movieName;
	}

	public String getMovieType() {
		return movieType;
	}

	public void setMovieType(String movieType) {
		this.movieType = movieType;
	}

	public String getMovieUrl() {
		return movieUrl;
	}

	public void setMovieUrl(String movieUrl) {
		this.movieUrl = movieUrl;
	}

	public String getMagnetUrl() {
		return magnetUrl;
	}

	public void setMagnetUrl(String magnetUrl) {
		this.magnetUrl = magnetUrl;
	}

	public String getEd2kUrl() {
		return ed2kUrl;
	}

	public void setEd2kUrl(String ed2kUrl) {
		this.ed2kUrl = ed2kUrl;
	}

	public String getTorrentUrl() {
		return torrentUrl;
	}

	public void setTorrentUrl(String torrentUrl) {
		this.torrentUrl = torrentUrl;
	}

	public Date getInsertTime() {
		return insertTime;
	}

	public void setInsertTime(Date insertTime) {
		this.insertTime = insertTime;
	}

	public Date getOperateTime() {
		return operateTime;
	}

	public void setOperateTime(Date operateTime) {
		this.operateTime = operateTime;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((movieName == null) ? 0 : movieName.hashCode());
		result = prime * result + ((movieUrl == null) ? 0 : movieUrl.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Movie other = (Movie) obj;
		if (movieName == null) {
			if (other.movieName != null)
				return false;
		} else if (!movieName.equals(other.movieName))
			return false;
		if (movieUrl == null) {
			if (other.movieUrl != null)
				return false;
		} else if (!movieUrl.equals(other.movieUrl))
			return false;
		return true;
	}



	
}
