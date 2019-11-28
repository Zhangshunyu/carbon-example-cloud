package com.rtm.dataclear

import java.text.SimpleDateFormat
import java.util.{Calendar, Date}

import datalake.ConfigUtil
import datalake.util.CarbonSessionUtil
import org.apache.hadoop.conf.Configuration

object batchExportAudiRawData {
  def main(args: Array[String]): Unit = {
    //TODO 请在resources/config.conf文件中将kafka地址和本地测试数据路径修改为你自己的配置
    //load common config from resources/config.conf
    val properties = ConfigUtil.loadCommonConfig("config.conf", new Configuration)
    val defaultCarbonStoreLocation = "/root/tmp/hive/carbonstore"

    val carbonPropertiesFilePath = if (args.length > 0) {
      //TODO 在实际mrs环境上运行的时候再根据master节点上carbon.properties文件实例路径main入参时传入，本地测试时用""
      // main入参时 args(0)用/opt/Bigdata/MRS_2.0.1/1_13_JDBCServer/etc/carbon.properties
      args(0)
    } else {""}

    val spark = CarbonSessionUtil.createCarbonSession("dataStreaming",
      defaultCarbonStoreLocation, carbonPropertiesFilePath)
    val fmt = new SimpleDateFormat("yyyy-MM-dd")
    val day:Calendar = Calendar.getInstance()
    day.add(Calendar.DATE, -1)
    //    "create table RAW_DATA_AUDI(type string, recvTs timestamp,JSON string) stored by 'org.apache.carbondata' location 'obs://rtma-drc-datalake/carbon'"
    spark.sql(
      s"""
         | INSERT INTO RAW_DATA_AUDI (RECVTS, VIN, TYPE, JSON)
         | SELECT RECVTS, VIN, TYPE, JSON FROM AUDI_FAW_DataLake_T
         | WHERE RECVTS='${ fmt.format(day.getTime) }'
       """.stripMargin)
    spark.close()
  }
}
