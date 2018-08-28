package com.mode.fridge.defined.action;

import com.mode.fridge.defined.ViomiDefined;
import com.mode.fridge.defined.property.Wakeup;
import com.xiaomi.miot.typedef.device.operable.ActionOperable;
import com.xiaomi.miot.typedef.urn.ActionType;

public class SetWakeup extends ActionOperable {

    public static final ActionType TYPE = ViomiDefined.Action.setWakeup.toActionType();

    public SetWakeup() {
        super(TYPE);

        super.addArgument(Wakeup.TYPE.toString());
    }
}