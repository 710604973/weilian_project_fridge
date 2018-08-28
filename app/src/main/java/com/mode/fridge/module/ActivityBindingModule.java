package com.mode.fridge.module;

import com.mode.fridge.activity.LoginGuideActivity;
import com.mode.fridge.activity.LogoutActivity;
import com.mode.fridge.activity.MainIotActivity;
import com.mode.fridge.activity.web.MallWebActivity;
import com.mode.fridge.common.scope.ActivityScoped;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

/**
 * 继承 BaseActivity 需在此类注册
 * Created by nanquan on 2018/1/6.
 */
@Module
public abstract class ActivityBindingModule {

    @ActivityScoped
    @ContributesAndroidInjector(modules = MainIotModule.class)
    abstract MainIotActivity mainIotActivity();// 启动页

    @ActivityScoped
    @ContributesAndroidInjector(modules = LogoutModule.class)
    abstract LogoutActivity logoutActivity();// 註銷页

    @ActivityScoped
    @ContributesAndroidInjector
    abstract LoginGuideActivity loginGuideActivity();// 登录指引

    @ActivityScoped
    @ContributesAndroidInjector
    abstract MallWebActivity mallWebActivity();// 浏览器

}