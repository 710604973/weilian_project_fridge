package com.mode.fridge.defined.action;

import com.mode.fridge.defined.ViomiDefined;
import com.mode.fridge.defined.property.InvadeAlarm;
import com.xiaomi.miot.typedef.device.operable.ActionOperable;
import com.xiaomi.miot.typedef.urn.ActionType;

public class SetInvadeAlarm extends ActionOperable {

    public static final ActionType TYPE = ViomiDefined.Action.setInvadeAlarm.toActionType();

    public SetInvadeAlarm() {
        super(TYPE);

        super.addArgument(InvadeAlarm.TYPE.toString());
    }
}