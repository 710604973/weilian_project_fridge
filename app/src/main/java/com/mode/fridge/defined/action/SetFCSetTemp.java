package com.mode.fridge.defined.action;

import com.mode.fridge.defined.ViomiDefined;
import com.mode.fridge.defined.property.FCSetTemp;
import com.xiaomi.miot.typedef.device.operable.ActionOperable;
import com.xiaomi.miot.typedef.urn.ActionType;

public class SetFCSetTemp extends ActionOperable {

    public static final ActionType TYPE = ViomiDefined.Action.setFCSetTemp.toActionType();

    public SetFCSetTemp() {
        super(TYPE);

        super.addArgument(FCSetTemp.TYPE.toString());
    }
}