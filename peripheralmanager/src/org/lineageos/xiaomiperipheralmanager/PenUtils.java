/*
 * Copyright (C) 2023 The LineageOS Project
 *
 * SPDX-License-Identifier: Apache-2.0
 */

package org.lineageos.xiaomiperipheralmanager;

import android.content.Context;
import android.hardware.input.InputManager;
import android.hardware.input.InputManager.InputDeviceListener;
import android.os.IBinder;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.util.Log;
import android.view.InputDevice;

import vendor.lineage.xiaomitouch.IXiaomiTouch;

public class PenUtils {

    private static final String TAG = "XiaomiPeripheralManagerPenUtils";
    private static final boolean DEBUG = true;

    private static final String ITOUCHFEATUREL_AIDL_INTERFACE =
            "vendor.lineage.xiaomitouch.IXiaomiTouch/default";

    private InputManager mInputManager;
    private IXiaomiTouch mXiaomiTouch;

    public void setup(Context context) {
        mInputManager = (InputManager) context.getSystemService(Context.INPUT_SERVICE);
        refreshPenMode();
        mInputManager.registerInputDeviceListener(mInputDeviceListener, null);
    }

    private void setXiaomiTouchMode(int mode, int value) {
        final IXiaomiTouch touchFeature = getXiaomiTouch();
        if (touchFeature == null) {
            Log.e(TAG, "setXiaomiTouchMode: touchFeature is null!");
            return;
        }
        if (DEBUG) Log.d(TAG, "setXiaomiTouchMode: mode: " + mode + ", value: " + value);
        try {
            touchFeature.setModeValue(mode, value);
        } catch (Exception e) {
            Log.e(TAG, "setXiaomiTouchMode failed!", e);
        }
    }

    private IXiaomiTouch getXiaomiTouch() {
        if (mXiaomiTouch == null) {
            if (DEBUG) Log.d(TAG, "getXiaomiTouch: mXiaomiTouch=null");
            try {
                IBinder binder = ServiceManager.getService(ITOUCHFEATUREL_AIDL_INTERFACE);
                if (binder == null)
                    throw new Exception("daemon binder null");
                mXiaomiTouch = IXiaomiTouch.Stub.asInterface(binder);
                if (mXiaomiTouch == null)
                    throw new Exception("AIDL daemon interface null");
            } catch (Exception e) {
                Log.e(TAG, "getXiaomiTouch failed!", e);
            }
        }
        return mXiaomiTouch;
    }

    private void enablePenMode() {
        Log.d(TAG, "enablePenMode: Enable Pen Mode");
        setXiaomiTouchMode(20, 18);
    }

    private void disablePenMode() {
        Log.d(TAG, "disablePenMode: Disable Pen Mode");
        setXiaomiTouchMode(20, 0);
    }

    private void refreshPenMode() {
        /*for (int id : mInputManager.getInputDeviceIds()) {
            if (isDeviceXiaomiPen(id)) {
                if (DEBUG) Log.d(TAG, "refreshPenMode: Found Xiaomi Pen");
                enablePenMode();
                return;
            }
        }
        if (DEBUG) Log.d(TAG, "refreshPenMode: No Xiaomi Pen found");
        disablePenMode();*/
        enablePenMode();
    }

    private boolean isDeviceXiaomiPen(int id) {
        InputDevice inputDevice = mInputManager.getInputDevice(id);
        return inputDevice.getVendorId() == 6421 && inputDevice.getProductId() == 19841;
    }

    private InputDeviceListener mInputDeviceListener = new InputDeviceListener() {
            @Override
            public void onInputDeviceAdded(int id) {
                refreshPenMode();
            }
            @Override
            public void onInputDeviceRemoved(int id) {
                refreshPenMode();
            }
            @Override
            public void onInputDeviceChanged(int id) {
                refreshPenMode();
            }
        };
}
