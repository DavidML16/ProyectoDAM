package morales.david.android.managers;

import android.app.Activity;

public final class ScreenManager {

    private static ScreenManager INSTANCE = null;

    public static ScreenManager getInstance() {
        if(INSTANCE == null)
            INSTANCE = new ScreenManager();
        return INSTANCE;
    }

    private Activity activity;

    public synchronized Activity getActivity() {
        return activity;
    }

    public synchronized void setActivity(Activity activity) {
        this.activity = activity;
    }

}
