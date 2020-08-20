package com.spark.streaming_log.project.utils

import org.apache.commons.lang3.time.FastDateFormat

object DateUtils {
    val YYYYMMDDHMMSS_FORMAT: FastDateFormat = FastDateFormat.getInstance("yyyy-MM-dd hh:mm:ss")
    val TARGET_FORMAT: FastDateFormat = FastDateFormat.getInstance("yyyyMMddhhmmss")

    //输入String返回该格式转为log的结果
    def getTime(time:String) = {
        YYYYMMDDHMMSS_FORMAT.parse(time).getTime
    }
    def parseToMinute(time:String)= {
        TARGET_FORMAT.format(getTime(time))
    }
}
