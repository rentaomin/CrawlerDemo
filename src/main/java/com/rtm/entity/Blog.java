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
	
	/** 主键 */
	private int id;
	
	/** 博客主题 */
	private String title;
	
	/** 博客作者 */
	private String author;
	
	/** 浏览人数 */
	private String browseNum;
	
	/** 博客原创数量 */
	private String originalNum;
	
	/** 粉丝数 */
	private String fansNum;
	
	/** 喜欢人数 */
	private String likeNum;
	
	/** 码云 */
	private String maYun;
	
	/** 博客分类 */
	private String blogCatagory;
	
	/** 博客内容 */
	private String blogContent;
	
	/** 博客地址 */
	private String blogUrl;
	
	/** 插入时间 */
	private Date insertTime;
	
	/** 操作时间 */
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
