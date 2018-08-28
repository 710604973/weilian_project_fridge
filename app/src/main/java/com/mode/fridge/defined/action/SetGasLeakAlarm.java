package com.mode.fridge.defined.action;

import com.mode.fridge.defined.ViomiDefined;
import com.mode.fridge.defined.property.GasLeakAlarm;
import com.xiaomi.miot.typedef.device.operable.ActionOperable;
import com.xiaomi.miot.typedef.urn.ActionType;

public class SetGasLeakAlarm extends ActionOperable {

    public static final ActionType TYPE = ViomiDefined.Action.setGasLeakAlarm.toActionType();

    public SetGasLeakAlarm() {
        super(TYPE);

        super.addArgument(GasLeakAlarm.TYPE.toString());
    }
}