package com.mode.fridge.defined.action;

import com.mode.fridge.defined.ViomiDefined;
import com.mode.fridge.defined.property.RCMinTemp;
import com.xiaomi.miot.typedef.device.operable.ActionOperable;
import com.xiaomi.miot.typedef.urn.ActionType;

public class SetRCMinTemp extends ActionOperable {

    public static final ActionType TYPE = ViomiDefined.Action.setRCMinTemp.toActionType();

    public SetRCMinTemp() {
        super(TYPE);

        super.addArgument(RCMinTemp.TYPE.toString());
    }
}