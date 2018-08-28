package com.mode.fridge.defined.action;

import com.mode.fridge.defined.ViomiDefined;
import com.mode.fridge.defined.property.PlayLightMusic;
import com.xiaomi.miot.typedef.device.operable.ActionOperable;
import com.xiaomi.miot.typedef.urn.ActionType;

public class SetPlayLightMusic extends ActionOperable {

    public static final ActionType TYPE = ViomiDefined.Action.setPlayLightMusic.toActionType();

    public SetPlayLightMusic() {
        super(TYPE);

        super.addArgument(PlayLightMusic.TYPE.toString());
    }
}