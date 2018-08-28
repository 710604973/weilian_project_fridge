package com.mode.fridge.defined.action;

import com.mode.fridge.defined.ViomiDefined;
import com.mode.fridge.defined.property.IndoorTemp;
import com.xiaomi.miot.typedef.device.operable.ActionOperable;
import com.xiaomi.miot.typedef.urn.ActionType;

public class SetIndoorTemp extends ActionOperable {

    public static final ActionType TYPE = ViomiDefined.Action.setIndoorTemp.toActionType();

    public SetIndoorTemp() {
        super(TYPE);

        super.addArgument(IndoorTemp.TYPE.toString());
    }
}