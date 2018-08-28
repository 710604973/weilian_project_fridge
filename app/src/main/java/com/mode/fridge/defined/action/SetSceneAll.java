package com.mode.fridge.defined.action;

import com.mode.fridge.defined.ViomiDefined;
import com.mode.fridge.defined.property.SceneAll;
import com.xiaomi.miot.typedef.device.operable.ActionOperable;
import com.xiaomi.miot.typedef.urn.ActionType;

public class SetSceneAll extends ActionOperable {

    public static final ActionType TYPE = ViomiDefined.Action.setSceneAll.toActionType();

    public SetSceneAll() {
        super(TYPE);

        super.addArgument(SceneAll.TYPE.toString());
    }
}