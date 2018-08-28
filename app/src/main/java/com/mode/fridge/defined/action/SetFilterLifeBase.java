package com.mode.fridge.defined.action;

import com.mode.fridge.defined.ViomiDefined;
import com.mode.fridge.defined.property.FilterLifeBase;
import com.xiaomi.miot.typedef.device.operable.ActionOperable;
import com.xiaomi.miot.typedef.urn.ActionType;

public class SetFilterLifeBase extends ActionOperable {

    public static final ActionType TYPE = ViomiDefined.Action.setFilterLifeBase.toActionType();

    public SetFilterLifeBase() {
        super(TYPE);

        super.addArgument(FilterLifeBase.TYPE.toString());
    }
}