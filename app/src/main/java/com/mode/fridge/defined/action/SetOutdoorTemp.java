package com.mode.fridge.defined.action;

import com.mode.fridge.defined.ViomiDefined;
import com.mode.fridge.defined.property.OutdoorTemp;
import com.xiaomi.miot.typedef.device.operable.ActionOperable;
import com.xiaomi.miot.typedef.urn.ActionType;

public class SetOutdoorTemp extends ActionOperable {

    public static final ActionType TYPE = ViomiDefined.Action.setOutdoorTemp.toActionType();

    public SetOutdoorTemp() {
        super(TYPE);

        super.addArgument(OutdoorTemp.TYPE.toString());
    }
}