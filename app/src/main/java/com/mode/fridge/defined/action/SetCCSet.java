package com.mode.fridge.defined.action;

import com.mode.fridge.defined.ViomiDefined;
import com.mode.fridge.defined.property.CCSet;
import com.xiaomi.miot.typedef.device.operable.ActionOperable;
import com.xiaomi.miot.typedef.urn.ActionType;

public class SetCCSet extends ActionOperable {

    public static final ActionType TYPE = ViomiDefined.Action.setCCSet.toActionType();

    public SetCCSet() {
        super(TYPE);

        super.addArgument(CCSet.TYPE.toString());
    }
}