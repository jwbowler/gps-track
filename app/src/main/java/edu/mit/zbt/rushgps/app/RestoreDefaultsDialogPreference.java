package edu.mit.zbt.rushgps.app;

import android.content.Context;
import android.preference.DialogPreference;
import android.preference.PreferenceManager;
import android.util.AttributeSet;

public class RestoreDefaultsDialogPreference extends DialogPreference {
    private Context mContext;

    public RestoreDefaultsDialogPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            PreferenceManager
                    .getDefaultSharedPreferences(mContext)
                    .edit().clear().commit();

            PreferenceManager.setDefaultValues(mContext, R.xml.preferences, true);
        }
    }
}
