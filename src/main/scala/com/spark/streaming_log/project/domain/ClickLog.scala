package com.spark.streaming_log.project.domain

// 蠢蛋
/**
在domain包下创建以下几个Scala样例类：
ClickLog：用于封装清洗后的日志信息
  */

/**
  * 封装清洗后的数据
  * @param ip 日志访问的ip地址
  * @param time 日志访问的时间
  * @param courseId 日志访问的实战课程编号
  * @param statusCode 日志访问的状态码
  * @param referer 日志访问的referer信息
  */
//样例类
case class ClickLog (ip:String,time:String,courseId:Int,statusCode:Int,referer:String)

//再创建样例类 CourseClickCount 用于封装课程统计的结果，样例类 CourseSearchClickCount 用于封装搜索引擎的统计结果
/**
  * 封装实战课程的总点击数结果
  * @param day_course 对应于Hbase中的RowKey
  * @param click_count 总点击数
  */
case class CourseClickCount(day_course:String,click_count:Int)

/**
  * 封装统计通过搜索引擎多来的实战课程的点击量
  * @param day_serach_course 当天通过某搜索引擎过来的实战课程
  * @param click_count 点击数
  */
case class CourseSearchClickCount(day_serach_course:String,click_count:Int)




























