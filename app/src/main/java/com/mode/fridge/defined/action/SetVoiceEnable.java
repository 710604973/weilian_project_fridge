package com.mode.fridge.defined.action;

import com.mode.fridge.defined.ViomiDefined;
import com.mode.fridge.defined.property.VoiceEnable;
import com.xiaomi.miot.typedef.device.operable.ActionOperable;
import com.xiaomi.miot.typedef.urn.ActionType;

public class SetVoiceEnable extends ActionOperable {

    public static final ActionType TYPE = ViomiDefined.Action.setVoiceEnable.toActionType();

    public SetVoiceEnable() {
        super(TYPE);

        super.addArgument(VoiceEnable.TYPE.toString());
    }
}