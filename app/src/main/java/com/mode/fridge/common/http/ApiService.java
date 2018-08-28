package com.mode.fridge.common.http;


import com.mode.fridge.bean.QRCodeBase;
import com.mode.fridge.bean.prop.RPCResult;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Streaming;
import retrofit2.http.Url;
import rx.Observable;

/**
 * Http 请求 Api
 * Created by William on 2018/1/4.
 */
public interface ApiService {

    // 生成商城登录二维码
    @Headers({"domain:vmall"})
    @GET("services/vmall/login/QRCode.json")
    Observable<QRCodeBase> createLoginQRCode(@Query("type") String type, @Query("clientID") String clientId);

    // 检查商城登录状态
    @Headers({"domain:vmall"})
    @POST("services/vmall/login/QRCode.json")
    Observable<QRCodeBase> checkLoginStatus(@Body RequestBody requestBody);

    // 意见反馈
    @Headers({"domain:store"})
    @POST("/services/user/feedbacks.json")
    Observable<String> feedback(@Body RequestBody requestBody);

    // 小米开放接口
    @Headers({"domain:xiaomi"})
    @GET
    Observable<RPCResult> miOpen(@Url String url, @Query("data") String data, @Query("clientId") String clientId, @Query("accessToken") String accessToken);
//
//    /**
//     * @param lastId 本地最新消息id
//     * @return 获取活动促销
//     */
//    @Headers({"domain:message"})
//    @GET("/information/fridge21/activity/{infoId}")
//    Observable<RequestResult<List<ActivityMessage>>> getActivityMessages(@Path("infoId") String lastId);
//
//    /**
//     * @param yid    云米id
//     * @param lastId 本地最新消息id
//     * @return 获取商城消息
//     */
//    @Headers({"domain:message"})
//    @GET("/information/fridge21/user-related/{yid}/{infoId}")
//    Observable<RequestResult<List<MallMessage>>> getMallMessages(@Path("yid") String yid, @Path("infoId") String lastId);

    /**
     * @param mid    小米id
     * @param lastId 本地最新消息id
     * @return 获取系统消息
     */
    @Headers({"domain:message"})
    @GET("/information/fridge21/system/{mid}/{infoId}")
    Observable<String> getSystemMessages(@Path("mid") String mid, @Path("infoId") String lastId);

    /**
     * @param mid    小米id
     * @param lastId 本地最新消息id
     * @return 获取未读消息
     */
    @Headers({"domain:message"})
    @GET("/information/fridge21/checkNewInfos/{mid}/{yid}")
    Observable<String> getUnreadMessages(@Path("mid") String mid, @Path("yid") String lastId);

    /**
     * @param alias 请求内容
     * @return 上报系统消息别名
     */
    @Headers({"domain:message"})
    @FormUrlEncoded
    @POST("/information/alias")
    Observable<String> reportAlias(@Field("alias") String alias);

//    /**
//     * @return 获取菜谱分类
//     */
//    @Headers({"domain:mob"})
//    @GET("/v1/cook/category/query?key=2439c8105325d")
//    Observable<MobResult<CookMenuCategory>> getCookCategory();
//
//    /**
//     * @param cid  标签id
//     * @param name 菜谱名称
//     * @param page 起始页
//     * @param size 返回条数
//     * @return 查询菜谱
//     */
//    @Headers({"domain:mob"})
//    @GET("/v1/cook/menu/search?key=2439c8105325d")
//    Observable<MobResult<CookMenuSearch>> getCookSearch(@Query("cid") String cid,
//                                                        @Query("name") String name,
//                                                        @Query("page") int page,
//                                                        @Query("size") int size);

//    /**
//     * @param id 菜谱id
//     * @return 获取菜谱详情
//     */
//    @Headers({"domain:mob"})
//    @GET("/v1/cook/menu/query?key=2439c8105325d")
//    Observable<MobResult<CookMenuDetail>> getCookDetail(@Query("id") String id);
//
//    @Headers({"domain:download"})
//    @GET("getdata?type=version&&p=1&l=1")
//    Observable<AppUpdateResult> checkAppUpdate(@Query("package") String pack);

    // 南京物联，开灯
    @Headers({"domain:debug"})
    @POST("/wlink/switch/turnOn")
    Observable<String> openLight(@Body RequestBody requestBody);

    // 南京物联，关灯
    @Headers({"domain:debug"})
    @POST("/wlink/switch/turnOff")
    Observable<String> closeLight(@Body RequestBody requestBody);

    // 南京物联，开门
    @Headers({"domain:debug"})
    @POST("/wlink/door/unlock")
    Observable<String> openDoor(@Body RequestBody requestBody);

    // 南京物联，获取状态
    @Headers({"domain:debug"})
    @GET("/wlink/devicesStatus/did/{deviceId}")
    Observable<String> getStatus(@Path("deviceId") String deviceId);

    // 南京物联，开启
    @Headers({"domain:debug"})
    @PUT("wlink/switch/turnOn")
    Observable<String> statusOn(@Body RequestBody requestBody);

    // 南京物联，关闭
    @Headers({"domain:debug"})
    @PUT("wlink/switch/turnOff")
    Observable<String> statusOff(@Body RequestBody requestBody);

    // 南京物联，窗帘开
    @Headers({"domain:debug"})
    @PUT("wlink/curtain/turnOn")
    Observable<String> windowOn(@Body RequestBody requestBody);

    // 南京物联，窗帘关
    @Headers({"domain:debug"})
    @PUT("wlink/curtain/turnOff")
    Observable<String> windowOff(@Body RequestBody requestBody);

    // 南京物联，插座开
    @Headers({"domain:debug"})
    @PUT("wlink/socket/turnOn")
    Observable<String> socketOn(@Body RequestBody requestBody);

    // 南京物联，插座关
    @Headers({"domain:debug"})
    @PUT("wlink/socket/turnOff")
    Observable<String> socketOff(@Body RequestBody requestBody);

    /**
     * 下载文件
     *
     * @param url: 下载链接
     */
    @Streaming
    @GET
    Observable<ResponseBody> download(@Url String url);
}