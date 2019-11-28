package datalake.util

import datalake.ConfigUtil
import org.apache.carbondata.common.logging.LogServiceFactory
import org.apache.hadoop.conf.Configuration
import org.apache.spark.sql.{SaveMode, SparkSession}
import org.apache.carbondata.core.constants.CarbonCommonConstants
import org.apache.carbondata.core.util.CarbonProperties

object CarbonSessionUtil {
  private val LOGGER = LogServiceFactory.getLogService(classOf[ConfigUtil].getCanonicalName)

  val sparkObsConfigFileName = "spark-obs.conf"

  def createCarbonSession (appName: String,
                           defaultCarbonStoreLocation: String, carbonPropertiesFileFullPath: String): SparkSession = {

    CarbonProperties.getInstance()
      .addProperty(CarbonCommonConstants.CARBON_TIMESTAMP_FORMAT, "yyyy/MM/dd HH:mm:ss")
      .addProperty(CarbonCommonConstants.CARBON_DATE_FORMAT, "yyyy/MM/dd")
      .addProperty(CarbonCommonConstants.ENABLE_UNSAFE_COLUMN_PAGE, "true")
      .addProperty(CarbonCommonConstants.CARBON_BADRECORDS_LOC, "")

    ConfigUtil.loadCarbonConfig(carbonPropertiesFileFullPath, new Configuration())

    val realEnvCarbonStoreLocation = if (CarbonProperties.getStorePath != null) {
      LOGGER.info("get the path from carbon properties.")
      CarbonProperties.getStorePath} else {
      LOGGER.info("get the path from default path.")
      defaultCarbonStoreLocation}
    println(s"Use realEnvCarbonStoreLocation: $realEnvCarbonStoreLocation" )
    LOGGER.info("real store path is: " + realEnvCarbonStoreLocation)

    val masterUrl = "yarn" // if (carbonPropertiesFileFullPath.isEmpty) "local" else "yarn"

    import org.apache.spark.sql.CarbonSession._

    val sparkSessionBuilder = SparkSession
      .builder()
      // use yarn on real env
      .master(masterUrl)
      .appName(appName)
//      .config("spark.driver.host", "localhost")
      .config("spark.sql.crossJoin.enabled", "true")
    // load the obs related config into session
    ConfigUtil.loadSparkConfig(sparkObsConfigFileName, new Configuration(), sparkSessionBuilder)
      sparkSessionBuilder.config("spark.sql.warehouse.dir", realEnvCarbonStoreLocation + "dir")
//    sparkSessionBuilder.config("spark.sql.warehouse.dir", realEnvCarbonStoreLocation)
    CarbonProperties.getInstance().addProperty("carbon.storelocation", realEnvCarbonStoreLocation)
    val spark = sparkSessionBuilder.getOrCreateCarbonSession(realEnvCarbonStoreLocation, null)
    spark.sparkContext.setLogLevel("INFO")
    spark
  }

}
