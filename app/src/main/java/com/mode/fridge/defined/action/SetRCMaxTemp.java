package com.mode.fridge.defined.action;

import com.mode.fridge.defined.ViomiDefined;
import com.mode.fridge.defined.property.RCMaxTemp;
import com.xiaomi.miot.typedef.device.operable.ActionOperable;
import com.xiaomi.miot.typedef.urn.ActionType;

public class SetRCMaxTemp extends ActionOperable {

    public static final ActionType TYPE = ViomiDefined.Action.setRCMaxTemp.toActionType();

    public SetRCMaxTemp() {
        super(TYPE);

        super.addArgument(RCMaxTemp.TYPE.toString());
    }
}