package com.mode.fridge.defined.action;

import com.mode.fridge.defined.ViomiDefined;
import com.mode.fridge.defined.property.IpAddr;
import com.xiaomi.miot.typedef.device.operable.ActionOperable;
import com.xiaomi.miot.typedef.urn.ActionType;

public class SetIpAddr extends ActionOperable {

    public static final ActionType TYPE = ViomiDefined.Action.setIpAddr.toActionType();

    public SetIpAddr() {
        super(TYPE);

        super.addArgument(IpAddr.TYPE.toString());
    }
}