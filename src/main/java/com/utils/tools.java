package com.utils;


import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class tools {

    //哈希函数
    public static String Hash(String str) {
        int num = Math.abs(str.hashCode() % 100);
        DecimalFormat df = new DecimalFormat("00");
        String key = df.format(num);
        return key;
    }

    //判断日期是否为工作日
    public static String isWeekend(String bDate) throws ParseException {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");

        Date bdate = df.parse(bDate);
        Calendar cal = Calendar.getInstance();
        cal.setTime(bdate);
        if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            return "0";
        } else {
            return "1";
        }

    }

    //判断日期是否为工作日
    public static String isWeekend() throws ParseException {
        Date bdate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(bdate);
        if (cal.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || cal.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
            return "0";
        } else {
            return "1";
        }
    }

    //定时器
    public static void timer4(String vin) {

        Calendar calendar = Calendar.getInstance();
//        calendar.set(Calendar.HOUR_OF_DAY, 16); // 控制时
//        calendar.set(Calendar.MINUTE, 43);       // 控制分
//        calendar.set(Calendar.SECOND, 0);       // 控制秒
//
        Date time = calendar.getTime();         // 得出执行任务的时间,此处为今天的12：00：00

//        if(){
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                System.out.println("qqqqqqq");
            }
        },time,1000);// 这里设定将延时每天固定执行
//        }

    }

    //大小写转换
    public static String ZE(String a) throws Exception {
        PinyinTool tool = new PinyinTool();
        PinyinTool.Type firstupper = PinyinTool.Type.FIRSTUPPER;
        PinyinTool.Type lowee = PinyinTool.Type.LOWERCASE;
        char[] chars = a.toCharArray();
        String aa="";
        for(int i=0;i<chars.length;i++){
            if(i==0){
                aa=aa.concat(tool.toPinYin(String.valueOf(chars[i]), "", firstupper));
            }
            else aa=aa.concat(tool.toPinYin(String.valueOf(chars[i]), "", lowee));
        }
        return aa;
    }


}


