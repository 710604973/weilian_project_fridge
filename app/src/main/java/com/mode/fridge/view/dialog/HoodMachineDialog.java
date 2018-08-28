package com.mode.fridge.view.dialog;


import com.mode.fridge.adapter.HoodAdapter;
import com.mode.fridge.adapter.base.BaseDeviceAdapter;
import com.mode.fridge.view.dialog.base.BaseDeviceDialog;

public class HoodMachineDialog extends BaseDeviceDialog {

    @Override
    public BaseDeviceAdapter getAdapter() {
        return new HoodAdapter(getActivity());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
