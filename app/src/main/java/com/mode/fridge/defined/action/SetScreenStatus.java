package com.mode.fridge.defined.action;

import com.mode.fridge.defined.ViomiDefined;
import com.mode.fridge.defined.property.ScreenStatus;
import com.xiaomi.miot.typedef.device.operable.ActionOperable;
import com.xiaomi.miot.typedef.urn.ActionType;

public class SetScreenStatus extends ActionOperable {

    public static final ActionType TYPE = ViomiDefined.Action.setScreenStatus.toActionType();

    public SetScreenStatus() {
        super(TYPE);

        super.addArgument(ScreenStatus.TYPE.toString());
    }
}