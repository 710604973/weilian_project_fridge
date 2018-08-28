package com.mode.fridge.defined.action;

import com.mode.fridge.defined.ViomiDefined;
import com.mode.fridge.defined.property.StopMusic;
import com.xiaomi.miot.typedef.device.operable.ActionOperable;
import com.xiaomi.miot.typedef.urn.ActionType;

public class SetStopMusic extends ActionOperable {

    public static final ActionType TYPE = ViomiDefined.Action.setStopMusic.toActionType();

    public SetStopMusic() {
        super(TYPE);

        super.addArgument(StopMusic.TYPE.toString());
    }
}