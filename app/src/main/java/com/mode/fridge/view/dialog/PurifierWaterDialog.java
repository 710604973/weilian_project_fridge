package com.mode.fridge.view.dialog;

import com.mode.fridge.adapter.WaterPurifierAdapter;
import com.mode.fridge.adapter.base.BaseDeviceAdapter;
import com.mode.fridge.view.dialog.base.BaseDeviceDialog;

public class PurifierWaterDialog extends BaseDeviceDialog {
    @Override
    public BaseDeviceAdapter getAdapter() {
        return new WaterPurifierAdapter(getActivity());
    }
}
