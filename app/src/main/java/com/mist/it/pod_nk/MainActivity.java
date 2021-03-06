package com.mist.it.pod_nk;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.crashlytics.android.Crashlytics;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.fabric.sdk.android.Fabric;

import static com.mist.it.pod_nk.MyConstant.urlGetUser;


public class MainActivity extends AppCompatActivity {
    @BindView(R.id.imgMALogo)
    ImageView logoImageView;
    @BindView(R.id.edtMAUsername)
    EditText usernameEditText;
    @BindView(R.id.edtMAPassword)
    EditText passwordEditText;
    @BindView(R.id.btnMALogin)
    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);


    }

    protected class SynGetUser extends AsyncTask<Void, Void, String> {
        @BindView(R.id.imgCAAlert)
        ImageView alertImageView;
        @BindView(R.id.txtCAHeader)
        TextView headerTextView;
        @BindView(R.id.txtCADescript)
        TextView descriptTextView;
        private String usernameString, passwordString;

        SynGetUser(String usernameString, String passwordString) {
            this.usernameString = usernameString;
            this.passwordString = passwordString;

        }

        @Override
        protected String doInBackground(Void... params) {
            try {
                OkHttpClient okHttpClient = new OkHttpClient();
                Request.Builder builder = new Request.Builder();
                RequestBody requestBody = new FormEncodingBuilder()
                        .add("isAdd", "true")
                        .add("username", usernameString)
                        .add("password", passwordString)
                        .build();
                Request request = builder.post(requestBody).url(urlGetUser).build();
                Response response = okHttpClient.newCall(request).execute();
                return response.body().string();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("TAG", s);
            if (!s.equals("null")) {
                try {
                    JSONArray jsonArray = new JSONArray(s);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);
                    String truckIdString = jsonObject.getString("TruckID");
                    String truckRegString = jsonObject.getString("TruckReg");
                    String truckTypeIdString = jsonObject.getString("TruckTypeID");
                    String driverNameString = jsonObject.getString("DriverName");
                    String driverSurname = jsonObject.getString("DriverSurname");
                    String[] loginStrings = new String[]{truckIdString, driverNameString, driverSurname, truckRegString, truckTypeIdString, usernameString};

                    Intent intent = new Intent(MainActivity.this, JobListActivity.class);
                    intent.putExtra("Login", loginStrings);
                    intent.putExtra("Date", "");
                    startActivity(intent);
                    finish();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                View view = View.inflate(getBaseContext(), R.layout.custom_alert, null);

                ButterKnife.bind(this,view);

                alertImageView.setImageResource(R.drawable.caution);
                headerTextView.setText(getResources().getText(R.string.err_login_h));
                descriptTextView.setText(getResources().getText(R.string.err_login_d));

                builder.setView(view);


                builder.setPositiveButton(getResources().getText(R.string.OK), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        //Not do every thing
                    }
                });
                builder.show();
            }
        }
    }

    @OnClick(R.id.btnMALogin)
    public void onViewClicked() {
        SynGetUser synGetUser = new SynGetUser(usernameEditText.getText().toString(), passwordEditText.getText().toString());
        synGetUser.execute();
    }
}
