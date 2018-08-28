package com.mode.fridge.contract;

import com.mode.fridge.bean.WeatherBean;
import com.mode.fridge.callback.BasePresenter;

import java.util.List;

/**
 * 首页 Contract
 */
public interface MainContract {
    interface View {
//        void displayAlbum(List<Uri> list);// 显示相册

//        void refreshFridgeUi(DeviceParams params);// 刷新冰箱 Ui

        void refreshCity(String city);// 刷新定位城市

        void refreshWeather(List<WeatherBean> list);// 刷新天气

//        void refreshMessageBoard(List<MessageBoardEntity> list);// 留言板刷新
//        void refreshCookbook(CookMenuDetail cookMenuDetail);// 菜谱刷新
    }

    interface Presenter extends BasePresenter<View> {
//        void loadAlbum();// 读取相册
//
//        void loadMessageBoard();// 读取留言板
//
//        void loadCookBook();// 读取菜谱
//
//        void loadCookbookCategory();// 读取菜谱分类
//
//        void loadCookbookRecommend();// 读取推荐菜谱
//
//        void loadFridge();// 读取冰箱设置温度

        void location();// 定位城市

        void loadWeather(String cityName);// 获取天气
    }
}