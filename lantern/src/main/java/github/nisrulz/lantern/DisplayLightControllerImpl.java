/*
 * Copyright (C) 2017 Nishant Srivastava
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package github.nisrulz.lantern;

import static github.nisrulz.lantern.Utils.isMarshmallowAndAbove;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.view.Window;
import android.view.WindowManager;

class DisplayLightControllerImpl implements DisplayLightController {

    private Activity activity;

    public DisplayLightControllerImpl(final Activity activity) {
        this.activity = activity;
    }

    @Override
    public boolean checkSystemWritePermission(Activity activity) {
        boolean retVal = true;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            retVal = Settings.System.canWrite(activity);
        }
        return retVal;
    }

    @Override
    public void cleanup() {
        this.activity = null;
    }

    @Override
    public void disableAlwaysOnMode() {
        activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void disableAutoBrightMode() {
        disableFullBrightMode();
    }

    @Override
    public void disableFullBrightMode() {
        if (checkSystemWritePermission(activity)) {
            Settings.System.putInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE,
                    Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);

            Window window = activity.getWindow();
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.screenBrightness = 10 / 100.0f;
            window.setAttributes(layoutParams);
        }
    }

    @Override
    public void enableAlwaysOnMode() {
        activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    @Override
    public void enableAutoBrightMode() {
        if (checkSystemWritePermission(activity)) {
            Settings.System.putInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE,
                    Settings.System.SCREEN_BRIGHTNESS_MODE_AUTOMATIC);
        }

    }

    @Override
    public void enableFullBrightMode() {
        if (checkSystemWritePermission(activity)) {
            Settings.System.putInt(activity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS_MODE,
                    Settings.System.SCREEN_BRIGHTNESS_MODE_MANUAL);

            Window window = activity.getWindow();
            WindowManager.LayoutParams layoutParams = window.getAttributes();
            layoutParams.screenBrightness = 100 / 100.0f;
            window.setAttributes(layoutParams);
        }

    }

    @Override
    public void requestSystemWritePermission(Activity activity) {
        // Request for permission if not yet granted
        if (!checkSystemWritePermission(activity)) {
            if (isMarshmallowAndAbove()) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                intent.setData(Uri.parse("package:" + activity.getPackageName()));
                activity.startActivity(intent);
            }
        }
    }
}