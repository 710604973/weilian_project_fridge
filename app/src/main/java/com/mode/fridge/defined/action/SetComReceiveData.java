package com.mode.fridge.defined.action;

import com.mode.fridge.defined.ViomiDefined;
import com.mode.fridge.defined.property.ComReceiveData;
import com.xiaomi.miot.typedef.device.operable.ActionOperable;
import com.xiaomi.miot.typedef.urn.ActionType;

public class SetComReceiveData extends ActionOperable {

    public static final ActionType TYPE = ViomiDefined.Action.setComReceiveData.toActionType();

    public SetComReceiveData() {
        super(TYPE);

        super.addArgument(ComReceiveData.TYPE.toString());
    }
}