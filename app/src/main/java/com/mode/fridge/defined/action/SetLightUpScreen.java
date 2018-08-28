package com.mode.fridge.defined.action;

import com.mode.fridge.defined.ViomiDefined;
import com.mode.fridge.defined.property.LightUpScreen;
import com.xiaomi.miot.typedef.device.operable.ActionOperable;
import com.xiaomi.miot.typedef.urn.ActionType;

public class SetLightUpScreen extends ActionOperable {

    public static final ActionType TYPE = ViomiDefined.Action.setLightUpScreen.toActionType();

    public SetLightUpScreen() {
        super(TYPE);

        super.addArgument(LightUpScreen.TYPE.toString());
    }
}