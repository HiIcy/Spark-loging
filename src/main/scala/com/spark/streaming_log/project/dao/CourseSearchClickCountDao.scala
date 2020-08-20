package com.spark.streaming_log.project.dao

import com.spark.streaming_log.project.domain.CourseSearchClickCount
import com.spark.streaming_log.project.utils.HBaseUtils
import org.apache.hadoop.hbase.util.Bytes

import scala.collection.mutable.ListBuffer

// 蠢蛋媳妇
object CourseSearchClickCountDao {
    val tableName = "ns1:courses_search_clickcount"
    val cf = "info"
    val qualifer = "click_count"

    /**
      * 保存数据到Hbase
      * @param list (day_course:String,click_count:Int) //统计后当天每门课程的总点击数
      */
    def save(list:ListBuffer[CourseSearchClickCount]): Unit = {
        val table = HBaseUtils.getInstance().getTable(tableName)
        for(item <- list){
            table.incrementColumnValue(Bytes.toBytes(item.day_serach_course),
                Bytes.toBytes(cf),
                Bytes.toBytes(qualifer),
                item.click_count)  //赋值为Long,自动转换
        }
    }
}
