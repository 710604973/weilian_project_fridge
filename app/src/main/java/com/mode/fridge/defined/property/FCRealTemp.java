package com.mode.fridge.defined.property;

import com.mode.fridge.defined.ViomiDefined;
import com.xiaomi.miot.typedef.data.DataType;
import com.xiaomi.miot.typedef.data.value.Vint;
import com.xiaomi.miot.typedef.property.AccessType;
import com.xiaomi.miot.typedef.property.PropertyDefinition;
import com.xiaomi.miot.typedef.property.PropertyOperable;
import com.xiaomi.miot.typedef.urn.PropertyType;

public class FCRealTemp extends PropertyOperable<Vint> {

    public static PropertyType TYPE = ViomiDefined.Property.FCRealTemp.toPropertyType();

    private static AccessType PERMISSIONS = AccessType.valueOf(AccessType.GET | AccessType.NOTIFY);

    private static DataType FORMAT = DataType.INT;

    public FCRealTemp() {
        super(new PropertyDefinition(TYPE, PERMISSIONS, FORMAT));
    }

    public int getValue() {
        return ((Vint) super.getCurrentValue()).getValue();
    }

    public void setValue(int value) {
        super.setDataValue(new Vint(value));
    }
}