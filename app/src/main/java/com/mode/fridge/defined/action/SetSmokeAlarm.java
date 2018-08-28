package com.mode.fridge.defined.action;

import com.mode.fridge.defined.ViomiDefined;
import com.mode.fridge.defined.property.SmokeAlarm;
import com.xiaomi.miot.typedef.device.operable.ActionOperable;
import com.xiaomi.miot.typedef.urn.ActionType;

public class SetSmokeAlarm extends ActionOperable {

    public static final ActionType TYPE = ViomiDefined.Action.setSmokeAlarm.toActionType();

    public SetSmokeAlarm() {
        super(TYPE);

        super.addArgument(SmokeAlarm.TYPE.toString());
    }
}