package com.mode.fridge.defined.property;

import com.mode.fridge.defined.ViomiDefined;
import com.xiaomi.miot.typedef.data.DataType;
import com.xiaomi.miot.typedef.data.value.Vstring;
import com.xiaomi.miot.typedef.property.AccessType;
import com.xiaomi.miot.typedef.property.PropertyDefinition;
import com.xiaomi.miot.typedef.property.PropertyOperable;
import com.xiaomi.miot.typedef.urn.PropertyType;

public class CoolBeverage extends PropertyOperable<Vstring> {

    public static PropertyType TYPE = ViomiDefined.Property.CoolBeverage.toPropertyType();

    private static AccessType PERMISSIONS = AccessType.valueOf(AccessType.GET | AccessType.SET | AccessType.NOTIFY);

    private static DataType FORMAT = DataType.STRING;

    public CoolBeverage() {
        super(new PropertyDefinition(TYPE, PERMISSIONS, FORMAT));
    }

    public String getValue() {
        return ((Vstring) super.getCurrentValue()).getValue();
    }

    public void setValue(String value) {
        super.setDataValue(new Vstring(value));
    }
}