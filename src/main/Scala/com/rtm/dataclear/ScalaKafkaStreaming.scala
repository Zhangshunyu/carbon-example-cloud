package com.rtm.dataclear

import java.text.SimpleDateFormat
import java.util.concurrent.{Executors, TimeUnit}
import java.util.{Date, Properties}

import datalake.ConfigUtil
import datalake.util.{CarbonSessionUtil, KafkaSink}
import org.apache.kafka.common.serialization.{StringDeserializer, StringSerializer}
import org.apache.spark.broadcast.Broadcast
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import net.sf.json.JSONObject
import org.apache.carbondata.common.logging.LogServiceFactory
import org.apache.hadoop.conf.Configuration
import org.apache.spark.sql.{DataFrame, Row, SaveMode, SparkSession}
import org.apache.spark.sql.types.{StringType, StructField, StructType, TimestampType}
import org.apache.carbondata.core.util.CarbonProperties

object ScalaKafkaStreaming {
  private val LOGGER = LogServiceFactory.getLogService(classOf[ConfigUtil].getCanonicalName)
  val fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

  def main(args: Array[String]): Unit = {
    //TODO 请在resources/config.conf文件中将kafka地址和本地测试数据路径修改为你自己的配置
    //load common config from resources/config.conf
    val properties = ConfigUtil.loadCommonConfig("config.conf", new Configuration)
    val sourceBootstrapServers = properties.getProperty("sourceBootstrapServers")
    val targetBootstrapServers = properties.getProperty("targetBootstrapServers")

    val defaultCarbonStoreLocation = properties.getProperty("defaultCarbonStoreLocation")
    val streamLoadDurationSecInConf: String = properties.getProperty("streamLoadDuration")
    val streamLoadDurationSec: Int = if(streamLoadDurationSecInConf != null) {
      streamLoadDurationSecInConf.toInt
    } else {
      300
    }
    println("The load app will run per " + streamLoadDurationSec + " seconds.")

    //    val carbonPropertiesFilePath = "D:\\RTMA_COMPLETION\\environment\\conf\\carbon.properties"
    val carbonPropertiesFilePath = if (args.length > 0) {
      //TODO 在实际mrs环境上运行的时候再根据master节点上carbon.properties文件实例路径main入参时传入，本地测试时用""
      // main入参时 args(0)用/opt/Bigdata/MRS_2.0.1/1_13_JDBCServer/etc/carbon.properties
      LOGGER.info("The properties file is : " + args(0))
      args(0)
    } else {""}

    var spark = CarbonSessionUtil.createCarbonSession("ScalaKafkaStreaming",
      defaultCarbonStoreLocation, carbonPropertiesFilePath)

    //Table
    val VGIC_DataLake = properties.getProperty("VGIC_DataLake")
    val SVW_DataLake = properties.getProperty("SVW_DataLake")
    val FAW_FBU_DataLake = properties.getProperty("FAW_FBU_DataLake")
    val AUDI_FAW_DataLake = properties.getProperty("AUDI_FAW_DataLake")

    println("create table begin!")

    spark.sql(
      s"""
         | CREATE TABLE IF NOT EXISTS audi_faw_datalake (
         | vin STRING,
         | recvt TIMESTAMP,
         | type STRING,
         | json STRING
         | )
         | STORED BY 'carbondata'
         |  tblproperties('sort_scope'='global_sort','sort_columns'='recvt')
         | location 'obs://audi-faw-datalake/carbon/default/audi_faw_datalake'
         |  """.stripMargin)
    println("AUDI_FAW_DataLake is created!")

    spark.sql(
      s"""
         | CREATE TABLE IF NOT EXISTS vgic_datalake (
         | vin STRING,
         | recvt TIMESTAMP,
         | type STRING,
         | json STRING
         | )
         | STORED BY 'carbondata'
         |  tblproperties('sort_scope'='global_sort','sort_columns'='recvt')
         | location 'obs://vgic-datalake/carbon/default/vgic_datalake'
         | """.stripMargin)
    println("VGIC_DataLake is created!")

    spark.sql(
      s"""
         | CREATE TABLE IF NOT EXISTS svw_datalake (
         | vin STRING,
         | recvt TIMESTAMP,
         | type STRING,
         | json STRING
         | )
         | STORED BY 'carbondata'
         |  tblproperties('sort_scope'='global_sort','sort_columns'='recvt')
         | location 'obs://saic-vw-datalake/carbon/default/svw_datalake'
         |  """.stripMargin)
    println("SVW_DataLake is created!")

    spark.sql(
      s"""
         | CREATE TABLE IF NOT EXISTS faw_fbu_datalake (
         | vin STRING,
         | recvt TIMESTAMP,
         | type STRING,
         | json STRING
         | )
         | STORED BY 'carbondata'
         |  tblproperties('sort_scope'='global_sort','sort_columns'='recvt')
         | location 'obs://audi-fbu-datalake/carbon/default/faw_fbu_datalake'
          | """.stripMargin)
    println("FAW_FBU_DataLake is created!")



    var ssc: StreamingContext = new StreamingContext(spark.sparkContext, Seconds(streamLoadDurationSec))
    ssc.checkpoint(CarbonProperties.getStorePath + "/checkpoint")
    //TODO bootstrapServers请修改为实际环境的。
    val sourceGroupId = properties.getProperty("sourceGroupId")
    val sourceTopicName = properties.getProperty("sourceTopicName")
    val targetTopicVGIC = properties.getProperty("targetTopicVGIC")
    val targetTopicAUDI_FAW = properties.getProperty("targetTopicAUDI_FAW")
    val targetTopicSVW = properties.getProperty("targetTopicSVW")
    val targetTopicFAW_FBU = properties.getProperty("targetTopicFAW_FBU")
    val maxPoll = 200

    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "10.231.33.15:21007,10.231.33.75:21007,10.231.33.83:21007",
      "security.protocol" -> "SASL_PLAINTEXT",
      "sasl.kerberos.service.name" -> "kafka",
      "key.deserializer" ->classOf[StringDeserializer],
      "value.deserializer" ->classOf[StringDeserializer],
      "group.id" -> "rtm.data.consumer",
      "partition.assignment.strategy"->"org.apache.kafka.clients.consumer.RangeAssignor",
      //"group.id" -> "rtma.big.data.consumer",
      "enable.auto.commit" -> "true",
      "kerberos.domain.name" -> "hadoop.83a20cca_de1b_4767_95eb_2b8b8159a403.com",
//      "sasl.jaas.conf" -> "StormClient {\ncom.sun.security.auth.module.Krb5LoginModule required\nuseKeyTab=true\nkeyTab=\"/opt/rtma/user.keytab\"\nprincipal=\"rtma_bigdata\"\nuseTicketCache=false\nstoreKey=true\ndebug=true;\n};\nKafkaClient {\ncom.sun.security.auth.module.Krb5LoginModule required\nuseKeyTab=true\nkeyTab=\"/opt/rtma/user.keytab\"\nprincipal=\"rtma_bigdata\"\nuseTicketCache=false\nstoreKey=true\ndebug=true;\n};\nClient {\ncom.sun.security.auth.module.Krb5LoginModule required\nuseKeyTab=true\nkeyTab=\"/opt/rtma/user.keytab\"\nprincipal=\"rtma_bigdata\"\nuseTicketCache=false\nstoreKey=true\ndebug=true;\n};",
      "max.poll.records" -> "10",
      "auto.offset.reset" -> "latest"
    )

    val kafkaTopicDS = KafkaUtils.createDirectStream(ssc, LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](Set(sourceTopicName), kafkaParams))

    // 初始化KafkaSink,并广播
    val kafkaProducer: Broadcast[KafkaSink[String, String]] = {
      val kafkaProducerConfig = {
        val p = new Properties()
        p.put("bootstrap.servers", targetBootstrapServers)
        p.put("acks", "all")
        p.put("retries", "0")
        //        p.put("batch.size", "16384")
        p.put("linger.ms", "1")
        //        p.put("buffer.memory", "33554432")
        p.put("key.serializer", "org.apache.kafka.common.serialization.StringSerializer")
        p.put("value.serializer", "org.apache.kafka.common.serialization.StringSerializer")
        p
      }
      ssc.sparkContext.broadcast(KafkaSink[String, String](kafkaProducerConfig))
    }

    kafkaTopicDS.foreachRDD(rdd => {
      rdd.cache()
      if (!rdd.isEmpty()) {
        val eachRddStartTime = System.currentTimeMillis()
        val jsonList =
          rdd.map {
            record => {

              val data = JSONObject.fromObject(record.value())

              val vin = data.get("vin").toString
              val recvTs = if (data.get("recvTs") == "null") {
                fmt.format(new Date)
              } else {
                fmt.format(new Date(data.get("recvTs").toString.toLong))
              }

              if (vin.startsWith("WVW")) {
                kafkaProducer.value.send(targetTopicVGIC, record.value())
                Row(vin,recvTs,  "VGIC", record.value())
              } else if (vin.startsWith("LSV")) {
                kafkaProducer.value.send(targetTopicSVW, record.value())
                Row(vin,recvTs,  "SVW", record.value())
              } else if (vin.startsWith("WAU")) {
                kafkaProducer.value.send(targetTopicFAW_FBU, record.value())
                Row( vin,recvTs, "FAW_FBU", record.value())
              } else if (vin.startsWith("LFV")) {
                kafkaProducer.value.send(targetTopicAUDI_FAW, record.value())
                Row( vin, recvTs,"AUDI_FAW", record.value())
              } else {
                Row( vin,recvTs, "OTHER", record.value())
              }
            }
          }
        if (jsonList != null && !jsonList.isEmpty()) {

          val df = spark.createDataFrame(jsonList, defineSchema())
          df.cache()
          val loadTablesStartTime = System.currentTimeMillis()
          val exeService = Executors.newFixedThreadPool(4)

          exeService.submit(new Runnable {
            override def run(): Unit =
                        if (df.filter("type = 'VGIC'").count() > 0) {
                          df.filter("type = 'VGIC'")
                            .write
                            .format("carbondata")
                            .option("tableName", "VGIC_DataLake")
                            .mode(SaveMode.Append)
                            .save()
                          println("VGIC_DataLake is loaded!")
                        }
          })
          exeService.submit(new Runnable {
            override def run(): Unit =  if (df.filter("type = 'SVW'").count() > 0) {
                        df.filter("type = 'SVW'")
                          .write
                          .format("carbondata")
                          .option("tableName", "SVW_DataLake")
                          .mode(SaveMode.Append)
                          .save()
                        println("SVW_DataLake is loaded!")
                      }
          })
          exeService.submit(new Runnable {
            override def run(): Unit =
                      if (df.filter("type = 'FAW_FBU'").count() > 0) {
                        df.filter("type = 'FAW_FBU'")
                          .write
                          .format("carbondata")
                          .option("tableName", "FAW_FBU_DataLake")
                          .mode(SaveMode.Append)
                          .save()
                        println("FAW_FBU_DataLake is loaded!")
                      }
          })
          exeService.submit(new Runnable {
            override def run(): Unit =
                      if (df.filter("type = 'AUDI_FAW'").count() > 0) {
                        df.filter("type = 'AUDI_FAW'")
                                      .write
                                      .format("carbondata")
                                      .option("tableName", "AUDI_FAW_DataLake")
                                      .mode(SaveMode.Append)
                                      .save()
                        println("AUDI_FAW_DataLake is loaded!")
          }}
          )

          exeService.shutdown()
          if (!exeService.awaitTermination(60, TimeUnit.MINUTES)) {
            exeService.shutdownNow()
          }
          df.unpersist()

          val loadTablesEndTime = System.currentTimeMillis()
          println("---per rdd -load tables cost time in ms: " +(loadTablesEndTime-loadTablesStartTime))
        } else {
          println("null dataframe!")
        }

        val eachRddEndTime = System.currentTimeMillis()
        println("----per rdd cost in ms:" + (eachRddEndTime-eachRddStartTime))
      } else {
        println("=========rdd is empty, ignore it!")
      }
    })

    def defineSchema(): StructType = {
      StructType(Array(
        StructField("vin", StringType, true),
        StructField("recvt", StringType, true),
        StructField("type", StringType, true),
        StructField("json", StringType, true)
      ))
    }

    ssc.start()
    ssc.awaitTermination()

  }
}
