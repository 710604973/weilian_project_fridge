package com.mode.fridge.defined.property;

import com.mode.fridge.defined.ViomiDefined;
import com.xiaomi.miot.typedef.data.DataType;
import com.xiaomi.miot.typedef.data.value.Vbool;
import com.xiaomi.miot.typedef.property.AccessType;
import com.xiaomi.miot.typedef.property.PropertyDefinition;
import com.xiaomi.miot.typedef.property.PropertyOperable;
import com.xiaomi.miot.typedef.urn.PropertyType;

public class InvadeAlarm extends PropertyOperable<Vbool> {

    public static PropertyType TYPE = ViomiDefined.Property.InvadeAlarm.toPropertyType();

    private static AccessType PERMISSIONS = AccessType.valueOf(AccessType.GET | AccessType.SET | AccessType.NOTIFY);

    private static DataType FORMAT = DataType.BOOL;

    public InvadeAlarm() {
        super(new PropertyDefinition(TYPE, PERMISSIONS, FORMAT));
    }

    public boolean getValue() {
        return ((Vbool) super.getCurrentValue()).getValue();
    }

    public void setValue(boolean value) {
        super.setDataValue(new Vbool(value));
    }
}