package com.mode.fridge.defined.action;

import com.mode.fridge.defined.ViomiDefined;
import com.mode.fridge.defined.property.FCMinTemp;
import com.xiaomi.miot.typedef.device.operable.ActionOperable;
import com.xiaomi.miot.typedef.urn.ActionType;

public class SetFCMinTemp extends ActionOperable {

    public static final ActionType TYPE = ViomiDefined.Action.setFCMinTemp.toActionType();

    public SetFCMinTemp() {
        super(TYPE);

        super.addArgument(FCMinTemp.TYPE.toString());
    }
}