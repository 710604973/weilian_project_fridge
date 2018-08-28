/* This file is auto-generated.*/

package com.mode.fridge.defined.device;

import com.mode.fridge.defined.service.FridgeService;
import com.xiaomi.miot.host.manager.MiotDeviceConfig;
import com.xiaomi.miot.host.manager.MiotHostManager;
import com.xiaomi.miot.typedef.device.operable.DeviceOperable;
import com.xiaomi.miot.typedef.exception.MiotException;
import com.xiaomi.miot.typedef.listener.CompletedListener;
import com.xiaomi.miot.typedef.urn.DeviceType;

public class ViomiFridge extends DeviceOperable {

    private static final DeviceType DEVICE_TYPE = new DeviceType("Viomi", "ViomiFridge", "1");

    private FridgeService _FridgeService = new FridgeService(false);

    public ViomiFridge(MiotDeviceConfig config) {
        super(DEVICE_TYPE);
        super.setDiscoveryTypes(config.discoveryTypes());
        super.setFriendlyName(config.friendlyName());
        super.setDeviceId(config.deviceId());
        super.setMacAddress(config.macAddress());
        super.setManufacturer(config.manufacturer());
        super.setModelName(config.modelName());
        super.setMiotToken(config.miotToken());
        super.setMiotInfo(config.miotInfo());
        super.addService(_FridgeService);
        super.initializeInstanceID();
    }

    public FridgeService FridgeService() {
        return _FridgeService;
    }

    public void start(CompletedListener listener) throws MiotException {
        MiotHostManager.getInstance().register(this, listener, this);
    }

    public void stop(CompletedListener listener) throws MiotException {
        MiotHostManager.getInstance().unregister(this, listener);
    }

    public void sendEvents() throws MiotException {
        MiotHostManager.getInstance().sendEvent(super.getChangedProperties());
    }

    public void send(String method, String params) throws MiotException {
        MiotHostManager.getInstance().send(method, params);
    }
}