package com.mode.fridge.defined.action;

import com.mode.fridge.defined.ViomiDefined;
import com.mode.fridge.defined.property.OneKeyClean;
import com.xiaomi.miot.typedef.device.operable.ActionOperable;
import com.xiaomi.miot.typedef.urn.ActionType;

public class SetOneKeyClean extends ActionOperable {

    public static final ActionType TYPE = ViomiDefined.Action.setOneKeyClean.toActionType();

    public SetOneKeyClean() {
        super(TYPE);

        super.addArgument(OneKeyClean.TYPE.toString());
    }
}