package com.mode.fridge.module;


import com.mode.fridge.common.scope.ActivityScoped;
import com.mode.fridge.contract.IotTypeContract;
import com.mode.fridge.contract.MainContract;
import com.mode.fridge.presenter.IotTypePresenter;
import com.mode.fridge.presenter.MainPresenter;

import dagger.Binds;
import dagger.Module;

/**
 * 主页 Module
 * Created by William on 2018/3/5.
 */
@Module
public abstract class HomeModule {

    @ActivityScoped
    @Binds
    abstract MainContract.Presenter mainPresenter(MainPresenter presenter);

    @ActivityScoped
    @Binds
    abstract IotTypeContract.Presenter iotScenePresenter(IotTypePresenter presenter);
}