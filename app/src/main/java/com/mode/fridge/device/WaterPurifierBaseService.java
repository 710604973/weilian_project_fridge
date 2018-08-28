/* This file is auto-generated.*/

package com.mode.fridge.device;

import com.miot.api.CompletionHandler;
import com.miot.api.DeviceManipulator;
import com.miot.api.MiotManager;
import com.miot.common.ReturnCode;
import com.miot.common.abstractdevice.AbstractDevice;
import com.miot.common.abstractdevice.AbstractService;
import com.miot.common.device.invocation.PropertyInfo;
import com.miot.common.device.invocation.PropertyInfoFactory;
import com.miot.common.exception.MiotException;
import com.miot.common.property.Property;
import com.miot.common.property.PropertyDefinition;

public class WaterPurifierBaseService extends AbstractService {
    private static final String TAG = "WaterPurifierBaseService";

    //-------------------------------------------------------
    // Action Name
    //-------------------------------------------------------

    //-------------------------------------------------------
    // Property Name
    //-------------------------------------------------------
    public static final String PROPERTY_ButtonPressed = "ButtonPressed";
    public static final String PROPERTY_Mode = "Mode";
    public static final String PROPERTY_ModeType = "ModeType";
    public static final String PROPERTY_Prop = "Prop";

    private AbstractDevice mDevice = null;

    public WaterPurifierBaseService(AbstractDevice device) {
        mDevice = device;
    }

    //-------------------------------------------------------
    // Property value defined
    //-------------------------------------------------------
    /**
     * 按键状态
     */
    public enum ButtonPressed {
        undefined,
        tap,
        pure,
        select,
        reset,
    }

    /**
     * 制水模式
     */
    public enum Mode {
        undefined,
        idle,
        purifying,
    }

    /**
     * 设备类型（厨上厨下）
     */
    public enum ModeType {
        undefined,
        desktop,
        deskbottom,
    }


    //-------------------------------------------------------
    // Property: Notifications
    //-------------------------------------------------------
    public interface PropertyNotificationListener {

        /**
         * 按键状态 发生改变
         */
        void onButtonPressedChanged(ButtonPressed buttonPressed);
        /**
         * 制水模式 发生改变
         */
        void onModeChanged(Mode mode);
        /**
         * 设备类型（厨上厨下） 发生改变
         */
        void onModeTypeChanged(ModeType modeType);
        /**
         * 获取基本属性 发生改变
         */
        void onPropChanged(String prop);
    }

    //-------------------------------------------------------
    // Property: subscribeNotifications
    //-------------------------------------------------------
    public void subscribeNotifications(final CompletionHandler handler, final PropertyNotificationListener listener) throws MiotException {
        if (handler == null) {
            throw new IllegalArgumentException("handler is null");
        }

        if (listener == null) {
            throw new IllegalArgumentException("listener is null");
        }

        PropertyInfo propertyInfo = PropertyInfoFactory.create(getService());
        for (Property property : getService().getProperties()) {
            PropertyDefinition definition = property.getDefinition();
            if (definition.isNotifiable()) {
                propertyInfo.addProperty(property);
            }
        }

        DeviceManipulator op = MiotManager.getDeviceManipulator();
        op.addPropertyChangedListener(propertyInfo,
                new DeviceManipulator.CompletionHandler() {
                    @Override
                    public void onSucceed() {
                        handler.onSucceed();
                    }

                    @Override
                    public void onFailed(int errCode, String description) {
                        handler.onFailed(errCode, description);
                    }
                },
                new DeviceManipulator.PropertyChangedListener() {
                    @Override
                    public void onPropertyChanged(PropertyInfo info, String propertyName) {
                        switch (propertyName) {
                            case PROPERTY_ButtonPressed:
                                if (info.getProperty(PROPERTY_ButtonPressed).isValueValid()) {
                                    ButtonPressed buttonPressed = ButtonPressed.valueOf((String) info.getValue(PROPERTY_ButtonPressed));
                                    listener.onButtonPressedChanged(buttonPressed);
                                }
                                break;
                            case PROPERTY_Mode:
                                if (info.getProperty(PROPERTY_Mode).isValueValid()) {
                                    Mode mode = Mode.valueOf((String) info.getValue(PROPERTY_Mode));
                                    listener.onModeChanged(mode);
                                }
                                break;
                            case PROPERTY_ModeType:
                                if (info.getProperty(PROPERTY_ModeType).isValueValid()) {
                                    ModeType modeType = ModeType.valueOf((String) info.getValue(PROPERTY_ModeType));
                                    listener.onModeTypeChanged(modeType);
                                }
                                break;
                            case PROPERTY_Prop:
                                if (info.getProperty(PROPERTY_Prop).isValueValid()) {
                                    String prop = (String) info.getValue(PROPERTY_Prop);
                                    listener.onPropChanged(prop);
                                }
                                break;

                            default:
                                break;
                        }
                    }
                });
    }

    //-------------------------------------------------------
    // Property: unsubscribeNotifications
    //-------------------------------------------------------
    public void unsubscribeNotifications(final CompletionHandler handler) throws MiotException {
        if (handler == null) {
            throw new IllegalArgumentException("handler is null");
        }

        PropertyInfo propertyInfo = PropertyInfoFactory.create(getService());
        for (Property property : getService().getProperties()) {
            PropertyDefinition definition = property.getDefinition();
            if (definition.isNotifiable()) {
                propertyInfo.addProperty(property);
            }
        }

        DeviceManipulator op = MiotManager.getDeviceManipulator();
        op.removePropertyChangedListener(propertyInfo, new DeviceManipulator.CompletionHandler() {
            @Override
            public void onSucceed() {
                handler.onSucceed();
            }

            @Override
            public void onFailed(int errCode, String description) {
                handler.onFailed(errCode, description);
            }
        });
    }

    //-------------------------------------------------------
    // Properties Getter
    //-------------------------------------------------------
    /**
     * 回调接口： 读取所有可读属性
     */

    public interface GetPropertiesCompletionHandler {
        void onSucceed(ButtonPressed buttonPressed, Mode mode, ModeType modeType, String prop);

        void onFailed(int errCode, String description);
    }

    /**
     * 读取所有可读属性
     */
    public void getProperties(final GetPropertiesCompletionHandler handler) throws MiotException {
        if (!mDevice.isConnectionEstablished()) {
            throw new MiotException("device not configurated connection");
        }

        PropertyInfo propertyInfo = PropertyInfoFactory.create(getService());
        propertyInfo.addProperty(getService().getProperty(PROPERTY_ButtonPressed));
        propertyInfo.addProperty(getService().getProperty(PROPERTY_Mode));
        propertyInfo.addProperty(getService().getProperty(PROPERTY_ModeType));
        propertyInfo.addProperty(getService().getProperty(PROPERTY_Prop));

        DeviceManipulator op = MiotManager.getDeviceManipulator();
        op.readProperty(propertyInfo, new DeviceManipulator.ReadPropertyCompletionHandler() {
            @Override
            public void onSucceed(PropertyInfo info) {
                Property buttonPressedProp = info.getProperty(PROPERTY_ButtonPressed);
                ButtonPressed buttonPressed = null;
                if(buttonPressedProp.isValueValid()) {
                    buttonPressed = ButtonPressed.valueOf((String) buttonPressedProp.getValue());
                }

                Property modeProp = info.getProperty(PROPERTY_Mode);
                Mode mode = null;
                if(modeProp.isValueValid()) {
                    mode = Mode.valueOf((String) modeProp.getValue());
                }

                Property modeTypeProp = info.getProperty(PROPERTY_ModeType);
                ModeType modeType = null;
                if(modeTypeProp.isValueValid()) {
                    modeType = ModeType.valueOf((String) modeTypeProp.getValue());
                }

                Property propProp = info.getProperty(PROPERTY_Prop);
                String prop = null;
                if(propProp.isValueValid()) {
                    prop = (String) propProp.getValue();
                }

                handler.onSucceed(buttonPressed, mode, modeType, prop);
            }

            @Override
            public void onFailed(int errCode, String description) {
                handler.onFailed(errCode, description);
            }
        });
    }

    //-------------------------------------------------------
    // Property Getters
    //-------------------------------------------------------
    /**
     * 回调接口： 读取ButtonPressed
     */
    public interface GetButtonPressedCompletionHandler {
        void onSucceed(ButtonPressed buttonPressed);

        void onFailed(int errCode, String description);
    }

    /**
     * 读取按键状态
     */
    public void getButtonPressed(final GetButtonPressedCompletionHandler handler) throws MiotException {
        if (!mDevice.isConnectionEstablished()) {
            throw new MiotException("device not configurated connection");
        }

        PropertyInfo propertyInfo = PropertyInfoFactory.create(getService(), PROPERTY_ButtonPressed);
        DeviceManipulator op = MiotManager.getDeviceManipulator();
        op.readProperty(propertyInfo, new DeviceManipulator.ReadPropertyCompletionHandler() {
            @Override
            public void onSucceed(PropertyInfo info) {
                Property buttonPressed = info.getProperty(PROPERTY_ButtonPressed);
                if(buttonPressed.isValueValid()) {
                    handler.onSucceed(ButtonPressed.valueOf((String) info.getValue(PROPERTY_ButtonPressed)));
                } else {
                    handler.onFailed(ReturnCode.E_INVALID_DATA, "device response valid: " + buttonPressed.getValue());
                }
            }

            @Override
            public void onFailed(int errCode, String description) {
                handler.onFailed(errCode, description);
            }
        });
    }
    /**
     * 回调接口： 读取Mode
     */
    public interface GetModeCompletionHandler {
        void onSucceed(Mode mode);

        void onFailed(int errCode, String description);
    }

    /**
     * 读取制水模式
     */
    public void getMode(final GetModeCompletionHandler handler) throws MiotException {
        if (!mDevice.isConnectionEstablished()) {
            throw new MiotException("device not configurated connection");
        }

        PropertyInfo propertyInfo = PropertyInfoFactory.create(getService(), PROPERTY_Mode);
        DeviceManipulator op = MiotManager.getDeviceManipulator();
        op.readProperty(propertyInfo, new DeviceManipulator.ReadPropertyCompletionHandler() {
            @Override
            public void onSucceed(PropertyInfo info) {
                Property mode = info.getProperty(PROPERTY_Mode);
                if(mode.isValueValid()) {
                    handler.onSucceed(Mode.valueOf((String) info.getValue(PROPERTY_Mode)));
                } else {
                    handler.onFailed(ReturnCode.E_INVALID_DATA, "device response valid: " + mode.getValue());
                }
            }

            @Override
            public void onFailed(int errCode, String description) {
                handler.onFailed(errCode, description);
            }
        });
    }
    /**
     * 回调接口： 读取ModeType
     */
    public interface GetModeTypeCompletionHandler {
        void onSucceed(ModeType modeType);

        void onFailed(int errCode, String description);
    }

    /**
     * 读取设备类型（厨上厨下）
     */
    public void getModeType(final GetModeTypeCompletionHandler handler) throws MiotException {
        if (!mDevice.isConnectionEstablished()) {
            throw new MiotException("device not configurated connection");
        }

        PropertyInfo propertyInfo = PropertyInfoFactory.create(getService(), PROPERTY_ModeType);
        DeviceManipulator op = MiotManager.getDeviceManipulator();
        op.readProperty(propertyInfo, new DeviceManipulator.ReadPropertyCompletionHandler() {
            @Override
            public void onSucceed(PropertyInfo info) {
                Property modeType = info.getProperty(PROPERTY_ModeType);
                if(modeType.isValueValid()) {
                    handler.onSucceed(ModeType.valueOf((String) info.getValue(PROPERTY_ModeType)));
                } else {
                    handler.onFailed(ReturnCode.E_INVALID_DATA, "device response valid: " + modeType.getValue());
                }
            }

            @Override
            public void onFailed(int errCode, String description) {
                handler.onFailed(errCode, description);
            }
        });
    }
    /**
     * 回调接口： 读取Prop
     */
    public interface GetPropCompletionHandler {
        void onSucceed(String prop);

        void onFailed(int errCode, String description);
    }

    /**
     * 读取获取基本属性
     */
    public void getProp(final GetPropCompletionHandler handler) throws MiotException {
        if (!mDevice.isConnectionEstablished()) {
            throw new MiotException("device not configurated connection");
        }

        PropertyInfo propertyInfo = PropertyInfoFactory.create(getService(), PROPERTY_Prop);
        DeviceManipulator op = MiotManager.getDeviceManipulator();
        op.readProperty(propertyInfo, new DeviceManipulator.ReadPropertyCompletionHandler() {
            @Override
            public void onSucceed(PropertyInfo info) {
                Property prop = info.getProperty(PROPERTY_Prop);
                if(prop.isValueValid()) {
                    handler.onSucceed((String) info.getValue(PROPERTY_Prop));
                } else {
                    handler.onFailed(ReturnCode.E_INVALID_DATA, "device response valid: " + prop.getValue());
                }
            }

            @Override
            public void onFailed(int errCode, String description) {
                handler.onFailed(errCode, description);
            }
        });
    }

    //-------------------------------------------------------
    // Actions
    //-------------------------------------------------------
}

