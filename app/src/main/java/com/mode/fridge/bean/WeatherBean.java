package com.mode.fridge.bean;

import com.alibaba.fastjson.annotation.JSONField;

/**
 * 讯飞天气 bean
 * Created by William on 2018/3/4.
 */

public class WeatherBean {
    @JSONField(name = "airData")
    private int airData;// 空气质量指数
    @JSONField(name = "airQuality")
    private String airQuality;// 空气质量
    @JSONField(name = "city")
    private String city;// 城市
    @JSONField(name = "date")
    private String date;// 日期
    @JSONField(name = "dateLong")
    private long dateLong;// 日期时间戳（秒）
    @JSONField(name = "humidity")
    private String humidity;// 相对湿度
    @JSONField(name = "lastUpdateTime")
    private String lastUpdateTime;// 最后更新时间
    @JSONField(name = "pm25")
    private String pm25;// PM2.5
    @JSONField(name = "temp")
    private int temp;// 温度
    @JSONField(name = "tempRange")
    private String tempRange;// 温度范围
    @JSONField(name = "weather")
    private String weather;// 天气
    @JSONField(name = "weatherType")
    private int weatherType;// 天气类型
    @JSONField(name = "wind")
    private String wind;// 风力描述
    @JSONField(name = "windLevel")
    private int windLevel;// 风力

    public int getAirData() {
        return airData;
    }

    public void setAirData(int airData) {
        this.airData = airData;
    }

    public String getAirQuality() {
        return airQuality;
    }

    public void setAirQuality(String airQuality) {
        this.airQuality = airQuality;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public long getDateLong() {
        return dateLong;
    }

    public void setDateLong(long dateLong) {
        this.dateLong = dateLong;
    }

    public String getHumidity() {
        return humidity;
    }

    public void setHumidity(String humidity) {
        this.humidity = humidity;
    }

    public String getLastUpdateTime() {
        return lastUpdateTime;
    }

    public void setLastUpdateTime(String lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public String getPm25() {
        return pm25;
    }

    public void setPm25(String pm25) {
        this.pm25 = pm25;
    }

    public int getTemp() {
        return temp;
    }

    public void setTemp(int temp) {
        this.temp = temp;
    }

    public String getTempRange() {
        return tempRange;
    }

    public void setTempRange(String tempRange) {
        this.tempRange = tempRange;
    }

    public String getWeather() {
        return weather;
    }

    public void setWeather(String weather) {
        this.weather = weather;
    }

    public int getWeatherType() {
        return weatherType;
    }

    public void setWeatherType(int weatherType) {
        this.weatherType = weatherType;
    }

    public String getWind() {
        return wind;
    }

    public void setWind(String wind) {
        this.wind = wind;
    }

    public int getWindLevel() {
        return windLevel;
    }

    public void setWindLevel(int windLevel) {
        this.windLevel = windLevel;
    }

    @Override
    public String toString() {
        return "WeatherBean{" +
                "airData=" + airData +
                ", airQuality='" + airQuality + '\'' +
                ", city='" + city + '\'' +
                ", date='" + date + '\'' +
                ", dateLong=" + dateLong +
                ", humidity='" + humidity + '\'' +
                ", lastUpdateTime='" + lastUpdateTime + '\'' +
                ", pm25='" + pm25 + '\'' +
                ", temp=" + temp +
                ", tempRange='" + tempRange + '\'' +
                ", weather='" + weather + '\'' +
                ", weatherType=" + weatherType +
                ", wind='" + wind + '\'' +
                ", windLevel=" + windLevel +
                '}';
    }
}
