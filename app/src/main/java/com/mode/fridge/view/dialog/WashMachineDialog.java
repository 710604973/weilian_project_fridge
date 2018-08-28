package com.mode.fridge.view.dialog;
import com.mode.fridge.adapter.CoverFlowAdapter;
import com.mode.fridge.adapter.base.BaseDeviceAdapter;
import com.mode.fridge.view.dialog.base.BaseDeviceDialog;

public class WashMachineDialog extends BaseDeviceDialog  {
    @Override
    public BaseDeviceAdapter getAdapter() {
        return new CoverFlowAdapter(getActivity());
    }

}
