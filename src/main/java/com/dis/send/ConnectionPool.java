package com.dis.send;

//import com.bigdata.dis.sdk.DISConfig;
//import com.bigdata.dis.sdk.adapter.kafka.producer.DISKafkaProducer;
//import com.typesafe.config.Config;
//import com.typesafe.config.ConfigFactory;
//import org.apache.kafka.clients.producer.Producer;
//
//import java.util.concurrent.ConcurrentLinkedQueue;

public class ConnectionPool {
//    private static ConcurrentLinkedQueue<Producer> connectionQueue = new ConcurrentLinkedQueue<Producer>();
//    private static DISConfig disConfig = initConfig();
//
//    public static DISConfig initConfig() {
//        Config load = ConfigFactory.load();
//        DISConfig disConfig = new DISConfig();
//        // 根据您实际情况进行配置
//        disConfig.setAK(load.getString("ak"));
//
//        // 根据您实际情况进行配置
//        disConfig.setSK(load.getString("sk"));
//
//        // 根据您实际情况进行配置
//        disConfig.setProjectId(load.getString("projectId"));
//
//        disConfig.setEndpoint(load.getString("endpoint"));
//        disConfig.setRegion(load.getString("region"));
//        disConfig.set("IS_DEFAULT_TRUSTED_JKS_ENABLED", "false");
//        return disConfig;
//    }
//
//    public static Producer<String, byte[]> getConnection() {
//        try {
//            if (connectionQueue.isEmpty()) {
//                for (int i = 0; i < 10; i++) {
//                    Producer<String, byte[]> disKafkaProducer = new DISKafkaProducer<String, byte[]>(disConfig);
//                    connectionQueue.add(disKafkaProducer);
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return connectionQueue.poll();
//
//    }
//
//    public static void returnConnection(Producer disKafkaProducer) {
//        try {
//            connectionQueue.add(disKafkaProducer);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//
//    }
}