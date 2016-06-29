package edu.illinois.ncsa.bwmon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class FeedsListUpdateReceiver extends BroadcastReceiver {
    public FeedsListUpdateReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // TODO: This method is called when the BroadcastReceiver is receiving
        // an Intent broadcast.
        MainActivity.setView();
    }
}
