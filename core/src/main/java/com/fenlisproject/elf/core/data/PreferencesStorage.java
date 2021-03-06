/*
* Copyright (C) 2015 Steven Lewi
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

package com.fenlisproject.elf.core.data;

import android.content.Context;
import android.content.SharedPreferences;

import com.fenlisproject.elf.core.util.SecurityUtils;

import java.io.Serializable;

public class PreferencesStorage implements DataStorage<Serializable> {

    private Context mContext;
    private String mBundleName;
    private SharedPreferences mPreferences;

    public PreferencesStorage(Context context, String bundleName) {
        this(context, bundleName, Context.MODE_PRIVATE);
    }

    public PreferencesStorage(Context context, String bundleName, int mode) {
        this.mContext = context.getApplicationContext();
        this.mBundleName = bundleName;
        this.mPreferences = mContext.getSharedPreferences(bundleName, mode);
    }

    public <V extends Serializable> V get(String key, Class<V> vClass) {
        synchronized (this) {
            String hashedKey = SecurityUtils.md5(mBundleName + "." + key);
            if (vClass == String.class) {
                return (V) mPreferences.getString(hashedKey, null);
            } else if (vClass == Boolean.class) {
                return (V) Boolean.valueOf(mPreferences.getBoolean(hashedKey, false));
            } else if (vClass == Float.class) {
                return (V) Float.valueOf(mPreferences.getFloat(hashedKey, 0F));
            } else if (vClass == Integer.class) {
                return (V) Integer.valueOf(mPreferences.getInt(hashedKey, 0));
            } else if (vClass == Long.class) {
                return (V) Long.valueOf(mPreferences.getLong(hashedKey, 0L));
            } else {
                return null;
            }
        }
    }

    @Override
    public Serializable get(String key) {
        return get(key, Serializable.class);
    }

    @Override
    public boolean put(String key, Serializable value) {
        synchronized (this) {
            String hashedKey = SecurityUtils.md5(mBundleName + "." + key);
            SharedPreferences.Editor editor = mPreferences.edit();
            if (value instanceof String) {
                editor.putString(hashedKey, (String) value);
                return editor.commit();
            } else if (value instanceof Boolean) {
                editor.putBoolean(hashedKey, (Boolean) value);
                return editor.commit();
            } else if (value instanceof Float) {
                editor.putFloat(hashedKey, (Float) value);
                return editor.commit();
            } else if (value instanceof Integer) {
                editor.putInt(hashedKey, (Integer) value);
                return editor.commit();
            } else if (value instanceof Long) {
                editor.putLong(hashedKey, (Long) value);
                return editor.commit();
            } else {
                return false;
            }
        }
    }

    @Override
    public boolean remove(String key) {
        synchronized (this) {
            String hashedKey = SecurityUtils.md5(mBundleName + "." + key);
            if (mPreferences.contains(hashedKey)) {
                return mPreferences.edit().remove(hashedKey).commit();
            } else {
                return false;
            }
        }
    }
}