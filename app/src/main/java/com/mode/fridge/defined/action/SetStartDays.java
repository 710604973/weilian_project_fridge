package com.mode.fridge.defined.action;

import com.mode.fridge.defined.ViomiDefined;
import com.mode.fridge.defined.property.StartDays;
import com.xiaomi.miot.typedef.device.operable.ActionOperable;
import com.xiaomi.miot.typedef.urn.ActionType;

public class SetStartDays extends ActionOperable {

    public static final ActionType TYPE = ViomiDefined.Action.setStartDays.toActionType();

    public SetStartDays() {
        super(TYPE);

        super.addArgument(StartDays.TYPE.toString());
    }
}