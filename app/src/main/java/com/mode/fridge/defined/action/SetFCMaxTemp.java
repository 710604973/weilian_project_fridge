package com.mode.fridge.defined.action;

import com.mode.fridge.defined.ViomiDefined;
import com.mode.fridge.defined.property.FCMaxTemp;
import com.xiaomi.miot.typedef.device.operable.ActionOperable;
import com.xiaomi.miot.typedef.urn.ActionType;

public class SetFCMaxTemp extends ActionOperable {

    public static final ActionType TYPE = ViomiDefined.Action.setFCMaxTemp.toActionType();

    public SetFCMaxTemp() {
        super(TYPE);

        super.addArgument(FCMaxTemp.TYPE.toString());
    }
}