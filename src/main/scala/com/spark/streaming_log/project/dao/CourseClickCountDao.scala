package com.spark.streaming_log.project.dao
import com.spark.streaming_log.project.domain.CourseClickCount
import com.spark.streaming_log.project.utils.HBaseUtils
import org.apache.hadoop.hbase.util.Bytes

import scala.collection.mutable.ListBuffer

// 蠢蛋媳妇
/**
  * 实战课程点击数统计访问层
  */
object CourseClickCountDao {
    val tableName="ns1:courses_clickcount" // 表名
    val cf = "info" // 列族
    val qualifer = "click_count" // 列

    /**
      * 保存数据到Hbase
      * @param list (day_course:String,click_count:Int) //统计后当天每门课程的总点击数
      */
    def save(list:ListBuffer[CourseClickCount]):Unit={
        //调用HBaseUtils的方法，获得HBase表实例
        val table = HBaseUtils.getInstance().getTable(tableName)
        for(item <- list){
            // 调用Hbase 的 一个自增加方法
            table.incrementColumnValue(Bytes.toBytes(item.day_course),
                Bytes.toBytes(cf),
                Bytes.toBytes(qualifer),
                item.click_count)
        }
    }
}
















