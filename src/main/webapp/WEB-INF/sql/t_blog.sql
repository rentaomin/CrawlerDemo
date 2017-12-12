CREATE TABLE `t_blog` (
  `id` int(8) NOT NULL AUTO_INCREMENT COMMENT '主键',
  `title` varchar(500) DEFAULT NULL COMMENT '博客标题',
  `author` varchar(100) DEFAULT NULL COMMENT '博客作者',
  `browseNum` varchar(20) DEFAULT NULL COMMENT '浏览数量',
  `originalNum` varchar(20) DEFAULT NULL COMMENT '原创数量',
  `fansNum` varchar(20) DEFAULT NULL COMMENT '粉丝数量',
  `likeNum` varchar(20) DEFAULT NULL COMMENT '喜欢数量',
  `maYun` varchar(20) DEFAULT NULL COMMENT '码云',
  `blogCatagory` varchar(100) DEFAULT NULL COMMENT '博客分类',
  `blogContent` longtext COMMENT '博客内容',
  `blogUrl` varchar(200) DEFAULT NULL COMMENT '博客地址',
  `insertTime` timestamp NULL DEFAULT NULL COMMENT '插入时间',
  `operateTime` timestamp NULL DEFAULT NULL COMMENT '操作时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=131690 DEFAULT CHARSET=utf8
