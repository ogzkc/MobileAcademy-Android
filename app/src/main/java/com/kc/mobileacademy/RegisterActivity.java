package com.kc.mobileacademy;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;

import com.Wsdl2Code.WebServices.MAService.MAService;
import com.Wsdl2Code.WebServices.MAService.ResponseMA;
import com.kc.Util.UtilitiesKC;

import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * Created by Oguz on 20.02.2016.
 */
public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout rootView;
    private ProgressBar registerProgress;
    private ScrollView registerForm;
    private LinearLayout emailRegisterForm;
    private EditText registerformEmail;
    private EditText registerformPassword;
    private EditText registerformPasswordRepeat;
    private EditText registerformName;
    private EditText registerformSurname;
    private EditText registerformSchool;
    private RadioGroup registerGenderGroup;
    private RadioButton registerGenderMale;
    private RadioButton registerGenderFemale;
    private Button emailRegisterButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        findViews();


    }

    private void findViews() {
        rootView = (LinearLayout) findViewById(0);
        registerProgress = (ProgressBar) findViewById(R.id.register_progress);
        registerForm = (ScrollView) findViewById(R.id.register_form);
        emailRegisterForm = (LinearLayout) findViewById(R.id.email_register_form);
        registerformEmail = (EditText) findViewById(R.id.registerform_email);
        registerformPassword = (EditText) findViewById(R.id.registerform_password);
        registerformPasswordRepeat = (EditText) findViewById(R.id.registerform_password_repeat);
        registerformName = (EditText) findViewById(R.id.registerform_name);
        registerformSurname = (EditText) findViewById(R.id.registerform_surname);
        registerformSchool = (EditText) findViewById(R.id.registerform_school);
        registerGenderGroup = (RadioGroup) findViewById(R.id.register_gender_group);
        registerGenderMale = (RadioButton) findViewById(R.id.register_gender_male);
        registerGenderFemale = (RadioButton) findViewById(R.id.register_gender_female);
        emailRegisterButton = (Button) findViewById(R.id.email_register_button);

        emailRegisterButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if (v == emailRegisterButton) {
            String email = registerformEmail.getText().toString();
            String pass = registerformPassword.getText().toString();
            String pass2 = registerformPasswordRepeat.getText().toString();
            String name = registerformName.getText().toString();
            String surname = registerformSurname.getText().toString();
            String school = registerformSchool.getText().toString();
            String gender = "-";
            if (registerGenderMale.isChecked())
                gender = "m";
            else if (registerGenderFemale.isChecked())
                gender = "f";

            if (!UtilitiesKC.isValidEmail(email)) {
                registerformEmail.setError(getString(R.string.email_isnot_valid));
                registerformEmail.requestFocus();
                return;
            }

            if (pass.length() < 4) {
                registerformPassword.setError(getString(R.string.password_too_short));
                registerformPassword.requestFocus();
                return;
            }

            if (!pass.equals(pass2)) {
                registerformPasswordRepeat.setError(getString(R.string.passwords_are_not_same));
                registerformPasswordRepeat.requestFocus();
                return;
            }

            if (school.equals(""))
                school = "-";
            if (name.equals(""))
                name = "-";
            if (surname.equals(""))
                surname = "-";

            new RegisterwithEmailAsyncTask().execute(name, surname, email, pass, school, gender, "1");


        }
    }


    private class RegisterwithEmailAsyncTask extends AsyncTask<String, Void, Boolean> {

        ResponseMA responseMA = null;
        SweetAlertDialog pDialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = UtilitiesKC.showProgressDialog(getString(R.string.loading), RegisterActivity.this);
        }

        @Override
        protected void onPostExecute(Boolean succ) {
            super.onPostExecute(succ);
            pDialog.dismissWithAnimation();

            if (!succ) {
                UtilitiesKC.showErrorDialog(getString(R.string.oopss), getString(R.string.error_text_dialog), getString(R.string.ok_button), RegisterActivity.this);
                return;
            }

            if (!responseMA.success) {
                UtilitiesKC.showErrorDialog(getString(R.string.oopss), responseMA.error.messageEN, getString(R.string.ok_button), RegisterActivity.this);
                return;
            }

            if (responseMA.status.statusCode.equals("302") || responseMA.status.statusCode.equals("303")) {
//                UtilitiesKC.showSuccesDialog(getString(R.string.account_has_been_Created_title), getString(R.string.account_has_been_created), getString(R.string.ok_button), RegisterActivity.this);

                SweetAlertDialog alert = new SweetAlertDialog(RegisterActivity.this, SweetAlertDialog.SUCCESS_TYPE);
                alert.setTitleText(getString(R.string.account_has_been_Created_title))
                        .setContentText(getString(R.string.account_has_been_created))
                        .setConfirmText(getString(R.string.ok_button))

                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sweetAlertDialog) {
                                Intent home_intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                startActivity(home_intent);
                                overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);
                                finish();
                            }
                        });
                alert.setCancelable(false);
                alert.show();

                return;
            } else if (responseMA.status.statusCode.equals("301")) {
                UtilitiesKC.showErrorDialog(getString(R.string.account_create_used_email_adress_title), getString(R.string.account_create_used_email_adress), getString(R.string.ok_button), RegisterActivity.this);
                return;
            }


        }

        @Override
        protected Boolean doInBackground(String... params) {

            try {
                MAService sv = new MAService();
                responseMA = sv.RegisterWithEmail( params[0], params[1], params[2], params[3], params[4], params[5], "1");
                int a = 3;
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }

        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent home_intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(home_intent);
        overridePendingTransition(R.anim.animation_enter, R.anim.animation_leave);
        finish();
    }
}
