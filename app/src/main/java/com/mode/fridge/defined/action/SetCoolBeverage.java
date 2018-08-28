package com.mode.fridge.defined.action;

import com.mode.fridge.defined.ViomiDefined;
import com.mode.fridge.defined.property.CoolBeverage;
import com.xiaomi.miot.typedef.device.operable.ActionOperable;
import com.xiaomi.miot.typedef.urn.ActionType;

public class SetCoolBeverage extends ActionOperable {

    public static final ActionType TYPE = ViomiDefined.Action.setCoolBeverage.toActionType();

    public SetCoolBeverage() {
        super(TYPE);

        super.addArgument(CoolBeverage.TYPE.toString());
    }
}