package com.dis.send;

//import com.bigdata.dis.sdk.DISConfig;
//import com.bigdata.dis.sdk.adapter.kafka.producer.DISKafkaProducer;
//import com.cloudwise.sdg.dic.DicInitializer;
//import com.cloudwise.sdg.template.TemplateAnalyzer;
//import org.apache.commons.io.FileUtils;
//import org.apache.kafka.clients.producer.Producer;
//import org.apache.kafka.clients.producer.ProducerRecord;
//
//import java.io.File;
//import java.nio.ByteBuffer;


public class DISKafkaProdecerDemo {
//    public static void main(String[] args) throws  Exception
//    {
//        DISConfig disConfig = new DISConfig();
//
//        // 根据您实际情况进行配置
//        disConfig.setAK("GKK2GX7HCWF6XLG6ISJX");
//
//        // 根据您实际情况进行配置
//        disConfig.setSK("g749Y4wa1MoVPlg0Ln9mBYgsB4fGKOvaGdXHQSkm");
//
//        // 根据您实际情况进行配置
//        disConfig.setProjectId("64854a1e1f98493ca4119fecceccf83b");
//
//        disConfig.setEndpoint("https://dis.cn-north-1.myhwclouds.com:20004");
//        disConfig.setRegion("cn-north-1");
//        disConfig.set("IS_DEFAULT_TRUSTED_JKS_ENABLED", "false");
//
//        Producer<String, byte[]> disKafkaProducer = new DISKafkaProducer<String, byte[]>(disConfig);
////        disKafkaProducer.close();
//
//        // 配置流名称，该参数值要与开通DIS通道时填写的“通道名称”的值一致
//        String streamName = "DEV_RTM_DIS_Devops";
////        BasicConfigurator.configure();//自动快速地使用缺省Log4j环境。
//        // 模拟生成待发送的数据
//        //初始化词典
//        DicInitializer.init();
////        File templates = new File("/root/newrtm/templates");
////        File templates =new File("C:\\Users\\Administrator\\IdeaProjects\\newrtm\\templates");
//        File templates = new File("templates");
//        if(templates.isDirectory()){
//            File[] tplFiles = templates.listFiles();
//            for(File tplFile: tplFiles){
//                if(tplFile.isFile()){
//                    String tpl = FileUtils.readFileToString(tplFile);
//                    String tplName = tplFile.getName();
//                    TemplateAnalyzer testTplAnalyzer = new TemplateAnalyzer(tplName, tpl);
//                    for(int x = 0; x < 1; x = x+1) {
////                    while(true){
//                        String abc = testTplAnalyzer.analyse();
//                        System.out.println(abc);
//                        ByteBuffer buffer = ByteBuffer.wrap(abc.getBytes());
//                        long key = System.currentTimeMillis();
//                        ProducerRecord<String, byte[]> record = new ProducerRecord<String, byte[]>(streamName, String.valueOf(key), buffer.array());
////                        Future<RecordMetadata> future = disKafkaProducer.send(record);
//                        Thread.currentThread().sleep(10);
//                    }
//                }
//            }
//        }
//        disKafkaProducer.close();
//
//    }
}
