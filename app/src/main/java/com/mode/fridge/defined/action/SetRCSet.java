package com.mode.fridge.defined.action;

import com.mode.fridge.defined.ViomiDefined;
import com.mode.fridge.defined.property.RCSet;
import com.xiaomi.miot.typedef.device.operable.ActionOperable;
import com.xiaomi.miot.typedef.urn.ActionType;

public class SetRCSet extends ActionOperable {

    public static final ActionType TYPE = ViomiDefined.Action.setRCSet.toActionType();

    public SetRCSet() {
        super(TYPE);

        super.addArgument(RCSet.TYPE.toString());
    }
}