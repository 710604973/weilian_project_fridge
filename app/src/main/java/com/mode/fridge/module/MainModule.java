package com.mode.fridge.module;



import com.mode.fridge.common.scope.ActivityScoped;
import com.mode.fridge.contract.MainContract;
import com.mode.fridge.presenter.MainPresenter;

import dagger.Binds;
import dagger.Module;

@Module
public abstract class MainModule {

    @ActivityScoped
    @Binds
    abstract MainContract.Presenter mainPresenter(MainPresenter presenter);
}
