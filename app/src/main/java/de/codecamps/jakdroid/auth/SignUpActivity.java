package de.codecamps.jakdroid.auth;

import android.accounts.AccountManager;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import de.codecamps.jakdroid.R;

public class SignUpActivity extends Activity{
    private String TAG = getClass().getSimpleName();
    private String mAccountType;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAccountType = getIntent().getStringExtra(AuthenticatorActivity.ARG_ACCOUNT_TYPE);
        setContentView(R.layout.act_register);

        findViewById(R.id.alreadyMember).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(RESULT_CANCELED);
                finish();
            }
        });
        findViewById(R.id.submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    private void createAccount() {

        new AsyncTask<String, Void, Intent>() {

            String accountName = ((TextView) findViewById(R.id.accountName)).getText().toString().trim();
            String accountPassword = ((TextView) findViewById(R.id.accountPassword)).getText().toString().trim();

            @Override
            protected Intent doInBackground(String... params) {

                Log.d(AccountGeneral.ACCOUNT_NAME, TAG + "> Started authenticating");

                String authtoken = null;
                Bundle data = new Bundle();
                try {
                    authtoken = AccountGeneral.sServerAuthenticate.userSignUp( accountName, accountPassword, "Full Access");

                    data.putString(AccountManager.KEY_ACCOUNT_NAME, accountName);
                    data.putString(AccountManager.KEY_ACCOUNT_TYPE, mAccountType);
                    data.putString(AccountManager.KEY_AUTHTOKEN, authtoken);
                    data.putString(AuthenticatorActivity.PARAM_USER_PASS, accountPassword);
                } catch (Exception e) {
                    data.putString(AuthenticatorActivity.KEY_ERROR_MESSAGE, e.getMessage());
                }

                final Intent res = new Intent();
                res.putExtras(data);
                return res;
            }

            @Override
            protected void onPostExecute(Intent intent) {
                if (intent.hasExtra(AuthenticatorActivity.KEY_ERROR_MESSAGE)) {
                    Toast.makeText(getBaseContext(), intent.getStringExtra(AuthenticatorActivity.KEY_ERROR_MESSAGE), Toast.LENGTH_SHORT).show();
                } else {
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        }.execute();
    }


    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}
