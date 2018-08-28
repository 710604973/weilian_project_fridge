/* This file is auto-generated.*/

package com.mode.fridge.device;

import android.os.Parcel;
import android.util.Log;

import com.miot.common.abstractdevice.AbstractDevice;
import com.miot.common.device.Device;
import com.miot.common.device.Service;

public class WaterPurifierBase extends AbstractDevice {

    private static final String TAG = "WaterPurifierBase";

    private static final String DEVICE_TYPE = "WaterPurifierBase";

    public static final String SERVICE_WaterPurifierBaseService = "urn:schemas-mi-com:service:WaterPurifier:BaseService:1";
    public WaterPurifierBaseService mWaterPurifierBaseService = new WaterPurifierBaseService(this);

    public synchronized static WaterPurifierBase create(Device device) {
        Log.d(TAG, "create");

        WaterPurifierBase thiz = null;
        do {
            String deviceType = device.getType().getClassType() + device.getType().getSubType();
            if (!deviceType.equals(DEVICE_TYPE)) {
                break;
            }

            thiz = new WaterPurifierBase();
            if (!thiz.init(device)) {
                thiz = null;
            }
        } while (false);

        return thiz;
    }

    private boolean init(Device device) {
        boolean ret = false;

        do {
            if (device == null) {
                break;
            }

            Service baseService = device.getService(SERVICE_WaterPurifierBaseService);
            if (baseService == null) {
                break;
            }
            mWaterPurifierBaseService.setService(baseService);
            this.setDevice(device);

            ret = true;
        } while (false);

        return ret;
    }

    //-------------------------------------------------------
    // Parcelable
    //-------------------------------------------------------
    public static final Creator<WaterPurifierBase> CREATOR = new Creator<WaterPurifierBase>() {

        @Override
        public WaterPurifierBase createFromParcel(Parcel in) {
            return new WaterPurifierBase(in);
        }

        @Override
        public WaterPurifierBase[] newArray(int size) {
            return new WaterPurifierBase[size];
        }
    };

    private WaterPurifierBase() {
    }

    private WaterPurifierBase(Parcel in) {
        readFromParcel(in);
    }

    public void readFromParcel(Parcel in) {
        Device device = in.readParcelable(Device.class.getClassLoader());

        if (!this.init(device)) {
            Log.d(TAG, "init from device failed!");
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel out, int flags) {
        out.writeParcelable(this.getDevice(), flags);
    }
}
