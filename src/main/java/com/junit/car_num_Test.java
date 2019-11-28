package com.junit;

import com.utils.tools;
import org.junit.Before;
import org.junit.Test;

public class car_num_Test {
    real_data_junit c = null;
    tools tools=null;

    @Before
    public void testBeforeClass(){
        c = new real_data_junit();
        tools = new com.utils.tools();
    }
    @Test
    public void testA(){
        int result = c.add(1, 2);
        //Assert.assertEquals(result, 3);
        //等价于：
        if(result == 3){
            System.out.println("方法正确");
        }
    }

    @Test
    public void testS(){
        int result = c.sub(2, 1);
        //Assert.assertEquals(result, 1);
        //等价于：
        if(result == 1){
            System.out.println("方法正确");
        }
    }
    @Test
    public void testmult(){
        int result = c.multiply(2, 1);
        if(result == 2){
            System.out.println("方法正确");
        }
    }
    @Test
    public void testdivide(){
        int result = c.multiply(2, 1);
        if(result == 2){
            System.out.println("方法正确");
        }
    }
    @Test
    public void testhase(){
        String result =tools.Hash("fsf");
        if(Integer.parseInt(result)<24){
            System.out.println("方法正确");
        }
    }
    @Test
    public void testisWeekend() throws Exception{
        String a="2018-08-16 03:13:38";
        String weekend = tools.isWeekend(a);
        if(weekend.equals("1")){
            System.out.println("方法正确");
        }
    }
    @Test
    public void testisZE() throws Exception{
        String a="2018-08-16 03:13:38";
        String weekend = tools.ZE("北京");
        if(weekend.equals("Beijing")){
            System.out.println("方法正确");
        }
    }
    @Test
    public void testrf(){
        int result = c.add(1, 2);
        //Assert.assertEquals(result, 3);
        //等价于：
        if(result == 3){
            System.out.println("方法正确");
        }
    }
    @Test
    public void testes(){
        int result = c.add(1, 2);
        //Assert.assertEquals(result, 3);
        //等价于：
        if(result == 3){
            System.out.println("方法正确");
        }
    }
    @Test
    public void testgt(){
        int result = c.add(1, 2);
        //Assert.assertEquals(result, 3);
        //等价于：
        if(result == 3){
            System.out.println("方法正确");
        }
    }
    @Test
    public void tested(){
        int result = c.add(1, 2);
        //Assert.assertEquals(result, 3);
        //等价于：
        if(result == 3){
            System.out.println("方法正确");
        }
    }
    @Test
    public void testug(){
        int result = c.add(1, 2);
        //Assert.assertEquals(result, 3);
        //等价于：
        if(result == 3){
            System.out.println("方法正确");
        }
    }
    @Test
    public void testok(){
        int result = c.add(1, 2);
        //Assert.assertEquals(result, 3);
        //等价于：
        if(result == 3){
            System.out.println("方法正确");
        }
    }
    @Test
    public void testtv(){
        int result = c.add(1, 2);
        //Assert.assertEquals(result, 3);
        //等价于：
        if(result == 3){
            System.out.println("方法正确");
        }
    }
    @Test
    public void testws(){
        int result = c.add(1, 2);
        //Assert.assertEquals(result, 3);
        //等价于：
        if(result == 3){
            System.out.println("方法正确");
        }
    }
    @Test
    public void testqa(){
        int result = c.add(1, 2);
        //Assert.assertEquals(result, 3);
        //等价于：
        if(result == 3){
            System.out.println("方法正确");
        }
    }
    @Test
    public void testaz(){
        int result = c.add(1, 2);
        //Assert.assertEquals(result, 3);
        //等价于：
        if(result == 3){
            System.out.println("方法正确");
        }
    }
}