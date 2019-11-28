package com.util

import java.text.SimpleDateFormat
import java.util.{Calendar, Date}



object tool {

  /**
    * 日期格式化函数，将日志中的日期进行格式化，按照分钟为最小单位,格式化成毫秒
    * @param line
    */
  def formatHour(line:String):String={

    val fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val time: String = fm.format(new Date(line.toLong))
    time
  }
  //日期相差分钟
  def days(data:String,data1:String):Long={
    var calendar = Calendar.getInstance()
    val sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(data)
    val sdf1 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(data1)
    calendar.setTime(sdf)
    val d1: Date = calendar.getTime()
    calendar.setTime(sdf1)
    val d2 = calendar.getTime()
    val daterange: Long = d2.getTime() - d1.getTime()
//    val time = 1000*3600*24
    val time = 1000
    val day: Long =daterange/time/60
    day
  }
  //获取当前时间
  def NowDate(): String = {
    val now: Date = new Date()
    val dateFormat: SimpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    val date = dateFormat.format(now)
    return date
  }


}
