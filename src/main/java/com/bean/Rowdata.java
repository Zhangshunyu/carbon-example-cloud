package com.bean;

import com.alibaba.fastjson.JSON;

public class Rowdata implements java.io.Serializable {
    private String rowkey;
    private String vin;
    private String data;
    private String Vehicle_type;
    private String Energy_type;
    private String pro;
    private String city;
    private String weekend;
    private String speedRand;
    private String VehRunMode;
    private String Brand;
    private String V048;//车辆状态
    private String E001;//车辆充放电状态
    //    private String V049;//运行模式
//    private String V019;//车速
    private String V015;//累计里程
    private String E008;//总电压
    private String E009;//总电流
    private String E010;//电池点亮soc
    private String E031;//电机扭矩区间
    //    private String GPS002;//经度
//    private String GPS003;//纬度
    private String speed;// 速度
    private String SOCRange;//soc区间
    private String EMTorque;//电机扭矩
    private String AcceleratorRange;// 踏板行程区间
    private String Charging_type;//充电模式
    private String data_exception;//数据异常标志
    private String E062;//电池最高温度值
    private String Charging_current_range;//总电流区间
    private String Battery_voltage_range;//总电压区间
    private String Vehicle_state;//汽车驾驶状态
    private String Battery_temperature_range; //电池温度区间
    private String Motor_temperature_range; //电机温度区间
    private String row;

    public Rowdata(String rowkey, String vin, String data, String vehicle_type, String energy_type, String pro, String city, String weekend, String speedRand, String vehRunMode, String brand, String v048, String e001, String v015, String e008, String e009, String e010, String e031, String speed, String SOCRange, String EMTorque, String acceleratorRange, String charging_type, String data_exception, String e062, String charging_current_range, String battery_voltage_range, String vehicle_state, String battery_temperature_range, String motor_temperature_range,String row) {
        this.rowkey = rowkey;
        this.vin = vin;
        this.data = data;
        Vehicle_type = vehicle_type;
        Energy_type = energy_type;
        this.pro = pro;
        this.city = city;
        this.weekend = weekend;
        this.speedRand = speedRand;
        VehRunMode = vehRunMode;
        Brand = brand;
        V048 = v048;
        E001 = e001;
        V015 = v015;
        E008 = e008;
        E009 = e009;
        E010 = e010;
        E031 = e031;
        this.speed = speed;
        this.SOCRange = SOCRange;
        this.EMTorque = EMTorque;
        AcceleratorRange = acceleratorRange;
        Charging_type = charging_type;
        this.data_exception = data_exception;
        E062 = e062;
        Charging_current_range = charging_current_range;
        Battery_voltage_range = battery_voltage_range;
        Vehicle_state = vehicle_state;
        Battery_temperature_range = battery_temperature_range;
        Motor_temperature_range = motor_temperature_range;
        this.row=row;
    }

    public String getRow() {
        return row;
    }

    public void setRow(String row) {
        this.row = row;
    }

    public String getRowkey() {
        return rowkey;
    }

    public void setRowkey(String rowkey) {
        this.rowkey = rowkey;
    }

    public String getVin() {
        return vin;
    }

    public void setVin(String vin) {
        this.vin = vin;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getVehicle_type() {
        return Vehicle_type;
    }

    public void setVehicle_type(String vehicle_type) {
        Vehicle_type = vehicle_type;
    }

    public String getEnergy_type() {
        return Energy_type;
    }

    public void setEnergy_type(String energy_type) {
        Energy_type = energy_type;
    }

    public String getPro() {
        return pro;
    }

    public void setPro(String pro) {
        this.pro = pro;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getWeekend() {
        return weekend;
    }

    public void setWeekend(String weekend) {
        this.weekend = weekend;
    }

    public String getSpeedRand() {
        return speedRand;
    }

    public void setSpeedRand(String speedRand) {
        this.speedRand = speedRand;
    }

    public String getVehRunMode() {
        return VehRunMode;
    }

    public void setVehRunMode(String vehRunMode) {
        VehRunMode = vehRunMode;
    }

    public String getBrand() {
        return Brand;
    }

    public void setBrand(String brand) {
        Brand = brand;
    }

    public String getV048() {
        return V048;
    }

    public void setV048(String v048) {
        V048 = v048;
    }

    public String getE001() {
        return E001;
    }

    public void setE001(String e001) {
        E001 = e001;
    }

    public String getV015() {
        return V015;
    }

    public void setV015(String v015) {
        V015 = v015;
    }

    public String getE008() {
        return E008;
    }

    public void setE008(String e008) {
        E008 = e008;
    }

    public String getE009() {
        return E009;
    }

    public void setE009(String e009) {
        E009 = e009;
    }

    public String getE010() {
        return E010;
    }

    public void setE010(String e010) {
        E010 = e010;
    }

    public String getE031() {
        return E031;
    }

    public void setE031(String e031) {
        E031 = e031;
    }

    public String getSpeed() {
        return speed;
    }

    public void setSpeed(String speed) {
        this.speed = speed;
    }

    public String getSOCRange() {
        return SOCRange;
    }

    public void setSOCRange(String SOCRange) {
        this.SOCRange = SOCRange;
    }

    public String getEMTorque() {
        return EMTorque;
    }

    public void setEMTorque(String EMTorque) {
        this.EMTorque = EMTorque;
    }

    public String getAcceleratorRange() {
        return AcceleratorRange;
    }

    public void setAcceleratorRange(String acceleratorRange) {
        AcceleratorRange = acceleratorRange;
    }

    public String getCharging_type() {
        return Charging_type;
    }

    public void setCharging_type(String charging_type) {
        Charging_type = charging_type;
    }

    public String getData_exception() {
        return data_exception;
    }

    public void setData_exception(String data_exception) {
        this.data_exception = data_exception;
    }

    public String getE062() {
        return E062;
    }

    public void setE062(String e062) {
        E062 = e062;
    }

    public String getCharging_current_range() {
        return Charging_current_range;
    }

    public void setCharging_current_range(String charging_current_range) {
        Charging_current_range = charging_current_range;
    }

    public String getBattery_voltage_range() {
        return Battery_voltage_range;
    }

    public void setBattery_voltage_range(String battery_voltage_range) {
        Battery_voltage_range = battery_voltage_range;
    }

    public String getVehicle_state() {
        return Vehicle_state;
    }

    public void setVehicle_state(String vehicle_state) {
        Vehicle_state = vehicle_state;
    }

    public String getBattery_temperature_range() {
        return Battery_temperature_range;
    }

    public void setBattery_temperature_range(String battery_temperature_range) {
        Battery_temperature_range = battery_temperature_range;
    }

    public String getMotor_temperature_range() {
        return Motor_temperature_range;
    }

    public void setMotor_temperature_range(String motor_temperature_range) {
        Motor_temperature_range = motor_temperature_range;
    }

    public String toString() {
        return JSON.toJSONString(this);
    }

}
