package com.rtm.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * 博客实体类
 * @author Dell
 *
 */
public class Blog implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private int id;
	
	private String title;
	
	private String author;
	
	private String browseNum;
	
	private String originalNum;
	
	private String fansNum;
	
	private String likeNum;
	
	private String maYun;
	
	private String blogCatagory;
	
	private String blogContent;
	
	private String blogUrl;
	
	private Date insertTime;
		
	private Date operateTime;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getBrowseNum() {
		return browseNum;
	}

	public void setBrowseNum(String browseNum) {
		this.browseNum = browseNum;
	}

	public String getOriginalNum() {
		return originalNum;
	}

	public void setOriginalNum(String originalNum) {
		this.originalNum = originalNum;
	}

	public String getFansNum() {
		return fansNum;
	}

	public void setFansNum(String fansNum) {
		this.fansNum = fansNum;
	}

	public String getLikeNum() {
		return likeNum;
	}

	public void setLikeNum(String likeNum) {
		this.likeNum = likeNum;
	}

	public String getMaYun() {
		return maYun;
	}

	public void setMaYun(String maYun) {
		this.maYun = maYun;
	}

	public String getBlogCatagory() {
		return blogCatagory;
	}

	public void setBlogCatagory(String blogCatagory) {
		this.blogCatagory = blogCatagory;
	}

	public String getBlogContent() {
		return blogContent;
	}

	public void setBlogContent(String blogContent) {
		this.blogContent = blogContent;
	}

	public String getBlogUrl() {
		return blogUrl;
	}

	public void setBlogUrl(String blogUrl) {
		this.blogUrl = blogUrl;
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
		result = prime * result + ((author == null) ? 0 : author.hashCode());
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
		Blog other = (Blog) obj;
		if (author == null) {
			if (other.author != null)
				return false;
		} else if (!author.equals(other.author))
			return false;
		return true;
	}
	
	
	
}
