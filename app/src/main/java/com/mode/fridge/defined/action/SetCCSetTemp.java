package com.mode.fridge.defined.action;

import com.mode.fridge.defined.ViomiDefined;
import com.mode.fridge.defined.property.CCSetTemp;
import com.xiaomi.miot.typedef.device.operable.ActionOperable;
import com.xiaomi.miot.typedef.urn.ActionType;

public class SetCCSetTemp extends ActionOperable {

    public static final ActionType TYPE = ViomiDefined.Action.setCCSetTemp.toActionType();

    public SetCCSetTemp() {
        super(TYPE);

        super.addArgument(CCSetTemp.TYPE.toString());
    }
}