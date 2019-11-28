package com.rtm.dataclear

import java.io.IOException
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util
import java.util.{Date, Properties}

import org.apache.hadoop.conf.Configuration
import com.utils._
import net.sf.json.JSONObject
import org.apache.log4j.{Level, Logger}
import org.apache.spark.streaming.{Seconds, StreamingContext}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.streaming.dstream.{DStream, InputDStream}
import org.apache.spark.streaming.kafka010.ConsumerStrategies.Subscribe
import org.apache.spark.streaming.kafka010.KafkaUtils
import org.apache.spark.streaming.kafka010.LocationStrategies.PreferConsistent
import org.apache.kafka.common.serialization.StringDeserializer
import com.bean.Rowdata
import com.typesafe.config.{Config, ConfigFactory}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{DataFrame, Row, SQLContext, SaveMode, SparkSession}

import scala.collection.mutable.ArrayBuffer

object Realtime_spark {
  val sparkConfig = new SparkConf()
    .setAppName("data_dump_hbase")
    .setMaster("local[5]")
    .set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    .set("spark.default.parallelism", "1")

  var sparkContext:SparkContext = null
  val para: Config = ConfigFactory.load("application.conf")


  def main(args: Array[String]): Unit = {
    System.setProperty("java.security.auth.login.config", "/opt/rtma/jaas.conf")
    System.setProperty("java.security.krb5.conf", "/opt/rtma/krb5.conf")
    println("-------------------")
    println(System.getProperty("java.security.krb5.conf"))


    val hadoopConf: Configuration = new Configuration()

    // 屏蔽不必要的日志 ,在终端上显示需要的日志
    Logger.getLogger("org.apache.spark").setLevel(Level.ALL)
    Logger.getLogger("org.eclipse.jetty.server").setLevel(Level.ALL)
    Logger.getLogger("org.apache.kafka.clients.consumer").setLevel(Level.ALL)

//    sparkConfig.set("spark.serializer", "org.apache.spark.serializer.KryoSerializer")
    val ssc = new StreamingContext(sparkConfig, Seconds(5))
    sparkContext = ssc.sparkContext

//    QA_TEST
//    return

    /*
      Kafaka配置信息
  */
    val kafkaParams = Map[String, Object](
      "bootstrap.servers" -> "10.231.33.15:21007,10.231.33.75:21007,10.231.33.83:21007",
      "security.protocol" -> "SASL_PLAINTEXT",
      "sasl.kerberos.service.name" -> "kafka",
      "key.deserializer" ->classOf[StringDeserializer],
      "value.deserializer" ->classOf[StringDeserializer],
      "group.id" -> "test",
      "partition.assignment.strategy"->"org.apache.kafka.clients.consumer.RangeAssignor",
      //"group.id" -> "rtma.big.data.consumer",
      "enable.auto.commit" -> "true",
      "kerberos.domain.name" -> "hadoop.83a20cca_de1b_4767_95eb_2b8b8159a403.com",
//      "sasl.jaas.conf" -> "StormClient {\ncom.sun.security.auth.module.Krb5LoginModule required\nuseKeyTab=true\nkeyTab=\"/opt/rtma/user.keytab\"\nprincipal=\"rtma_bigdata\"\nuseTicketCache=false\nstoreKey=true\ndebug=true;\n};\nKafkaClient {\ncom.sun.security.auth.module.Krb5LoginModule required\nuseKeyTab=true\nkeyTab=\"/opt/rtma/user.keytab\"\nprincipal=\"rtma_bigdata\"\nuseTicketCache=false\nstoreKey=true\ndebug=true;\n};\nClient {\ncom.sun.security.auth.module.Krb5LoginModule required\nuseKeyTab=true\nkeyTab=\"/opt/rtma/user.keytab\"\nprincipal=\"rtma_bigdata\"\nuseTicketCache=false\nstoreKey=true\ndebug=true;\n};",
      "max.poll.records" -> "10",
      "auto.offset.reset" -> "latest"
    )
   try {
     val inboundKafkaTopics = Set("dynamicDataForRTMA")
     val kafkaStream: DStream[String] = KafkaUtils.createDirectStream[String, String](ssc, PreferConsistent, Subscribe[String, String](inboundKafkaTopics, kafkaParams)).map(_.value())

     kafkaStream.foreachRDD(rdd => {
 //      println("foreachRDD start")
       //kafka消费
       //rdd.foreach(u => hBaseWriter(u))
       rdd.foreachPartition(iterator => hBaseWriter(iterator))
 //      println("foreachRDD finish")
     })

     ssc.start()
     ssc.awaitTermination()
   }
    catch {
      case e: Exception => {
        Logger.getLogger(this.getClass()).error(e.getMessage())
      }
    }
  }

  val fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
  val pgProp = new Properties()
  pgProp.setProperty("driver", para.getString("pgdriver"))
  pgProp.setProperty("user", para.getString("pguser"))
  pgProp.setProperty("password", para.getString("pgpassword"))

  case class vgc_qa_pedals_stepping(vin:String, reporttime:Timestamp)
  case class vgc_qa_warning(vin:String, reporttime:Timestamp, isvalid:Boolean)
  case class vgc_qa_battery_cell_warning(vin:String, reporttime:Timestamp, cell_high_no:String, cell_high_voltage:Double, cell_low_no:String, cell_low_voltage:Double, voltage:Double, cells:Int, voltage_array:String, temp_sensors:Int, temp_array:String, battery_current:Double)

  def hBaseWriter(iterator: Iterator[String]): Unit = {
    println("==============")
    println(iterator.isEmpty)
    if (iterator != null )
      println(iterator.length)
    else
      println("////////////")
    println("..................")
  }
 //   println("hBaseWriter finish")

}
