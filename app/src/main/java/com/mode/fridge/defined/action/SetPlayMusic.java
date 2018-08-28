package com.mode.fridge.defined.action;

import com.mode.fridge.defined.ViomiDefined;
import com.mode.fridge.defined.property.PlayMusic;
import com.xiaomi.miot.typedef.device.operable.ActionOperable;
import com.xiaomi.miot.typedef.urn.ActionType;

public class SetPlayMusic extends ActionOperable {

    public static final ActionType TYPE = ViomiDefined.Action.setPlayMusic.toActionType();

    public SetPlayMusic() {
        super(TYPE);

        super.addArgument(PlayMusic.TYPE.toString());
    }
}