package com.mode.fridge.defined.action;

import com.mode.fridge.defined.ViomiDefined;
import com.mode.fridge.defined.property.Mode;
import com.xiaomi.miot.typedef.device.operable.ActionOperable;
import com.xiaomi.miot.typedef.urn.ActionType;

public class SetMode extends ActionOperable {

    public static final ActionType TYPE = ViomiDefined.Action.setMode.toActionType();

    public SetMode() {
        super(TYPE);

        super.addArgument(Mode.TYPE.toString());
    }
}