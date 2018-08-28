package com.mode.fridge.view.dialog;
import com.mode.fridge.adapter.WashDishMachineAdapter;
import com.mode.fridge.adapter.base.BaseDeviceAdapter;
import com.mode.fridge.view.dialog.base.BaseDeviceDialog;


public class WashDishMachineDialog extends BaseDeviceDialog {
    @Override
    public BaseDeviceAdapter getAdapter() {
        return new WashDishMachineAdapter(getActivity());
    }
}
