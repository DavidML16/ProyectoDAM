package morales.david.android.utils;

import android.app.Activity;
import android.content.res.Configuration;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.ActionBar;

import morales.david.android.R;

public class ActionBarUtil {

    public static void changeStyle(Activity activity, ActionBar actionBar) {

        final ActionBar abar = actionBar;

        boolean isDarkThemeOn = (activity.getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK)  == Configuration.UI_MODE_NIGHT_YES;

        if(!isDarkThemeOn)
            abar.setBackgroundDrawable(new ColorDrawable(activity.getColor(R.color.primary_dark)));
        else
            abar.setBackgroundDrawable(new ColorDrawable(activity.getColor(R.color.secondary_dark)));

    }

}
