package de.codecamps.jakdroid;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerCallback;
import android.accounts.AccountManagerFuture;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Toast;
import de.codecamps.jakdroid.auth.AccountGeneral;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class StartupActivity extends Activity {
    private AccountManager mAccountManager;

    private AlertDialog mAlertDialog;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_startup);
        mAccountManager = AccountManager.get(this);
        if (!getSharedPreferences(AccountGeneral.ACCOUNT_TYPE, Context.MODE_PRIVATE).contains(AccountGeneral.ACCOUNT_NAME)) {
            showAccountPicker(AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, false);
        } else {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    String authToken = getSharedPreferences(AccountGeneral.ACCOUNT_TYPE, Context.MODE_PRIVATE).getString(AccountGeneral.ACCOUNT_NAME, null);
                    try {
                        HttpURLConnection connection = (HttpURLConnection) new URL("https://jak.codecamps.de/jak-login/login/validate/" + authToken).openConnection();
                        if (connection.getResponseCode() == 200)
                            startActivity(new Intent(StartupActivity.this, BoardActivity.class));
                        else {
                            showAccountPicker(AccountGeneral.AUTHTOKEN_TYPE_FULL_ACCESS, false);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }).start();

        }
    }

    private void showAccountPicker(final String authTokenType, final boolean invalidate) {
        final Account[] availableAccounts = mAccountManager.getAccountsByType(AccountGeneral.ACCOUNT_TYPE);
        if (availableAccounts.length == 0) {
            mAccountManager.addAccount(AccountGeneral.ACCOUNT_TYPE, authTokenType, null, null, this, new AccountManagerCallback<Bundle>() {
                @Override
                public void run(AccountManagerFuture<Bundle> future) {
                    try {
                        Bundle bnd = future.getResult();
                        showMessage(getString(R.string.account_created));
                        Log.d(AccountGeneral.ACCOUNT_NAME, "AddNewAccount Bundle is " + bnd);

                        showAccountPicker(authTokenType, false);
                    } catch (Exception e) {
                        e.printStackTrace();
                        showMessage(e.getMessage());
                    }
                }
            }, null);
        } else {
            String[] name = new String[availableAccounts.length];
            for (int i = 0; i < availableAccounts.length; i++) {
                name[i] = availableAccounts[i].name;
            }

            // Account picker
            mAlertDialog = new AlertDialog.Builder(this).setTitle(getString(R.string.choose_account)).setAdapter(new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_list_item_1, name),
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (invalidate)
                                invalidateAuthToken(availableAccounts[which], authTokenType);
                            else
                                getExistingAccountAuthToken(availableAccounts[which], authTokenType);
                        }
                    }).create();
            mAlertDialog.show();
        }
    }

    private void getExistingAccountAuthToken(Account account, String authTokenType) {
        final AccountManagerFuture<Bundle> future = mAccountManager.getAuthToken(account, authTokenType, null, this, null, null);

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bundle bnd = future.getResult();
                    final String authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                    SharedPreferences sharedPreferences = getSharedPreferences(AccountGeneral.ACCOUNT_TYPE, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString(AccountGeneral.ACCOUNT_NAME, authtoken);
                    editor.commit();
                    startActivity(new Intent(StartupActivity.this, BoardActivity.class));
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage(e.getMessage());
                }
            }
        }).start();
    }

    private void invalidateAuthToken(final Account account, String authTokenType) {
        final AccountManagerFuture<Bundle> future = mAccountManager.getAuthToken(account, authTokenType, null, this, null, null);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Bundle bnd = future.getResult();
                    final String authtoken = bnd.getString(AccountManager.KEY_AUTHTOKEN);
                    mAccountManager.invalidateAuthToken(account.type, authtoken);
                    showMessage(account.name + " " + getString(R.string.account_invalidated));
                } catch (Exception e) {
                    e.printStackTrace();
                    showMessage(e.getMessage());
                }
            }
        }).start();
    }

    private void showMessage(final String msg) {
        if (TextUtils.isEmpty(msg))
            return;

        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getBaseContext(), msg, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
