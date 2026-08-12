package ar.com.anura.plugins.backgroundmode;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BackgroundModeCloseReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        context.stopService(new Intent(context, BackgroundModeService.class));
    }
}
