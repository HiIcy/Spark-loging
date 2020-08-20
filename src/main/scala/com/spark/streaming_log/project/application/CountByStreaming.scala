package com.spark.streaming_log.project.application

import com.spark.streaming_log.project.dao.{CourseClickCountDao, CourseSearchClickCountDao}
import com.spark.streaming_log.project.domain.{ClickLog, CourseClickCount, CourseSearchClickCount}
import com.spark.streaming_log.project.utils.DateUtils
import org.apache.spark.SparkConf
import org.apache.spark.streaming.kafka.KafkaUtils
import org.apache.spark.streaming.{Seconds, StreamingContext}

import scala.collection.mutable.ListBuffer

// 蠢蛋媳妇
object CountByStreaming {
    def main(args: Array[String]): Unit = {
        /**
          * 最终该程序将打包在集群上运行，
          * 需要接收几个参数：zookeeper服务器的ip，kafka消费组，
          * 主题，以及线程数
          */
        if(args.length != 4){
            System.err.println("Error:you need to input:<zookeeper> <group> <toplics> <threadNum>")
            System.exit(1)
        }

        val Array(zkAdderss,group,topics,threadNum) = args

        /**
          * 创建Spark上下文，下本地运行需要设置AppName
          * Master等属性，打包上集群前需要删除
          */
        val sparkConf = new SparkConf()
            .setAppName("CountByStreaming")
            .setMaster("local[4]")

        //创建Spark离散流，每隔60秒接收数据
        val ssc = new StreamingContext(sparkConf,Seconds(60))

        // kafka 数据源
        val topicsMap = topics.split(",").map((_,threadNum.toInt)).toMap
        //创建kafka离散流，每隔60秒消费一次kafka集群的数据
        val kafkaInputDS = KafkaUtils.createStream(ssc,zkAdderss,group,topicsMap)

        //得到原始的日志数据
        val logResourcesDS = kafkaInputDS.map(_._2)
        /**
          * (1)清洗数据，把它封装到ClickLog中
          * (2)过滤掉非法的数据
          */
        val cleanDataRDD = logResourcesDS.map(line =>{
            val splits = line.split("\t")
            if(splits.length != 5) {      //不合法的数据直接封装默认赋予错误值，filter会将其过滤
                ClickLog("", "", 0, 0, "")
            }
            else {
                val ip = splits(0) //获得日志中用户ip
                val time = DateUtils.parseToMinute(splits(1))
                val status = splits(3).toInt  //获得访问状态码
                val referer = splits(4)
                val url = splits(2).split(" ")(1)  //获得搜索url
                var courseId = 0
                if(url.startsWith("/class")){
                    val courseIdHtml = url.split("/")(2)
                    courseId = courseIdHtml.substring(0,courseIdHtml.lastIndexOf(".")).toInt
                }
                ClickLog(ip,time,courseId,status,referer)//将清洗后的日志数据封装进ClickLog中
            }
        }).filter(x=>x.courseId!=0)
        cleanDataRDD.print(2)
        /**
          * (1) 统计数据
          * (2) 把计算结果写进Hbase
          */
        cleanDataRDD.map(line => {
            // 这里相当于定义Hbase表"ns1:courses_clickcount"的 RowKey.
            // 将'日期_课程" 作为 RowKey，意义为某天某门课的访问数
            (line.time.substring(0,8)+"_"+line.courseId,1) // map为元组
        }).reduceByKey(_+_) // 聚合
            .foreachRDD(rdd => { // 一个DStream里有多个RDD
            rdd.foreachPartition(partition =>{  // 一个RDD里有多个Partition
                val list = new ListBuffer[CourseClickCount]
                partition.foreach(item => {  // 一个Partition里有多条记录
                    list.append(CourseClickCount(item._1,item._2))
                })
                CourseClickCountDao.save(list)//保存至HBase
            })
        })

        /**
          * 统计至今为止通过各个搜索引擎而来的实战课程的总点击数
          * (1)统计数据
          * (2)把统计结果写进HBase中去
          */
        cleanDataRDD.map(line => {
            val referer = line.referer
            val time = line.time.substring(0,8)
            var url=""
            if(referer =="-"){//过滤非法url
                (url,time)
            }else{
                // 取出搜索引擎的名字
                url = referer.replaceAll("//","/").split("/")(1)
                (url,time)
            }
        }).filter(x=>x._1 != "").map(line =>{
            // 将'日期_搜索引擎名'作为RowKey,意义为某天通过某搜索引擎访问课程的次数
            (line._2 + "_" + line._1,1)
        }).reduceByKey(_+_)
            .foreachRDD(rdd => {
                rdd.foreachPartition(partition => {
                    val list = new ListBuffer[CourseSearchClickCount]
                    partition.foreach(item => {
                        list.append(CourseSearchClickCount(item._1,item._2))
                    })
                    CourseSearchClickCountDao.save(list)
                })
            })
        ssc.start()
        ssc.awaitTermination()
    }
}





























