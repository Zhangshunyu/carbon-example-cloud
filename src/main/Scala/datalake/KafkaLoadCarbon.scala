package datalake

import java.text.SimpleDateFormat
import java.util.concurrent.{Executors, TimeUnit}
import java.util.{Date, Properties}

import datalake.util.CarbonSessionUtil
import net.sf.json.JSONObject
import org.apache.carbondata.common.logging.LogServiceFactory
import org.apache.carbondata.core.util.CarbonProperties
import org.apache.hadoop.conf.Configuration
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.apache.spark.sql.{Row, SaveMode}
import org.apache.spark.streaming.kafka010.{ConsumerStrategies, KafkaUtils, LocationStrategies}
import org.apache.spark.streaming.{Seconds, StreamingContext}

object KafkaLoadCarbon {
  private val LOGGER = LogServiceFactory.getLogService(classOf[ConfigUtil].getCanonicalName)
  val fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

  def main(args: Array[String]): Unit = {
    //TODO 请在resources/config.conf文件中将kafka地址和本地测试数据路径修改为你自己的配置
    //load common config from resources/config.conf
    val configProperties = ConfigUtil.loadCommonConfig("config.conf", new Configuration)

    val defaultCarbonStoreLocation = configProperties.getProperty("defaultCarbonStoreLocation")
    val streamLoadDurationSecInConf: String = configProperties.getProperty("streamLoadDuration")
    val streamLoadDurationSec: Int = if(streamLoadDurationSecInConf != null) {
      streamLoadDurationSecInConf.toInt
    } else {
      300
    }
    println("The load app will run per " + streamLoadDurationSec + " seconds.")

    val carbonPropertiesFilePath = if (args.length > 0) {
      //TODO 在实际mrs环境上运行的时候再根据master节点上carbon.properties文件实例路径main入参时传入，本地测试时用""
      // main入参时 args(0)用/opt/Bigdata/MRS_2.0.1/1_13_JDBCServer/etc/carbon.properties
      LOGGER.info("The properties file is : " + args(0))
      args(0)
    } else {""}

    val spark = CarbonSessionUtil.createCarbonSession("ScalaKafkaStreaming",
      defaultCarbonStoreLocation, carbonPropertiesFilePath)

    println("create table begin!")
    spark.sql(
      s"""
         | CREATE TABLE IF NOT EXISTS test_table (
         | vin STRING,
         | recvt TIMESTAMP,
         | type STRING,
         | json STRING
         | )
         | STORED BY 'carbondata'
         |  tblproperties('sort_scope'='global_sort','sort_columns'='recvt')
         | location 'obs://your-obs-bucket/carbon/default/test_table'
         |  """.stripMargin)
    println("test_table is created!")

    spark.sql(
      s"""
         | CREATE TABLE IF NOT EXISTS test_table_2 (
         | vin STRING,
         | recvt TIMESTAMP,
         | type STRING,
         | json STRING
         | )
         | STORED BY 'carbondata'
         |  tblproperties('sort_scope'='global_sort','sort_columns'='recvt')
         | location 'obs://your-obs-bucket/carbon/default/test_table_2'
         | """.stripMargin)
    println("test_table_2 is created!")

    val ssc: StreamingContext = new StreamingContext(spark.sparkContext, Seconds(streamLoadDurationSec))
    ssc.checkpoint(CarbonProperties.getStorePath + "/checkpoint")
    val sourceTopicName = configProperties.getProperty("sourceTopicName")


    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "your-kafka-ip-port",
      "security.protocol" -> "SASL_PLAINTEXT",
      "sasl.kerberos.service.name" -> "kafka",
      "key.deserializer" ->classOf[StringDeserializer],
      "value.deserializer" ->classOf[StringDeserializer],
      "group.id" -> "rtm.data.consumer",
      "partition.assignment.strategy"->"org.apache.kafka.clients.consumer.RangeAssignor",
      "enable.auto.commit" -> "true",
      "kerberos.domain.name" -> "your-kafka-kerberos.domain.name",
      "max.poll.records" -> "10",
      "auto.offset.reset" -> "latest"
    )

    val kafkaTopicDS = KafkaUtils.createDirectStream(ssc, LocationStrategies.PreferConsistent,
      ConsumerStrategies.Subscribe[String, String](Set(sourceTopicName), kafkaParams))

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

              if (vin.startsWith("AAA")) {
                Row(vin,recvTs,  "AAA", record.value())
              } else {
                Row( vin,recvTs, "BBB", record.value())
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
                        if (df.filter("type = 'AAA'").count() > 0) {
                          df.filter("type = 'AAA'")
                            .write
                            .format("carbondata")
                            .option("tableName", "test_table")
                            .mode(SaveMode.Append)
                            .save()
                          println("test_table is loaded!")
                        }
          })
          exeService.submit(new Runnable {
            override def run(): Unit =  if (df.filter("type = 'BBB'").count() > 0) {
                        df.filter("type = 'BBB'")
                          .write
                          .format("carbondata")
                          .option("tableName", "test_table_2")
                          .mode(SaveMode.Append)
                          .save()
                        println("test_table_2 is loaded!")
                      }
          })

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
