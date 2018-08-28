package com.mode.fridge.defined.action;

import com.mode.fridge.defined.ViomiDefined;
import com.mode.fridge.defined.property.SceneChoose;
import com.xiaomi.miot.typedef.device.operable.ActionOperable;
import com.xiaomi.miot.typedef.urn.ActionType;

public class SetSceneChoose extends ActionOperable {

    public static final ActionType TYPE = ViomiDefined.Action.setSceneChoose.toActionType();

    public SetSceneChoose() {
        super(TYPE);

        super.addArgument(SceneChoose.TYPE.toString());
    }
}