package com.mode.fridge.utils;

import com.mode.fridge.AppConstants;
import com.mode.fridge.R;

/**
 * 根据不同 model 设置 icon
 * Created by William on 2018/2/3.
 */
public class DeviceIconUtil {

    // 根据设备 Model 显示图标
    public static int switchIconWithModel(String model) {
        int drawableId;
        switch (model) {
            case AppConstants.YUNMI_WATERPURI_C1:
            case AppConstants.YUNMI_WATERPURI_C2:
                drawableId = R.drawable.icon_device_water_purifier_c;
                break;
            case AppConstants.YUNMI_WATERPURI_V1:
                drawableId = R.drawable.icon_device_water_purifier_v1_400g;
                break;
            case AppConstants.YUNMI_WATERPURI_V2:
                drawableId = R.drawable.icon_device_water_purifier_v1_600g;
                break;
            case AppConstants.YUNMI_WATERPURI_S1:
                drawableId = R.drawable.icon_device_water_purifier_s1_75g;
                break;
            case AppConstants.YUNMI_WATERPURI_S2:
                drawableId = R.drawable.icon_device_water_purifier_s1_50g;
                break;
            case AppConstants.YUNMI_WATERPURI_X3:
                drawableId = R.drawable.icon_device_water_purifier_x3;
                break;
            case AppConstants.YUNMI_WATERPURI_X5:
                drawableId = R.drawable.icon_device_water_purifier_x5;
                break;
            case AppConstants.YUNMI_WATERPURIFIER_V1:
            case AppConstants.YUNMI_WATERPURIFIER_V2:
            case AppConstants.YUNMI_WATERPURIFIER_V3:
            case AppConstants.YUNMI_WATERPURI_LX2:
            case AppConstants.YUNMI_WATERPURI_LX3:
                drawableId = R.drawable.icon_device_water_purifier_mi;
                break;
            case AppConstants.VIOMI_HOOD_A5:
                drawableId = R.drawable.icon_device_hood_t;
                break;
            case AppConstants.VIOMI_HOOD_A6:
                drawableId = R.drawable.icon_device_hood_a6;
                break;
            case AppConstants.VIOMI_HOOD_A7:
                drawableId = R.drawable.icon_device_hood_a7;
                break;
            case AppConstants.VIOMI_HOOD_A4:
                drawableId = R.drawable.icon_device_hood_a4;
                break;
            case AppConstants.VIOMI_HOOD_C1:
                drawableId = R.drawable.icon_device_hood_c1;
                break;
            case AppConstants.VIOMI_HOOD_H1:
                drawableId = R.drawable.icon_device_hood_h1;
                break;
            case AppConstants.VIOMI_HOOD_H2:
                drawableId = R.drawable.icon_device_hood_h2;
                break;
            case AppConstants.YUNMI_KETTLE_R1:
                drawableId = R.drawable.icon_device_heat_kettle_r1;
                break;
            case AppConstants.YUNMI_PLMACHINE_MG2:
                drawableId = R.drawable.icon_device_machine_mg2;
                break;
            case AppConstants.VIOMI_FRIDGE_V1:
                drawableId = R.drawable.icon_device_fridge_v1;
                break;
            case AppConstants.VIOMI_FRIDGE_V2:
                drawableId = R.drawable.icon_device_fridge_v2;
                break;
            case AppConstants.VIOMI_FRIDGE_V3:
                drawableId = R.drawable.icon_device_fridge_v3;
                break;
//            case AppConstants.VIOMI_FRIDGE_V31:
            case AppConstants.VIOMI_FRIDGE_V4:
                drawableId = R.drawable.icon_device_fridge_v4;
                break;
            case AppConstants.VIOMI_FRIDGE_U1:
                drawableId = R.drawable.icon_device_fridge_u1;
                break;
            case AppConstants.VIOMI_FRIDGE_U2:
                drawableId = R.drawable.icon_device_fridge_u2;
                break;
//            case AppConstants.VIOMI_FRIDGE_U3:
//            case AppConstants.VIOMI_FRIDGE_W1:
//            case AppConstants.VIOMI_FRIDGE_X1:
            case AppConstants.VIOMI_FRIDGE_X2:
                drawableId = R.drawable.icon_device_fridge_x2;
                break;
            case AppConstants.VIOMI_FRIDGE_X3:
                drawableId = R.drawable.icon_device_fridge_x3;
                break;
            case AppConstants.VIOMI_FRIDGE_X4:
                drawableId = R.drawable.icon_device_fridge_x4;
                break;
//            case AppConstants.VIOMI_FRIDGE_X41:
            case AppConstants.VIOMI_FRIDGE_X5:
                drawableId = R.drawable.icon_device_fridge_x5;
                break;
            default:
                drawableId = R.drawable.icon_device_default;
                break;
        }
        return drawableId;
    }

    // 根据设备类型显示图标
    public static int switchIconWithPosition(int position) {
        int drawableId;
        switch (position) {
            case 0:
                drawableId = R.drawable.icon_device_scene_fridge;
                break;
            case 1:
                drawableId = R.drawable.icon_device_scene_steamed_oven;
                break;
            case 2:
                drawableId = R.drawable.icon_device_scene_range_hood;
                break;
            case 3:
                drawableId = R.drawable.icon_device_scene_water_purifier;
                break;
            case 4:
                drawableId = R.drawable.icon_device_scene_heat_kettle;
                break;
            case 5:
                drawableId = R.drawable.icon_device_scene_center_water_purifier;
                break;
            case 6:
                drawableId = R.drawable.icon_device_scene_center_water_softener;
                break;
            case 7:
                drawableId = R.drawable.icon_device_scene_dish_washing;
                break;
            case 8:
                drawableId = R.drawable.icon_device_scene_washing_machine;
                break;
            case 9:
                drawableId = R.drawable.icon_device_scene_water_heater;
                break;
            case 10:
                drawableId = R.drawable.icon_device_scene_air_purifier;
                break;
            case 11:
                drawableId = R.drawable.icon_device_scene_fan;
                break;
            case 12:
                drawableId = R.drawable.icon_device_scene_aromatherapy_machine;
                break;
            case 13:
                drawableId = R.drawable.icon_device_scene_health_kettle;
                break;
            case 14:
                drawableId = R.drawable.icon_device_scene_sweep_robot;
                break;
            case 15:
                drawableId = R.drawable.icon_device_scene_camera;
                break;
            default:
                drawableId = R.drawable.icon_device_scene_sweep_robot;
                break;
        }
        return drawableId;
    }

    // 不同设备显示不同背景
    public static int switchDeviceBg(int position) {
        int drawableId;
        switch (position) {
            case 0:
                drawableId = R.drawable.icon_device_circle_blue;
                break;
            case 1:
                drawableId = R.drawable.icon_device_circle_purple;
                break;
            case 2:
                drawableId = R.drawable.icon_device_circle_orange;
                break;
            case 3:
                drawableId = R.drawable.icon_device_circle_green;
                break;
            case 4:
                drawableId = R.drawable.icon_device_circle_green;
                break;
            case 5:
                drawableId = R.drawable.icon_device_circle_blue;
                break;
            case 6:
                drawableId = R.drawable.icon_device_circle_blue;
                break;
            case 7:
                drawableId = R.drawable.icon_device_circle_orange;
                break;
            case 8:
                drawableId = R.drawable.icon_device_circle_orange;
                break;
            case 9:
                drawableId = R.drawable.icon_device_circle_purple;
                break;
            case 10:
                drawableId = R.drawable.icon_device_circle_green;
                break;
            case 11:
                drawableId = R.drawable.icon_device_circle_purple;
                break;
            case 12:
                drawableId = R.drawable.icon_device_circle_purple;
                break;
            case 13:
                drawableId = R.drawable.icon_device_circle_orange;
                break;
            case 14:
                drawableId = R.drawable.icon_device_circle_blue;
                break;
            default:
                drawableId = R.drawable.icon_device_circle_orange;
                break;
        }
        return drawableId;
    }
}