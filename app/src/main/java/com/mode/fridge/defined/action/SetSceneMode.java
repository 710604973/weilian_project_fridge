package com.mode.fridge.defined.action;

import com.mode.fridge.defined.ViomiDefined;
import com.mode.fridge.defined.property.SceneMode;
import com.xiaomi.miot.typedef.device.operable.ActionOperable;
import com.xiaomi.miot.typedef.urn.ActionType;

public class SetSceneMode extends ActionOperable {

    public static final ActionType TYPE = ViomiDefined.Action.setSceneMode.toActionType();

    public SetSceneMode() {
        super(TYPE);

        super.addArgument(SceneMode.TYPE.toString());
    }
}