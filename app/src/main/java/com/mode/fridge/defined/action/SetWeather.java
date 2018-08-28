package com.mode.fridge.defined.action;

import com.mode.fridge.defined.ViomiDefined;
import com.mode.fridge.defined.property.Weather;
import com.xiaomi.miot.typedef.device.operable.ActionOperable;
import com.xiaomi.miot.typedef.urn.ActionType;

public class SetWeather extends ActionOperable {

    public static final ActionType TYPE = ViomiDefined.Action.setWeather.toActionType();

    public SetWeather() {
        super(TYPE);

        super.addArgument(Weather.TYPE.toString());
    }
}