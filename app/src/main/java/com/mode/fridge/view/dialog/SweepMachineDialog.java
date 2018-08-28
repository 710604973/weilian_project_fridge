package com.mode.fridge.view.dialog;

import com.mode.fridge.adapter.SweepMachineAdapter;
import com.mode.fridge.adapter.base.BaseDeviceAdapter;
import com.mode.fridge.view.dialog.base.BaseDeviceDialog;

public class SweepMachineDialog extends BaseDeviceDialog {
    @Override
    public BaseDeviceAdapter getAdapter() {
        return new SweepMachineAdapter(getActivity());
    }
}
