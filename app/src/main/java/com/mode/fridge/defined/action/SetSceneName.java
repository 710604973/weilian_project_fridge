package com.mode.fridge.defined.action;

import com.mode.fridge.defined.ViomiDefined;
import com.mode.fridge.defined.property.SceneName;
import com.xiaomi.miot.typedef.device.operable.ActionOperable;
import com.xiaomi.miot.typedef.urn.ActionType;

public class SetSceneName extends ActionOperable {

    public static final ActionType TYPE = ViomiDefined.Action.setSceneName.toActionType();

    public SetSceneName() {
        super(TYPE);

        super.addArgument(SceneName.TYPE.toString());
    }
}