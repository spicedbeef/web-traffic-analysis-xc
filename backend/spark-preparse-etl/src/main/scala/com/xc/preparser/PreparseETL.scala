package com.xc.preparser

import org.apache.spark.SparkConf
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.{Encoders, SaveMode, SparkSession}

object PreparseETL {
  def main(args: Array[String]): Unit = {
    val conf = new SparkConf()
    if (args.isEmpty) {
      conf.setMaster("local")
    }
    val spark = SparkSession.builder()
      .appName("PreparseETL")
      .enableHiveSupport()
      .config(conf)
      .getOrCreate()

    val rawdataInputPath = spark.conf.get("spark.traffic.analysis.rawdata.input",
      "hdfs://19master:9999/user/hadoop-xc/traffic-analysis/rawlog/20180615")

    val numberPartitions = spark.conf.get("spark.traffic.analysis.rawdata.numberPartitions","2").toInt

    val preParsedLogRDD:RDD[PreParsedLog] = spark.sparkContext.textFile(rawdataInputPath).flatMap(line => Option(WebLogPreParser.parse(line)))

    val preParsedLogDS = spark.createDataset(preParsedLogRDD)(Encoders.bean(classOf[PreParsedLog]))

    //先合并小文件，然后按分区写文件到hive表
    preParsedLogDS.coalesce(numberPartitions)
      .write
      .mode(SaveMode.Append)
      .partitionBy("year", "month", "day")
      .saveAsTable("rawdata.web")

    spark.stop()



  }
}
