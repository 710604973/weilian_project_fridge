package com.mode.fridge.defined.action;

import com.mode.fridge.defined.ViomiDefined;
import com.mode.fridge.defined.property.WaterAlarm;
import com.xiaomi.miot.typedef.device.operable.ActionOperable;
import com.xiaomi.miot.typedef.urn.ActionType;

public class SetWaterAlarm extends ActionOperable {

    public static final ActionType TYPE = ViomiDefined.Action.setWaterAlarm.toActionType();

    public SetWaterAlarm() {
        super(TYPE);

        super.addArgument(WaterAlarm.TYPE.toString());
    }
}