package com.rtm.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 电影实体类
 * @author Dell
 *
 */
public class Movie implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//主键
	private int id;
	
	//电影名称
	private String movieName;
	
	//电影类型
	private String movieType;
	
	//电影地址
	private String movieUrl;
	
	//迅雷地址
	private String magnetUrl;
	
	//电驴地址
	private String ed2kUrl;
	
	//百度网盘地址
	private String baiduUrl;
	
	//百度密码
	private String baiduPwd;
	
	//qq旋风地址
	private String qqloadUrl;
	
	//种子地址
	private String torrentUrl;
	
	//360云盘地址
	private String yun360Url;
	
	//插入时间
	private Date insertTime;
	
	//操作时间
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

	public String getBaiduUrl() {
		return baiduUrl;
	}

	public void setBaiduUrl(String baiduUrl) {
		this.baiduUrl = baiduUrl;
	}

	public String getBaiduPwd() {
		return baiduPwd;
	}

	public void setBaiduPwd(String baiduPwd) {
		this.baiduPwd = baiduPwd;
	}

	public String getQqloadUrl() {
		return qqloadUrl;
	}

	public void setQqloadUrl(String qqloadUrl) {
		this.qqloadUrl = qqloadUrl;
	}

	public String getTorrentUrl() {
		return torrentUrl;
	}

	public void setTorrentUrl(String torrentUrl) {
		this.torrentUrl = torrentUrl;
	}

	public String getYun360Url() {
		return yun360Url;
	}

	public void setYun360Url(String yun360Url) {
		this.yun360Url = yun360Url;
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

	
}
