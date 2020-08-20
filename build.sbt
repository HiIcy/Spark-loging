name := "Spark_loging"

version := "0.1"

scalaVersion := "2.10.4"


libraryDependencies ++= Seq(
    "org.apache.spark" %% "spark-core" % "1.5.2" exclude("org.jboss.netty", "netty"),
    "org.apache.spark" %% "spark-sql" % "1.5.2" % "provided",
    "org.apache.spark" %% "spark-hive" % "1.5.2" % "provided",
    "org.apache.spark" %% "spark-streaming" % "1.5.2",
    "org.apache.kafka" % "kafka-clients" % "0.8.2.1",
    "org.apache.kafka" %% "kafka" % "0.8.2.1",
    //    "org.apache.kafka" %% "kafka" % "0.8.2.1",
    "org.apache.spark" %% "spark-streaming-kafka" % "1.5.2",
    "org.apache.hbase" % "hbase-client" % "1.3.1", // 1.3.1 对应服务器上0.98是可以的 2.0不行
    "org.apache.hadoop" % "hadoop-common" % "2.7.7",
    "org.apache.commons" % "commons-lang3" % "3.3.2",
    //    "org.apache.spark" %% "spark-streaming-kafka-0-8" % "2.4.3",
    "mysql" % "mysql-connector-java" % "5.1.38"
)

assemblyOption in assembly := (assemblyOption in assembly).value.copy(includeScala = false, includeDependency = false)

assemblyMergeStrategy in assembly := {
    case PathList("META-INF", xs @ _*) => MergeStrategy.discard
    case x => MergeStrategy.first
}