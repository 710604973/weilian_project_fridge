package com.mode.fridge.defined.action;

import com.mode.fridge.defined.ViomiDefined;
import com.mode.fridge.defined.property.CCMaxTemp;
import com.xiaomi.miot.typedef.device.operable.ActionOperable;
import com.xiaomi.miot.typedef.urn.ActionType;

public class SetCCMaxTemp extends ActionOperable {

    public static final ActionType TYPE = ViomiDefined.Action.setCCMaxTemp.toActionType();

    public SetCCMaxTemp() {
        super(TYPE);

        super.addArgument(CCMaxTemp.TYPE.toString());
    }
}