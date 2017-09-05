/*
 * Copyright (C) 2014-2016 The Dirty Unicorns Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.dirtyunicorns.dutweaks.fragments;

import android.content.ContentResolver;
import android.os.Bundle;
import android.os.SystemProperties;
import android.os.UserHandle;
import android.support.v7.preference.ListPreference;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceScreen;
import android.support.v7.preference.Preference.OnPreferenceChangeListener;
import android.support.v14.preference.SwitchPreference;
import android.provider.Settings;

import com.android.settings.R;
import com.android.internal.logging.MetricsProto.MetricsEvent;
import com.android.internal.util.du.DuUtils;
import com.android.settings.SettingsPreferenceFragment;

import com.dirtyunicorns.dutweaks.preference.CustomSeekBarPreference;

public class MiscTweaks extends SettingsPreferenceFragment implements OnPreferenceChangeListener {

    private static final String PREF_STATUS_BAR_WEATHER = "status_bar_weather";
    private static final String FLASHLIGHT_NOTIFICATION = "flashlight_notification";
    private static final String HEADSET_CONNECT_PLAYER = "headset_connect_player";
    private static final String SCREENSHOT_TYPE = "screenshot_type";
    private static final String SCROLLINGCACHE_PREF = "pref_scrollingcache";
    private static final String SCROLLINGCACHE_PERSIST_PROP = "persist.sys.scrollingcache";
    private static final String SCROLLINGCACHE_DEFAULT = "1";
    private static final String PREF_MEDIA_SCANNER_ON_BOOT = "media_scanner_on_boot";
    private static final String SCREENSHOT_DELAY = "screenshot_delay";
    private static final String WIRED_RINGTONE_FOCUS_MODE = "wired_ringtone_focus_mode";


    private ListPreference mStatusBarWeather;
    private CustomSeekBarPreference mScreenshotDelay;
    private SwitchPreference mFlashlightNotification;
    private ListPreference mLaunchPlayerHeadsetConnection;
    private ListPreference mScreenshotType;
    private ListPreference mScrollingCachePref;
    private ListPreference mMsob;
    private ListPreference mWiredHeadsetRingtoneFocus;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.misctweaks);

        final ContentResolver resolver = getActivity().getContentResolver();
        final PreferenceScreen prefScreen = getPreferenceScreen();

            // Status bar weather
            mStatusBarWeather = (ListPreference) findPreference(PREF_STATUS_BAR_WEATHER);
            int temperatureShow = Settings.System.getIntForUser(resolver,
                    Settings.System.STATUS_BAR_SHOW_WEATHER_TEMP, 0,
                    UserHandle.USER_CURRENT);
            mStatusBarWeather.setValue(String.valueOf(temperatureShow));
            if (temperatureShow == 0) {
                mStatusBarWeather.setSummary(R.string.statusbar_weather_summary);
            } else {
                mStatusBarWeather.setSummary(mStatusBarWeather.getEntry());
            }
            mStatusBarWeather.setOnPreferenceChangeListener(this);

        mFlashlightNotification = (SwitchPreference) findPreference(FLASHLIGHT_NOTIFICATION);
        mFlashlightNotification.setOnPreferenceChangeListener(this);
        if (!DuUtils.deviceSupportsFlashLight(getActivity())) {
            prefScreen.removePreference(mFlashlightNotification);
        } else {
            mFlashlightNotification.setChecked((Settings.System.getInt(resolver,
                    Settings.System.FLASHLIGHT_NOTIFICATION, 0) == 1));
        }

        mMsob = (ListPreference) findPreference(PREF_MEDIA_SCANNER_ON_BOOT);
        mMsob.setValue(String.valueOf(Settings.System.getInt(resolver,
                Settings.System.MEDIA_SCANNER_ON_BOOT, 0)));
        mMsob.setSummary(mMsob.getEntry());
        mMsob.setOnPreferenceChangeListener(this);

        mScrollingCachePref = (ListPreference) findPreference(SCROLLINGCACHE_PREF);
        mScrollingCachePref.setValue(SystemProperties.get(SCROLLINGCACHE_PERSIST_PROP,
                SystemProperties.get(SCROLLINGCACHE_PERSIST_PROP, SCROLLINGCACHE_DEFAULT)));
        mScrollingCachePref.setOnPreferenceChangeListener(this);

        mScreenshotType = (ListPreference) findPreference(SCREENSHOT_TYPE);
        int mScreenshotTypeValue = Settings.System.getInt(resolver,
                Settings.System.SCREENSHOT_TYPE, 0);
        mScreenshotType.setValue(String.valueOf(mScreenshotTypeValue));
        mScreenshotType.setSummary(mScreenshotType.getEntry());
        mScreenshotType.setOnPreferenceChangeListener(this);

        mScreenshotDelay = (CustomSeekBarPreference) findPreference(SCREENSHOT_DELAY);
        int screenshotDelay = Settings.System.getInt(resolver,
                Settings.System.SCREENSHOT_DELAY, 1000);
        mScreenshotDelay.setValue(screenshotDelay / 1);
        mScreenshotDelay.setOnPreferenceChangeListener(this);

        mWiredHeadsetRingtoneFocus = (ListPreference) findPreference(WIRED_RINGTONE_FOCUS_MODE);
        int mWiredHeadsetRingtoneFocusValue = Settings.Global.getInt(resolver,
                Settings.Global.WIRED_RINGTONE_FOCUS_MODE, 1);
        mWiredHeadsetRingtoneFocus.setValue(Integer.toString(mWiredHeadsetRingtoneFocusValue));
        mWiredHeadsetRingtoneFocus.setSummary(mWiredHeadsetRingtoneFocus.getEntry());
        mWiredHeadsetRingtoneFocus.setOnPreferenceChangeListener(this);

        mLaunchPlayerHeadsetConnection = (ListPreference) findPreference(HEADSET_CONNECT_PLAYER);
        int mLaunchPlayerHeadsetConnectionValue = Settings.System.getIntForUser(resolver,
                Settings.System.HEADSET_CONNECT_PLAYER, 0, UserHandle.USER_CURRENT);
        mLaunchPlayerHeadsetConnection.setValue(Integer.toString(mLaunchPlayerHeadsetConnectionValue));
        mLaunchPlayerHeadsetConnection.setSummary(mLaunchPlayerHeadsetConnection.getEntry());
        mLaunchPlayerHeadsetConnection.setOnPreferenceChangeListener(this);
    }

    @Override
    protected int getMetricsCategory() {
        return MetricsEvent.DIRTYTWEAKS;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        final ContentResolver resolver = getActivity().getContentResolver();
        if  (preference == mFlashlightNotification) {
            boolean checked = ((SwitchPreference)preference).isChecked();
            Settings.System.putInt(resolver,
                    Settings.System.FLASHLIGHT_NOTIFICATION, checked ? 1:0);
            return true;
            } else if (preference == mStatusBarWeather) {
                int temperatureShow = Integer.valueOf((String) newValue);
                int index = mStatusBarWeather.findIndexOfValue((String) newValue);
                Settings.System.putIntForUser(resolver,
                        Settings.System.STATUS_BAR_SHOW_WEATHER_TEMP,
                        temperatureShow, UserHandle.USER_CURRENT);
                if (temperatureShow == 0) {
                    mStatusBarWeather.setSummary(R.string.statusbar_weather_summary);
                } else {
                    mStatusBarWeather.setSummary(
                            mStatusBarWeather.getEntries()[index]);
                }
                return true;
        } else if (preference == mScrollingCachePref) {
            if (newValue != null) {
                SystemProperties.set(SCROLLINGCACHE_PERSIST_PROP, (String)newValue);
                return true;
            }
        } else if (preference == mMsob) {
            Settings.System.putInt(resolver,
                    Settings.System.MEDIA_SCANNER_ON_BOOT,
                    Integer.valueOf(String.valueOf(newValue)));
            mMsob.setValue(String.valueOf(newValue));
            mMsob.setSummary(mMsob.getEntry());
            return true;
        } else if  (preference == mScreenshotType) {
            int mScreenshotTypeValue = Integer.parseInt(((String) newValue).toString());
            mScreenshotType.setSummary(
                    mScreenshotType.getEntries()[mScreenshotTypeValue]);
            Settings.System.putInt(resolver,
                    Settings.System.SCREENSHOT_TYPE, mScreenshotTypeValue);
            mScreenshotType.setValue(String.valueOf(mScreenshotTypeValue));
            return true;
        } else if (preference == mScreenshotDelay) {
            int screenshotDelay = (Integer) newValue;
            Settings.System.putInt(resolver,
                    Settings.System.SCREENSHOT_DELAY, screenshotDelay * 1);
            return true;
        } else if (preference == mWiredHeadsetRingtoneFocus) {
            int mWiredHeadsetRingtoneFocusValue = Integer.valueOf((String) newValue);
            int index = mWiredHeadsetRingtoneFocus.findIndexOfValue((String) newValue);
            mWiredHeadsetRingtoneFocus.setSummary(
                    mWiredHeadsetRingtoneFocus.getEntries()[index]);
            Settings.Global.putInt(resolver, Settings.Global.WIRED_RINGTONE_FOCUS_MODE,
                    mWiredHeadsetRingtoneFocusValue);
            return true;
        } else if (preference == mLaunchPlayerHeadsetConnection) {
            int mLaunchPlayerHeadsetConnectionValue = Integer.valueOf((String) newValue);
            int index = mLaunchPlayerHeadsetConnection.findIndexOfValue((String) newValue);
            mLaunchPlayerHeadsetConnection.setSummary(
                    mLaunchPlayerHeadsetConnection.getEntries()[index]);
            Settings.System.putIntForUser(resolver, Settings.System.HEADSET_CONNECT_PLAYER,
                    mLaunchPlayerHeadsetConnectionValue, UserHandle.USER_CURRENT);
            return true;
        }
        return false;
    }
}
