package com.utils;


import java.sql.*;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class DBTest {

    //创建数据库连接。
    public static Connection GetConnection(String username, String passwd) {
        Config load = ConfigFactory.load();
        String driver =load.getString("dws_driver");
        String sourceURL =load.getString("sourceURL");
        Connection conn = null;
        try {
            //加载数据库驱动。
            Class.forName(driver).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        try {
            //创建数据库连接。
            conn = DriverManager.getConnection(sourceURL, username, passwd);
//            System.out.println("Connection succeed!");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        return conn;
    };


    //执行预处理语句，批量插入数据。
    public static void BatchInsertData(Connection conn,String sql) {
        PreparedStatement pst = null;

        try {
            //生成预处理语句。
//            pst = conn.prepareStatement("INSERT INTO tpch.carnum  VALUES (?,?,?)");
            pst = conn.prepareStatement(sql);
//            for (int i = 0; i < 3; i++) {
            //添加参数。
//                pst.setString(1, "2018-05-06 12:11:11");
//                pst.setString(2, "data " + i);
//                pst.setString(3, "data " + i);
            pst.addBatch();
//            }
            //执行批处理。
            pst.executeBatch();
            pst.close();
        } catch (SQLException e) {
            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
            e.printStackTrace();
        }
    }



}