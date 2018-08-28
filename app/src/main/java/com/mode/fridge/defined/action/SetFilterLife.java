package com.mode.fridge.defined.action;

import com.mode.fridge.defined.ViomiDefined;
import com.mode.fridge.defined.property.FilterLife;
import com.xiaomi.miot.typedef.device.operable.ActionOperable;
import com.xiaomi.miot.typedef.urn.ActionType;

public class SetFilterLife extends ActionOperable {

    public static final ActionType TYPE = ViomiDefined.Action.setFilterLife.toActionType();

    public SetFilterLife() {
        super(TYPE);

        super.addArgument(FilterLife.TYPE.toString());
    }
}