package com.mode.fridge.view.dialog;
import com.mode.fridge.adapter.FanMachineAdapter;
import com.mode.fridge.adapter.base.BaseDeviceAdapter;
import com.mode.fridge.view.dialog.base.BaseDeviceDialog;

public class FanMachineDialog extends BaseDeviceDialog  {

    @Override
    public BaseDeviceAdapter getAdapter() {
        return new FanMachineAdapter(getActivity());
    }
}
