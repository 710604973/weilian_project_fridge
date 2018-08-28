package com.mode.fridge.defined.action;

import com.mode.fridge.defined.ViomiDefined;
import com.mode.fridge.defined.property.SmartCool;
import com.xiaomi.miot.typedef.device.operable.ActionOperable;
import com.xiaomi.miot.typedef.urn.ActionType;

public class SetSmartCool extends ActionOperable {

    public static final ActionType TYPE = ViomiDefined.Action.setSmartCool.toActionType();

    public SetSmartCool() {
        super(TYPE);

        super.addArgument(SmartCool.TYPE.toString());
    }
}