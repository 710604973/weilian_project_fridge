package com.mode.fridge.defined.action;

import com.mode.fridge.defined.ViomiDefined;
import com.mode.fridge.defined.property.SmartFreeze;
import com.xiaomi.miot.typedef.device.operable.ActionOperable;
import com.xiaomi.miot.typedef.urn.ActionType;

public class SetSmartFreeze extends ActionOperable {

    public static final ActionType TYPE = ViomiDefined.Action.setSmartFreeze.toActionType();

    public SetSmartFreeze() {
        super(TYPE);

        super.addArgument(SmartFreeze.TYPE.toString());
    }
}