package com.kc.mobileacademy;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;

import com.Wsdl2Code.WebServices.MAService.MAService;
import com.Wsdl2Code.WebServices.MAService.ResponseMA;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.kc.Util.Config;
import com.kc.Util.UtilitiesKC;
import com.kc.WidgetKC.ButtonBoldType;
import com.kc.WidgetKC.TextViewBold;
import com.kc.WidgetKC.TextViewMed;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import br.com.kots.mob.complex.preferences.ComplexPreferences;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment implements View.OnClickListener {


    public ProfileFragment() {
        // Required empty public constructor
    }

    View rootView;
    private View containerView;
    private int res;

    private ImageView profileImageLinearLayout;
    private ProgressBar profileProgress;
    private ScrollView profileForm;
    private LinearLayout emailProfileForm;
    private ButtonBoldType emailProfileButton;
    private EditText et_email;
    private EditText et_nickname;
    private EditText et_name;
    private EditText et_surname;
    private EditText et_school;

    private TextViewBold tv_name;
    private TextViewMed tv_school;
    private TextViewMed tv_coin;

    DisplayImageOptions options;
    ImageLoaderConfiguration config;
    ImageLoader loader;

    public static ProfileFragment newInstance() {


        Bundle args = new Bundle();

        ProfileFragment fragment = new ProfileFragment();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        getControls();


        return rootView;
    }

    private void getControls(){

        config = new ImageLoaderConfiguration.Builder(getActivity())
                .build();
        loader = ImageLoader.getInstance();
        loader.init(config);
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(false)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        profileImageLinearLayout = (ImageView) rootView.findViewById(R.id.profile_image);
        profileProgress = (ProgressBar) rootView.findViewById(R.id.profile_progress);
        profileForm = (ScrollView) rootView.findViewById(R.id.profile_form);
        emailProfileForm = (LinearLayout) rootView.findViewById(R.id.email_profile_form);
        emailProfileButton = (ButtonBoldType) rootView.findViewById(R.id.profile_updatebutton);
        et_email =  (EditText) rootView.findViewById(R.id.profileform_email);
        et_nickname = (EditText) rootView.findViewById(R.id.profileform_nickname);
        et_name = (EditText) rootView.findViewById(R.id.profileform_name);
        et_surname = (EditText) rootView.findViewById(R.id.profileform_surname);
        et_school = (EditText) rootView.findViewById(R.id.profileform_school);
        tv_coin = (TextViewMed) rootView.findViewById(R.id.profile_cointv);
        tv_name = (TextViewBold) rootView.findViewById(R.id.profile_nametv);
        tv_school = (TextViewMed) rootView.findViewById(R.id.profile_schooltv);
        emailProfileButton.setOnClickListener(this);

        if(!Config.user.getEmail().equals("-"))
            et_email.setText(Config.user.getEmail());

        if(!Config.user.getFirstName().equals("-"))
            et_name.setText(Config.user.getFirstName());

        if(!Config.user.getLastName().equals("-"))
            et_surname.setText(Config.user.getLastName());

        if(!Config.user.getSchool().equals("-"))
            et_school.setText(Config.user.getSchool());

        if(!Config.user.getNickname().equals("-"))
            et_nickname.setText(Config.user.getNickname());

        tv_coin.setText("Coin: "+Config.user.getCoin());
        tv_school.setText(Config.user.getSchool());
        tv_name.setText(Config.user.getFullName());

        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
// generate random color
        int color1 = generator.getRandomColor();

        final TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .withBorder(4) /* thickness in px */
                .endConfig()
                .buildRoundRect(Config.user.getFirstName().charAt(0) + "" + Config.user.getLastName().charAt(0), color1, 10);

        if(Config.user.getRegisterType() == 'f'){
            ImageLoader.getInstance().displayImage("http://graph.facebook.com/" + Config.user.getFbID() + "/picture?type=large", profileImageLinearLayout, options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                    profileImageLinearLayout.setImageDrawable(drawable);
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    Bitmap roundedBmp = UtilitiesKC.getRoundedBitmap(bitmap);
                    profileImageLinearLayout.setImageBitmap(roundedBmp);
                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });
        }else if (Config.user.getRegisterType() == 'g'){
            Log.d("GooglePhoto",Config.user.getGooglePhotoURL());
            ImageLoader.getInstance().displayImage(Config.user.getGooglePhotoURL(), profileImageLinearLayout, options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                    profileImageLinearLayout.setImageDrawable(drawable);
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    Bitmap roundedBmp = UtilitiesKC.getRoundedBitmap(bitmap);
                    profileImageLinearLayout.setImageBitmap(roundedBmp);
                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });
        }else{
            profileImageLinearLayout.setImageDrawable(drawable);
        }

    }

    @Override
    public void onClick(View v) {

        switch (v.getId()){
            case R.id.profile_updatebutton:

                if(et_email.getText().toString().isEmpty()){
                    UtilitiesKC.showWarningToast(getString(R.string.email_cannotbe_empty),getActivity());
                    return;
                }

                new updateProfileAsync().execute();


                break;
            default:
                break;
        }

    }

    private class updateProfileAsync extends AsyncTask<Void,Void,Boolean>{

        public String mEmail;
        public String mPassword;
        private SweetAlertDialog dialog;
        private ResponseMA responseMA = new ResponseMA();
        String nameNew,surnameNew,emailNew,schoolNew,nicknameNew;
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            dialog = UtilitiesKC.showProgressDialog(getString(R.string.loading), getActivity());
            dialog.setCancelable(false);

            nameNew = et_name.getText().toString();
            surnameNew = et_surname.getText().toString();
            emailNew = et_email.getText().toString();
            schoolNew = et_school.getText().toString();
            nicknameNew = et_nickname.getText().toString();

            if(nameNew.isEmpty())
                nameNew = "-";
            if(surnameNew.isEmpty())
                surnameNew = "-";
            if(emailNew.isEmpty())
                emailNew = "-";
            if(schoolNew.isEmpty())
                schoolNew = "-";
            if(nicknameNew.isEmpty())
                nicknameNew = "-";

        }

        @Override
        protected void onPostExecute(Boolean success) {
            super.onPostExecute(success);

            dialog.dismissWithAnimation();

            if (!success) {
                UtilitiesKC.showErrorDialog(getString(R.string.oopss), getString(R.string.error_text_dialog), getString(R.string.ok_button), getActivity());
                return;
            }
            if (responseMA == null) {
                UtilitiesKC.showErrorDialog(getString(R.string.oopss), getString(R.string.error_text_dialog), getString(R.string.ok_button), getActivity());
                return;
            }

            if (!responseMA.success) {
                UtilitiesKC.showErrorDialog(getString(R.string.oopss), responseMA.error.messageEN, getString(R.string.ok_button), getActivity());
                return;
            }

            if(responseMA.status.statusCode.equals("801")){
                UtilitiesKC.showSuccesDialog(getString(R.string.done_title),getString(R.string.editprofile_succes),getString(R.string.ok_button),getActivity());

                Config.user.setEmail(emailNew);
                Config.user.setNickname(nicknameNew);
                Config.user.setFirstName(nameNew);
                Config.user.setLastName(surnameNew);
                Config.user.setFullName(nameNew+" "+surnameNew);
                Config.user.setSchool(schoolNew);

                ComplexPreferences cp = ComplexPreferences.getComplexPreferences(getActivity(), "prefs", Context.MODE_PRIVATE);
                cp.putObject("user", Config.user);
                cp.commit();

                return;
            }

            if(responseMA.status.statusCode.equals("207")){
                UtilitiesKC.showErrorDialog(getString(R.string.oopss),getString(R.string.editprofile_error),getString(R.string.ok_button),getActivity());
                return;
            }



        }


        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                // Simulate network access.
                MAService service = new MAService();
                if(Config.user.getRegisterType() == 'f')
                    responseMA = service.updateUser(Config.user.getUserId(),String.valueOf(Config.user.getRegisterType()),Config.user.getFbID(),Config.user.getEmail(),Config.user.getPassword(),nameNew,surnameNew,emailNew,Config.user.getPassword(),schoolNew,String.valueOf(Config.user.getGender()),nicknameNew,String.valueOf(Config.platformIdAndroid));
                else if(Config.user.getRegisterType() == 'g')
                    responseMA = service.updateUser(Config.user.getUserId(),String.valueOf(Config.user.getRegisterType()),Config.user.getGPLink(),Config.user.getEmail(),Config.user.getPassword(),nameNew,surnameNew,emailNew,Config.user.getPassword(),schoolNew,String.valueOf(Config.user.getGender()),nicknameNew,String.valueOf(Config.platformIdAndroid));
                else if(Config.user.getRegisterType() == 'm')
                    responseMA = service.updateUser(Config.user.getUserId(),String.valueOf(Config.user.getRegisterType()),"-",Config.user.getEmail(),Config.user.getPassword(),nameNew,surnameNew,emailNew,Config.user.getPassword(),schoolNew,String.valueOf(Config.user.getGender()),nicknameNew,String.valueOf(Config.platformIdAndroid));

                return true;
            } catch (Exception e) {

                return false;
            }


        }
    }
}
