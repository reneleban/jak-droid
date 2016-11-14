package de.codecamps.jakdroid.auth;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

public class JAKAuthenticatorService extends Service {
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        JAKAuthenticator jakAuthenticator = new JAKAuthenticator(this);
        return jakAuthenticator.getIBinder();
    }
}
