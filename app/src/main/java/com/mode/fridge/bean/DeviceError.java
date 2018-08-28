package com.mode.fridge.bean;

/**
 * Created by young2 on 2017/3/15.
 */

public class DeviceError {
    public   boolean error_traffic;//通信故障
    public   boolean error_ccroom_sencor;//冷藏室传感器故障
    public   boolean error_tcroom_sencor;//变温室传感器故障
    public   boolean error_fzroom_sencor;//冷冻室传感器故障
    public   boolean error_cc_defrost_sencor;//冷藏化霜传感器故障
    public   boolean error_fz_defrost_sencor;//冷冻化霜传感器故障
    public   boolean error_indoor_sencor;//环境温度传感器故障
    public   boolean error_air_door;//风门故障
    public   boolean error_ccroom_fan;//冷藏风扇故障
    public   boolean error_tcroom_fan;//冷凝风扇故障
    public   boolean error_fzroom_fan;//冷冻风扇故障

    public  final static int int_error_traffic=0x0001;//通信故障
    public  final static int int_error_ccroom_sencor=0x0002;//冷藏室传感器故障
    public  final static int int_error_tcroom_sencor=0x0004;//变温室传感器故障
    public  final static int int_error_fzroom_sencor=0x0008;//冷冻室传感器故障
    public  final static int int_error_cc_defrost_sencor=0x0010;//冷藏化霜传感器故障
    public  final static int int_error_fz_defrost_sencor=0x0020;//冷冻化霜传感器故障
    public  final static int int_error_indoor_sencor=0x0040;//环境温度传感器故障
    public  final static int int_error_air_door=0x0080;//风门故障
    public  final static int int_error_ccroom_fan=0x0100;//冷藏风扇故障
    public  final static int int_error_tcroom_fan=0x0200;//冷凝风扇故障
    public  final static int int_error_fzroom_fan=0x0400;//冷冻风扇故障

    /***
     * 通信故障
     * 冷藏室传感器故障
     * 变温室传感器故障
     * 冷冻室传感器故障
     * 冷藏化霜传感器故障
     * 冷冻化霜传感器故障
     * 环境温度传感器故障
     * 风门故障
     * 冷冻风机故障
     * 冷藏风扇故障
     * 冷凝风扇故障
     * 冷冻风扇故障
     *
     */

    /***
     * 解析故障代码
     * @param error
     */
    public void parserErrorCode(int error){

        int code=0x0001;
        if((error&code)==code){
            error_traffic=true;
        }else {
            error_traffic=false;
        }

         code=0x0002;
        if((error&code)==code){
            error_ccroom_sencor=true;
        }else {
            error_ccroom_sencor=false;
        }

        code=0x0004;
        if((error&code)==code){
            error_tcroom_sencor=true;
        }else {
            error_tcroom_sencor=false;
        }

        code=0x0008;
        if((error&code)==code){
            error_fzroom_sencor=true;
        }else {
            error_fzroom_sencor=false;
        }
        code=0x0010;
        if((error&code)==code){
            error_cc_defrost_sencor=true;
        }else {
            error_cc_defrost_sencor=false;
        }
        code=0x0020;
        if((error&code)==code){
            error_fz_defrost_sencor=true;
        }else {
            error_fz_defrost_sencor=false;
        }
        code=0x0040;
        if((error&code)==code){
            error_indoor_sencor=true;
        }else {
            error_indoor_sencor=false;
        }
        code=0x0080;
        if((error&code)==code){
            error_air_door=true;
        }else {
            error_air_door=false;
        }

        code=0x0100;
        if((error&code)==code){
            error_ccroom_fan=true;
        }else {
            error_ccroom_fan=false;
        }

        code=0x0200;
        if((error&code)==code){
            error_tcroom_fan=true;
        }else {
            error_tcroom_fan=false;
        }

        code=0x0400;
        if((error&code)==code){
            error_fzroom_fan=true;
        }else {
            error_fzroom_fan=false;
        }

    }

    /***
     * 获取故障代码
     * @return
     */
    public int getErrorCode(){
        int error=0;
        if(error_traffic){
            error=error|0x01;
        }
        if(error_ccroom_sencor){
            error=error|0x02;
        }
        if(error_tcroom_sencor){
            error=error|0x04;
        }
        if(error_fzroom_sencor){
            error=error|0x08;
        }
        if(error_cc_defrost_sencor){
            error=error|0x10;
        }
        if(error_fz_defrost_sencor){
            error=error|0x20;
        }
        if(error_indoor_sencor){
            error=error|0x40;
        }
        if(error_air_door){
            error=error|0x80;
        }
        if(error_ccroom_fan){
            error=error|0x0100;
        }
        if(error_tcroom_fan){
            error=error|0x0200;
        }
        if(error_fzroom_fan){
            error=error|0x0400;
        }
        return error;
    }
}
