package com.kc.mobileacademy;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.Wsdl2Code.WebServices.MAService.MAService;
import com.Wsdl2Code.WebServices.MAService.ResponseMA;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.kc.Model.User;
import com.kc.Util.Config;
import com.kc.Util.UtilitiesKC;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

import br.com.kots.mob.complex.preferences.ComplexPreferences;
import cn.pedant.SweetAlert.SweetAlertDialog;

public class LoginActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener, OnClickListener {


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onResume() {
        super.onResume();

        AppEventsLogger.activateApp(this);

    }

    @Override
    protected void onPause() {
        super.onPause();

        AppEventsLogger.deactivateApp(this);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            accessTokenTracker.stopTracking();

        }catch (Exception ee){}
    }

    GoogleSignInOptions gso;
    GoogleApiClient googleApiClient;

    private LoginButton loginButton;
    CallbackManager callbackManager;
    AccessTokenTracker accessTokenTracker;
    AccessToken accessToken;

    Boolean isEmailPasswordShown = false;


    private static final int RC_SIGN_IN = 9001;
    private static final int REQUEST_READ_CONTACTS = 0;

    private LoginWithEmail mAuthTask = null;

    // UI references.
    private AutoCompleteTextView mEmailView;
    private EditText mPasswordView;
    private View mProgressView;
    //    private View mLoginFormView;
    private SignInButton google_SignInButton;
    private Button emailSignInButton;
    private Button registerWithEmailButton;

    private TextInputLayout passwordlay;
    private TextInputLayout emaillay;
    float height;

    private User user = new User();

    char signInBefore = '-';

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);


        mEmailView = (AutoCompleteTextView) findViewById(R.id.email);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mPasswordView = (EditText) findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int id, KeyEvent keyEvent) {
                if (id == R.id.login || id == EditorInfo.IME_NULL) {
                    attemptLogin();
                    return true;
                }
                return false;
            }
        });
        getPrefs();


//        mLoginFormView = findViewById(R.id.login_form);
        mProgressView = findViewById(R.id.login_progress);

        if(signInBefore != '-'){
            getUser();
            openMainActivity();
            return;
        }

        setFacebookLogin();
        setGooglePlusSignIn();
        findViews();

//        getSupportActionBar().setIcon(R.mipmap.ic_launcher);
//        getActionBar().setIcon(R.drawable.ic_mobileakademiwhite);

        if (signInBefore == 'm') {
            ComplexPreferences cp = ComplexPreferences.getComplexPreferences(this, "prefs", Context.MODE_PRIVATE);
            this.user = cp.getObject("user", User.class);
            mAuthTask = new LoginWithEmail(this.user.getEmail(), this.user.getPassword());
            mAuthTask.execute();
        }

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();

        ViewGroup view = (ViewGroup) getWindow().getDecorView();
        final LinearLayout content = (LinearLayout) view.getChildAt(0);

        ViewTreeObserver vto = content.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // Put your code here.
                int emailhe = emaillay.getHeight();
                int passhe = passwordlay.getHeight();
                height = (float) emailhe + (float) passhe + UtilitiesKC.convertPixelsToDp(16, LoginActivity.this);

                mEmailView.setVisibility(View.GONE);
                mPasswordView.setVisibility(View.GONE);

//                google_SignInButton.animate().translationYBy(-height).setDuration(700);
//                loginButton.animate().translationYBy(-height).setDuration(700);
//                registerWithEmailButton.animate().translationYBy(-height).setDuration(700);
//
//                mEmailView.animate().alpha(0f).setDuration(500);
//                mPasswordView.animate().alpha(0f).setDuration(500);
//
//                emailSignInButton.animate().translationYBy(-height).setDuration(700).setListener(new Animator.AnimatorListener() {
//                    @Override
//                    public void onAnimationStart(Animator animation) {
//
//                    }
//
//                    @Override
//                    public void onAnimationEnd(Animator animation) {
//                        mEmailView.setVisibility(View.GONE);
//                        mPasswordView.setVisibility(View.GONE);
//
//
//                    }
//
//                    @Override
//                    public void onAnimationCancel(Animator animation) {
//
//                    }
//
//                    @Override
//                    public void onAnimationRepeat(Animator animation) {
//
//                    }
//                });
                content.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });


    }

    private void findViews() {
        emailSignInButton = (Button) findViewById(R.id.email_sign_in_button);
        registerWithEmailButton = (Button) findViewById(R.id.register_with_email_button);
        emaillay = (TextInputLayout) findViewById(R.id.emaillayout);
        passwordlay = (TextInputLayout) findViewById(R.id.passwordlayout);
        emailSignInButton.setOnClickListener(this);
        registerWithEmailButton.setOnClickListener(this);
    }

    private void getPrefs() {

        try {
            ComplexPreferences cp = ComplexPreferences.getComplexPreferences(this, "prefs", Context.MODE_PRIVATE);
            signInBefore = cp.getObject("signIn", Character.class);
        } catch (Exception e) {
            e.printStackTrace();
            signInBefore = '-';
        }


    }

    private void setFacebookLogin() {
        loginButton = (LoginButton) findViewById(R.id.fb_login_button);
        loginButton.setReadPermissions(Arrays.asList("public_profile", "email", "user_friends", "user_education_history"));

        callbackManager = CallbackManager.Factory.create();

        accessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
                accessToken = currentAccessToken;
                if (currentAccessToken == null) {
                    // Log out logic
                    ComplexPreferences cp = ComplexPreferences.getComplexPreferences(LoginActivity.this, "prefs", Context.MODE_PRIVATE);
                    cp.putObject("signIn", '-');
                    cp.commit();
                }
            }
        };

        if (signInBefore == 'f') {
            LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email", "user_friends", "user_education_history"));
            LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    accessToken = loginResult.getAccessToken();
                    getInfoFromFB();
                }

                @Override
                public void onCancel() {

                }

                @Override
                public void onError(FacebookException error) {

                }
            });
        }

//        LoginManager.getInstance().logInWithReadPermissions(this, Arrays.asList("public_profile", "email", "user_friends", "user_education_history"));

        loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                // App code
                accessToken = loginResult.getAccessToken();
                getInfoFromFB();
            }

            @Override
            public void onCancel() {
                // App code
            }

            @Override
            public void onError(FacebookException exception) {
                // App code
            }
        });

    }

    private void getInfoFromFB() {
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                Log.d("FACEBOOK_OGZ", object.toString());
                Boolean succesGetInfo = false;

                try {

                    user.setEmail(object.getString("email"));
                    user.setFbID(object.getString("id"));
                    user.setFbLink(object.getString("link"));
                    user.setFirstName(object.getString("first_name"));
                    user.setGender(object.getString("gender").charAt(0));
                    user.setFullName(object.getString("name"));
                    user.setLastName(object.getString("last_name"));
                    user.setSchool("-");

                    Config.user.setEmail(object.getString("email"));
                    Config.user.setFbID(object.getString("id"));
                    Config.user.setFbLink(object.getString("link"));
                    Config.user.setFirstName(object.getString("first_name"));
                    Config.user.setGender(object.getString("gender").charAt(0));
                    Config.user.setFullName(object.getString("name"));
                    Config.user.setLastName(object.getString("last_name"));
                    Config.user.setSchool("-");

                    succesGetInfo = true;

                    JSONArray arrayEdu = object.getJSONArray("education");
                    for (int i = 0; i < arrayEdu.length(); i++) {
                        JSONObject objEdu = arrayEdu.getJSONObject(i);
                        if (objEdu.getString("type").equals("College")) {
                            user.setSchool(objEdu.getJSONObject("school").getString("name"));
                            Config.user.setSchool(objEdu.getJSONObject("school").getString("name"));
                            break;
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();

                }

                ComplexPreferences cp = ComplexPreferences.getComplexPreferences(LoginActivity.this, "prefs", Context.MODE_PRIVATE);
                cp.putObject("user", user);
                cp.commit();

                if (succesGetInfo) {
                    // Facebookdan veri alımı başarılı
                    new LoginWithFacebookAsyncTask().execute();
                }

            }

        });

        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,education,email,first_name,last_name,gender");
        request.setParameters(parameters);
        request.executeAsync();

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);


    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();


    }

    private void setGooglePlusSignIn() {
        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestProfile()
                .requestId()
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .build();

        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Plus.API)
                .build();

        google_SignInButton = (SignInButton) findViewById(R.id.google_sign_in_button);
        google_SignInButton.setSize(SignInButton.SIZE_STANDARD);
        google_SignInButton.setScopes(gso.getScopeArray());
        google_SignInButton.setOnClickListener(this);

        setGooglePlusButtonText(google_SignInButton, getString(R.string.login_with_google));

        if (signInBefore == 'g')
            signInGoogle();

    }

    private void getInfoFromGoogle(GoogleSignInResult result) {
        Log.d("GooglePlusSignIn", "handleSignInResult:" + result.isSuccess());
        Log.d("GOogleSIGN",result.getStatus().getStatusMessage()+"  ||| "+result.getSignInAccount().getGrantedScopes());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.

            GoogleSignInAccount acct = result.getSignInAccount();
            Person person = Plus.PeopleApi.getCurrentPerson(googleApiClient);
            String photourl = person.getImage().getUrl();
            photourl = photourl.replace("sz=50","sz=150");

            user.setRegisterType('g');
            user.setGPLink(acct.getId());
            if (acct.getDisplayName().split(" ").length == 3) {
                user.setFirstName(acct.getDisplayName().split(" ")[0] + " " + acct.getDisplayName().split(" ")[1]);
                user.setLastName(acct.getDisplayName().split(" ")[2]);
            } else if (acct.getDisplayName().split(" ").length == 2) {
                user.setFirstName(acct.getDisplayName().split(" ")[0]);
                user.setLastName(acct.getDisplayName().split(" ")[1]);
            } else if (acct.getDisplayName().split(" ").length == 4) {
                user.setFirstName(acct.getDisplayName().split(" ")[0] + " " + acct.getDisplayName().split(" ")[1] + " " + acct.getDisplayName().split(" ")[2]);
                user.setLastName(acct.getDisplayName().split(" ")[3]);
            }

            Config.user.setGooglePhotoURL(photourl);
            user.setGooglePhotoURL(photourl);
            user.setEmail(acct.getEmail());
            user.setFullName(acct.getDisplayName());

            if (person != null) {
                if (person.getGender() == 0)
                    user.setGender('m');
                else
                    user.setGender('f');
            }

            new LoginWithGoogleAsyncTask().execute();


        } else {
            // Signed out, show unauthenticated UI.
            UtilitiesKC.showErrorDialog(getString(R.string.oopss), getString(R.string.error_text_dialog), getString(R.string.ok_button), LoginActivity.this);
        }
    }

    @Override
    public void onClick(View v) {

        if(!UtilitiesKC.isNetworkConnected(LoginActivity.this)){
            UtilitiesKC.showWarningDialog(getString(R.string.oopss), getString(R.string.no_internet_conn), getString(R.string.ok_button), LoginActivity.this);
            return;
        }

        switch (v.getId()) {
            case R.id.google_sign_in_button:
                signInGoogle();
                break;
            case R.id.email_sign_in_button:
                if (!isEmailPasswordShown) {

                    google_SignInButton.animate().translationYBy(height).setDuration(700);
                    loginButton.animate().translationYBy(height).setDuration(700);
                    registerWithEmailButton.animate().translationYBy(height).setDuration(700);
                    emailSignInButton.animate().translationYBy(height).setDuration(700).setListener(new Animator.AnimatorListener() {
                        @Override
                        public void onAnimationStart(Animator animation) {

                        }

                        @Override
                        public void onAnimationEnd(Animator animation) {


                            emailSignInButton.setY(emailSignInButton.getY() - height);
                            registerWithEmailButton.setY(registerWithEmailButton.getY() - height);
                            loginButton.setY(loginButton.getY() - height);
                            google_SignInButton.setY(google_SignInButton.getY() - height);

                            mEmailView.setVisibility(View.VISIBLE);
                            mPasswordView.setVisibility(View.VISIBLE);


                            mEmailView.animate().alpha(1.0f).setDuration(600);
                            mPasswordView.animate().alpha(1.0f).setDuration(600);
                        }

                        @Override
                        public void onAnimationCancel(Animator animation) {

                        }

                        @Override
                        public void onAnimationRepeat(Animator animation) {

                        }
                    });

                    isEmailPasswordShown = true;
                } else {
                    attemptLogin();
                }
                break;
            case R.id.register_with_email_button:
                Intent intent_next = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent_next);
                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                finish();
                break;
            default:
                break;
        }

    }

    private void signInGoogle() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);

        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {

            try {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                getInfoFromGoogle(result);
            } catch (Exception e) {
                UtilitiesKC.showErrorDialog(getString(R.string.oopss), getString(R.string.error_text_dialog), getString(R.string.ok_button), LoginActivity.this);
                Log.d("GOOGLESIGNIN","On activity result ERROR"+e.getMessage());
            }

            return;

        }

        callbackManager.onActivityResult(requestCode, resultCode, data);





    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {

    }


    private void attemptLogin() {
        if (mAuthTask != null) {
            return;
        }

        // Reset errors.
        mEmailView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String email = mEmailView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
            focusView = mPasswordView;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmailView.setError(getString(R.string.error_field_required));
            focusView = mEmailView;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmailView.setError(getString(R.string.error_invalid_email));
            focusView = mEmailView;
            cancel = true;
        }

        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // Show a progress spinner, and kick off a background task to
            // perform the user login attempt.

            mAuthTask = new LoginWithEmail(email, password);
            mAuthTask.execute();
        }
    }

    private boolean isEmailValid(String email) {
        //TODO: Replace this with your own logic
        return UtilitiesKC.isValidEmail(email);
    }

    private boolean isPasswordValid(String password) {
        //TODO: Replace this with your own logic   -- En az 4 olacak
        return password.length() > 3;
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Login Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.kc.mobileacademy/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW, // TODO: choose an action type.
                "Login Page", // TODO: Define a title for the content shown.
                // TODO: If you have web page content that matches this app activity's content,
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                // TODO: Make sure this auto-generated app deep link URI is correct.
                Uri.parse("android-app://com.kc.mobileacademy/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }


    public class LoginWithEmail extends AsyncTask<Void, Void, Boolean> {

        private final String mEmail;
        private final String mPassword;
        private SweetAlertDialog dialog;
        private ResponseMA responseMA = new ResponseMA();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = UtilitiesKC.showProgressDialog(getString(R.string.loading), LoginActivity.this);
        }

        LoginWithEmail(String email, String password) {
            mEmail = email;
            mPassword = password;
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                // Simulate network access.
                MAService service = new MAService();
                responseMA = service.LoginWithEmailPassword( mEmail, mPassword);
            } catch (Exception e) {
                return false;
            }


            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            mAuthTask = null;
            dialog.dismissWithAnimation();

            if (!success) {
                UtilitiesKC.showErrorDialog(getString(R.string.oopss), getString(R.string.error_text_dialog), getString(R.string.ok_button), LoginActivity.this);
                return;
            }
            if (responseMA == null) {
                UtilitiesKC.showErrorDialog(getString(R.string.oopss), getString(R.string.error_text_dialog), getString(R.string.ok_button), LoginActivity.this);
                return;
            }

            if (!responseMA.success) {
                UtilitiesKC.showErrorDialog(getString(R.string.oopss), responseMA.error.messageEN, getString(R.string.ok_button), LoginActivity.this);
                return;
            }

            if (responseMA.status.statusCode.equals("201")) {
                //Success login with email
                user.setRegisterType('m');
                user.setSchool(responseMA.user.school);
                user.setFullName(responseMA.user.name + " " + responseMA.user.surname);
                user.setFirstName(responseMA.user.name);
                user.setLastName(responseMA.user.surname);
                user.setEmail(responseMA.user.email);
                user.setCoin(responseMA.user.coin);
                user.setGender(responseMA.user.gender.charAt(0));
                user.setNickname(responseMA.user.nickname);
                user.setPassword(mPassword);
                user.setUserTypeID(1);
                user.setUserId(responseMA.user.userId);

                Config.user.setRegisterType(user.getRegisterType());
                Config.user.setPassword(user.getPassword());
                Config.user.setNickname(user.getNickname());
                Config.user.setCoin(user.getCoin());
                Config.user.setEmail(user.getEmail());
                Config.user.setUserId(user.getUserId());
                Config.user.setFullName(user.getFullName());
                Config.user.setFirstName(user.getFirstName());
                Config.user.setLastName(user.getLastName());
                Config.user.setSchool(user.getSchool());
                Config.user.setGender(user.getGender());

                ComplexPreferences cp = ComplexPreferences.getComplexPreferences(LoginActivity.this, "prefs", Context.MODE_PRIVATE);
                cp.putObject("user", user);
                cp.putObject("signIn", 'm');
                cp.commit();

                Config.Announcement.alertInteger = responseMA.announcement.alertInteger;
                Config.Announcement.alertMessage = responseMA.announcement.alertMessage;
                Config.Announcement.alertPhoto = responseMA.announcement.alertPhoto;
                Config.Announcement.alertTitle = responseMA.announcement.alertTitle;
                Config.Announcement.isActive = responseMA.announcement.isActive;
                Config.Announcement.alertURL = responseMA.announcement.alertURL;

                Config.versionControl.alertInteger = responseMA.versionControl.alertInteger;
                Config.versionControl.alertMessage = responseMA.versionControl.alertMessage;
                Config.versionControl.alertPhoto = responseMA.versionControl.alertPhoto;
                Config.versionControl.alertTitle = responseMA.versionControl.alertTitle;
                Config.versionControl.isActive = responseMA.versionControl.isActive;
                Config.versionControl.alertURL = responseMA.versionControl.alertURL;


                openMainActivity();
                return;
            } else if (responseMA.status.statusCode.equals("203")) {
                // Banned from system
                UtilitiesKC.showErrorDialog(getString(R.string.banned_title), getString(R.string.banned_from_system_message), getString(R.string.ok_button), LoginActivity.this);
                return;
            } else if (responseMA.status.statusCode.equals("202") || responseMA.status.statusCode.equals("101")) {
                // Account is not activated
                UtilitiesKC.showWarningDialog(getString(R.string.account_not_activated_title), getString(R.string.account_not_activated), getString(R.string.account_not_activated_send_email), LoginActivity.this);
                return;
            } else if (responseMA.status.statusCode.equals("99")) {
                UtilitiesKC.showErrorDialog(getString(R.string.oopss), getString(R.string.error_text_dialog), getString(R.string.ok_button), LoginActivity.this);
                return;
            } else if (responseMA.status.statusCode.equals("204") || responseMA.status.statusCode.equals("206")) {
                mPasswordView.setError(getString(R.string.error_incorrect_password));
                mEmailView.setError(getString(R.string.error_incorrect_password));
                mPasswordView.requestFocus();
            }
        }

        @Override
        protected void onCancelled() {
            mAuthTask = null;
            dialog.dismissWithAnimation();
        }
    }

    private class LoginWithGoogleAsyncTask extends AsyncTask<Void, Void, Boolean> {

        ResponseMA responseMA = null;
        private SweetAlertDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = UtilitiesKC.showProgressDialog(getString(R.string.loading), LoginActivity.this);


        }

        @Override
        protected void onPostExecute(Boolean succ) {
            super.onPostExecute(succ);
            dialog.dismissWithAnimation();


            if (!succ) {
                UtilitiesKC.showErrorDialog(getString(R.string.oopss), getString(R.string.error_text_dialog), getString(R.string.ok_button), LoginActivity.this);
                return;
            }

            if (responseMA == null) {
                UtilitiesKC.showErrorDialog(getString(R.string.oopss), getString(R.string.error_text_dialog), getString(R.string.ok_button), LoginActivity.this);
                return;
            }

            if (!responseMA.success) {
                UtilitiesKC.showErrorDialog(getString(R.string.oopss), responseMA.error.messageEN, getString(R.string.ok_button), LoginActivity.this);
                return;
            }

            if (responseMA.success) {
                if (!responseMA.status.statusCode.equals("203")) {
                    user.setCoin(responseMA.user.coin);
                    user.setNickname(responseMA.user.nickname);
                    user.setRegisterType(responseMA.user.registerType.charAt(0));
                    user.setUserId(responseMA.user.userId);
                    user.setSchool(responseMA.user.school);
                    Config.user.setRegisterType(user.getRegisterType());
                    Config.user.setPassword(user.getPassword());
                    Config.user.setNickname(user.getNickname());
                    Config.user.setCoin(user.getCoin());
                    Config.user.setEmail(user.getEmail());
                    Config.user.setFullName(user.getFullName());
                    Config.user.setFirstName(user.getFirstName());
                    Config.user.setLastName(user.getLastName());
                    Config.user.setSchool(user.getSchool());
                    Config.user.setGender(user.getGender());
                    Config.user.setUserId(user.getUserId());
                    Config.user.setGPLink(user.getGPLink());
                    Config.user.setGooglePhotoURL(user.getGooglePhotoURL());

                    ComplexPreferences cp = ComplexPreferences.getComplexPreferences(LoginActivity.this, "prefs", Context.MODE_PRIVATE);
                    cp.putObject("user", user);
                    cp.putObject("signIn", 'g');
                    cp.commit();


                    Config.Announcement.alertInteger = responseMA.announcement.alertInteger;
                    Config.Announcement.alertMessage = responseMA.announcement.alertMessage;
                    Config.Announcement.alertPhoto = responseMA.announcement.alertPhoto;
                    Config.Announcement.alertTitle = responseMA.announcement.alertTitle;
                    Config.Announcement.isActive = responseMA.announcement.isActive;
                    Config.Announcement.alertURL = responseMA.announcement.alertURL;

                    Config.versionControl.alertInteger = responseMA.versionControl.alertInteger;
                    Config.versionControl.alertMessage = responseMA.versionControl.alertMessage;
                    Config.versionControl.alertPhoto = responseMA.versionControl.alertPhoto;
                    Config.versionControl.alertTitle = responseMA.versionControl.alertTitle;
                    Config.versionControl.isActive = responseMA.versionControl.isActive;
                    Config.versionControl.alertURL = responseMA.versionControl.alertURL;

                    openMainActivity();
                }
            }

        }

        @Override
        protected Boolean doInBackground(Void... params) {

//            try {
//                Thread.sleep(2500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

            try {
                MAService sv = new MAService();
                responseMA = sv.LoginWithGoogleV2( user.getFirstName(), user.getLastName(), user.getEmail(), "-", user.getSchool(), String.valueOf(user.getGender()), "1", user.getGPLink(),user.getGooglePhotoURL());
                return true;
            } catch (Exception ex) {
                responseMA = null;
                return false;
            }


        }
    }

    private class LoginWithFacebookAsyncTask extends AsyncTask<Void, Void, Boolean> {

        ResponseMA responseMA = null;
        private SweetAlertDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = UtilitiesKC.showProgressDialog(getString(R.string.loading), LoginActivity.this);


        }

        @Override
        protected void onPostExecute(Boolean succ) {
            super.onPostExecute(succ);
            dialog.dismissWithAnimation();

            if (!succ) {
                UtilitiesKC.showErrorDialog(getString(R.string.oopss), getString(R.string.error_text_dialog), getString(R.string.ok_button), LoginActivity.this);
                return;
            }
            if (responseMA == null) {
                UtilitiesKC.showErrorDialog(getString(R.string.oopss), getString(R.string.error_text_dialog), getString(R.string.ok_button), LoginActivity.this);
                return;
            }

            if (!responseMA.success) {
                UtilitiesKC.showErrorDialog(getString(R.string.oopss), responseMA.error.messageEN, getString(R.string.ok_button), LoginActivity.this);
                return;
            }

            if (responseMA.success) {
                if (!responseMA.status.statusCode.equals("203")) {
                    user.setCoin(responseMA.user.coin);
                    user.setNickname(responseMA.user.nickname);
                    user.setRegisterType(responseMA.user.registerType.charAt(0));
                    user.setUserId(responseMA.user.userId);
                    Config.user.setRegisterType(user.getRegisterType());
                    Config.user.setPassword(user.getPassword());
                    Config.user.setNickname(user.getNickname());
                    Config.user.setCoin(user.getCoin());
                    Config.user.setEmail(user.getEmail());
                    Config.user.setFullName(user.getFullName());
                    Config.user.setFirstName(user.getFirstName());
                    Config.user.setLastName(user.getLastName());
                    Config.user.setSchool(user.getSchool());
                    Config.user.setGender(user.getGender());
                    Config.user.setUserId(user.getUserId());
                    ComplexPreferences cp = ComplexPreferences.getComplexPreferences(LoginActivity.this, "prefs", Context.MODE_PRIVATE);
                    cp.putObject("user", user);
                    cp.putObject("signIn", 'f');
                    cp.commit();


                    Config.Announcement.alertInteger = responseMA.announcement.alertInteger;
                    Config.Announcement.alertMessage = responseMA.announcement.alertMessage;
                    Config.Announcement.alertPhoto = responseMA.announcement.alertPhoto;
                    Config.Announcement.alertTitle = responseMA.announcement.alertTitle;
                    Config.Announcement.isActive = responseMA.announcement.isActive;
                    Config.Announcement.alertURL = responseMA.announcement.alertURL;

                    Config.versionControl.alertInteger = responseMA.versionControl.alertInteger;
                    Config.versionControl.alertMessage = responseMA.versionControl.alertMessage;
                    Config.versionControl.alertPhoto = responseMA.versionControl.alertPhoto;
                    Config.versionControl.alertTitle = responseMA.versionControl.alertTitle;
                    Config.versionControl.isActive = responseMA.versionControl.isActive;
                    Config.versionControl.alertURL = responseMA.versionControl.alertURL;

                    openMainActivity();
                }
            }

        }

        @Override
        protected Boolean doInBackground(Void... params) {

//            try {
//                Thread.sleep(2500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }

            try {
                MAService sv = new MAService();
                responseMA = sv.LoginWithFacebook( user.getFirstName(), user.getLastName(), user.getEmail(), "-", user.getSchool(), String.valueOf(user.getGender()), "1", user.getFbID());
                return true;
            } catch (Exception e) {
                responseMA = null;
                e.printStackTrace();
                return false;
            }


        }
    }


    private void openMainActivity() {
        Intent intent_next = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent_next);
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
        finish();
    }

    protected void setGooglePlusButtonText(SignInButton signInButton, String buttonText) {
        for (int i = 0; i < signInButton.getChildCount(); i++) {
            View v = signInButton.getChildAt(i);
            if (v instanceof TextView) {
                TextView mTextView = (TextView) v;
                mTextView.setText(buttonText);
                mTextView.setTextSize(18);
                return;
            }
        }
    }

    private void getUser(){
        ComplexPreferences cp = ComplexPreferences.getComplexPreferences(this, "prefs", Context.MODE_PRIVATE);
        this.user = cp.getObject("user", User.class);

        Config.user.setRegisterType(user.getRegisterType());
        Config.user.setPassword(user.getPassword());
        Config.user.setNickname(user.getNickname());
        Config.user.setCoin(user.getCoin());
        Config.user.setEmail(user.getEmail());
        Config.user.setUserId(user.getUserId());
        Config.user.setFullName(user.getFullName());
        Config.user.setFirstName(user.getFirstName());
        Config.user.setLastName(user.getLastName());
        Config.user.setSchool(user.getSchool());
        Config.user.setGender(user.getGender());
        Config.user.setGooglePhotoURL(user.getGooglePhotoURL());
        Config.user.setFbID(user.getFbID());
        Config.user.setGPLink(user.getGPLink());

    }

}

