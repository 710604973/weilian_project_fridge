package com.mode.fridge.module;


import com.mode.fridge.common.scope.ActivityScoped;
import com.mode.fridge.contract.IotTypeContract;
import com.mode.fridge.contract.MainContract;
import com.mode.fridge.contract.ManageContract;
import com.mode.fridge.presenter.IotTypePresenter;
import com.mode.fridge.presenter.MainPresenter;
import com.mode.fridge.presenter.ManagePresenter;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class MainIotModule {


    @ActivityScoped
    @Binds
    abstract MainContract.Presenter mainPresenter(MainPresenter presenter);

    @ActivityScoped
    @Binds
    abstract IotTypeContract.Presenter iotScenePresenter(IotTypePresenter presenter);

    @ActivityScoped
    @Binds
    abstract ManageContract.Presenter managePresenter(ManagePresenter presenter);
}
