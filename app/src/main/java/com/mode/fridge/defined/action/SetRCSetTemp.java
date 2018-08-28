package com.mode.fridge.defined.action;

import com.mode.fridge.defined.ViomiDefined;
import com.mode.fridge.defined.property.RCSetTemp;
import com.xiaomi.miot.typedef.device.operable.ActionOperable;
import com.xiaomi.miot.typedef.urn.ActionType;

public class SetRCSetTemp extends ActionOperable {

    public static final ActionType TYPE = ViomiDefined.Action.setRCSetTemp.toActionType();

    public SetRCSetTemp() {
        super(TYPE);

        super.addArgument(RCSetTemp.TYPE.toString());
    }
}