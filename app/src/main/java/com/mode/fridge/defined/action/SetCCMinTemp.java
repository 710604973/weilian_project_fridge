package com.mode.fridge.defined.action;

import com.mode.fridge.defined.ViomiDefined;
import com.mode.fridge.defined.property.CCMinTemp;
import com.xiaomi.miot.typedef.device.operable.ActionOperable;
import com.xiaomi.miot.typedef.urn.ActionType;

public class SetCCMinTemp extends ActionOperable {

    public static final ActionType TYPE = ViomiDefined.Action.setCCMinTemp.toActionType();

    public SetCCMinTemp() {
        super(TYPE);

        super.addArgument(CCMinTemp.TYPE.toString());
    }
}