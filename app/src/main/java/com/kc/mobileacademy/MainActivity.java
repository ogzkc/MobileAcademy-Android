package com.kc.mobileacademy;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.Wsdl2Code.WebServices.MAService.MAService;
import com.afollestad.materialdialogs.MaterialDialog;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.kc.Util.Config;
import com.kc.Util.UtilitiesKC;
import com.kc.WidgetKC.ButtonBoldType;
import com.kc.WidgetKC.MenuKCTextView;
import com.miguelcatalan.materialsearchview.MaterialSearchView;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yalantis.guillotine.animation.GuillotineAnimation;
import com.yalantis.guillotine.interfaces.GuillotineListener;

import br.com.kots.mob.complex.preferences.ComplexPreferences;
import cn.pedant.SweetAlert.SweetAlertDialog;


public class MainActivity extends AppCompatActivity implements GuillotineListener, View.OnClickListener {

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private Fragment contentFragment;
    private int res = R.drawable.ic_library;
    private LinearLayout linearLayout;

    public static Boolean isMenuOpen = false;
    GuillotineAnimation menuGuill;


    Toolbar toolbar;
    FrameLayout root;
    View contentHamburger;

    MenuKCTextView tv_profile;
    MenuKCTextView tv_library;
    MenuKCTextView tv_store;
    LinearLayout linear_profile;
    LinearLayout linear_library;
    LinearLayout linear_store;
    Button menu_logoutButton;
    ImageView menu_ivProfile;
    TextView menu_tvName;
    TextView menu_tvSchool;
    Button menu_closeMenu;
    ButtonBoldType menu_feedback;
    RelativeLayout menu_relative;
    public static MaterialSearchView searchView;

    Boolean isSearchMade = false;

    View guillotineMenu;

    Typeface typeUbuntuBold;
    Typeface typeUbunduMed;

    DisplayImageOptions options;
    ImageLoaderConfiguration config;
    ImageLoader loader;

    private static final long RIPPLE_DURATION = 250;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);

        initVars();
        setControls();
        setSearchView();


        contentFragment = StoreFragment.newInstance();
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.main_fragment, contentFragment)
                .commit();

        tv_store.makeSelected();

        if (searchView.isSearchOpen())
            searchView.closeSearch();

        String loggedin = getResources().getString(R.string.login_succesfull);
        switch (Config.user.getRegisterType()) {
            case 'm':
                loggedin += " by Email";
                break;
            case 'f':
                loggedin += " by Facebook";
                break;
            case 'g':
                loggedin += " by Google+";
                break;
            default:
                break;
        }
        UtilitiesKC.showSuccessToast(loggedin, this);


        checkUpdateAnnouncement();

    }

    @Override
    public void onPostCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        searchView.showSearch();
        searchView.closeSearch();
    }

    private void initVars() {
        typeUbuntuBold = Typeface.createFromAsset(getAssets(), Config.ubuntuBold);
        typeUbunduMed = Typeface.createFromAsset(getAssets(), Config.ubuntuMed);

        config = new ImageLoaderConfiguration.Builder(this)
                .build();
        loader = ImageLoader.getInstance();
        loader.init(config);
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(false)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

    }

    private void setControls() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        root = (FrameLayout) findViewById(R.id.root);
        contentHamburger = findViewById(R.id.content_hamburger);

        if (toolbar != null) {
            setSupportActionBar(toolbar);
            getSupportActionBar().setTitle(null);
        }

        guillotineMenu = LayoutInflater.from(this).inflate(R.layout.guillotine, null, false);
        root.addView(guillotineMenu);


        menuGuill = new GuillotineAnimation.GuillotineBuilder(guillotineMenu, guillotineMenu.findViewById(R.id.guillotine_hamburger), contentHamburger)
                .setActionBarViewForAnimation(toolbar)
                .setClosedOnStart(true)
                .setGuillotineListener(this)
                .build();

        searchView = (MaterialSearchView) findViewById(R.id.search_view);
        linear_profile = (LinearLayout) guillotineMenu.findViewById(R.id.profile_linear);
        linear_library = (LinearLayout) guillotineMenu.findViewById(R.id.library_linear);
        linear_store = (LinearLayout) guillotineMenu.findViewById(R.id.store_linear);


        tv_profile = (MenuKCTextView) linear_profile.findViewById(R.id.profile_tv);
        tv_library = (MenuKCTextView) linear_library.findViewById(R.id.library_tv);
        tv_store = (MenuKCTextView) linear_store.findViewById(R.id.store_tv);

        linear_library.setOnClickListener(this);
        linear_store.setOnClickListener(this);
        linear_profile.setOnClickListener(this);

        menu_closeMenu = (Button) guillotineMenu.findViewById(R.id.menu_closeButton);
        menu_logoutButton = (Button) guillotineMenu.findViewById(R.id.menu_logOut);
        menu_tvName = (TextView) guillotineMenu.findViewById(R.id.menu_tvprofileName);
        menu_feedback = (ButtonBoldType) guillotineMenu.findViewById(R.id.menu_sendFeedbackButton);
        menu_tvSchool = (TextView) guillotineMenu.findViewById(R.id.menu_tvprofileschool);
        menu_ivProfile = (ImageView) guillotineMenu.findViewById(R.id.menu_ivprofile);

        menu_closeMenu.setOnClickListener(this);
        menu_logoutButton.setOnClickListener(this);
        menu_feedback.setOnClickListener(this);

        menu_tvName.setText(Config.user.getFullName());
        menu_tvSchool.setText(Config.user.getSchool());

        menu_tvName.setTypeface(typeUbuntuBold);
        menu_tvSchool.setTypeface(typeUbunduMed);

        ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
// generate random color
        int color1 = generator.getRandomColor();

        final TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .withBorder(4) /* thickness in px */
                .endConfig()
                .buildRoundRect(Config.user.getFirstName().charAt(0) + "" + Config.user.getLastName().charAt(0), color1, 10);

        if(Config.user.getRegisterType() == 'f'){
            ImageLoader.getInstance().displayImage("http://graph.facebook.com/" + Config.user.getFbID() + "/picture?type=large", menu_ivProfile, options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                    menu_ivProfile.setImageDrawable(drawable);
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    Bitmap roundedBmp = UtilitiesKC.getRoundedBitmap(bitmap);
                    menu_ivProfile.setImageBitmap(roundedBmp);
                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });
        }else if (Config.user.getRegisterType() == 'g'){
            Log.d("GooglePhoto",Config.user.getGooglePhotoURL());
            ImageLoader.getInstance().displayImage(Config.user.getGooglePhotoURL(), menu_ivProfile, options, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String s, View view) {

                }

                @Override
                public void onLoadingFailed(String s, View view, FailReason failReason) {
                    menu_ivProfile.setImageDrawable(drawable);
                }

                @Override
                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                    Bitmap roundedBmp = UtilitiesKC.getRoundedBitmap(bitmap);
                    menu_ivProfile.setImageBitmap(roundedBmp);
                }

                @Override
                public void onLoadingCancelled(String s, View view) {

                }
            });
        }else{
            menu_ivProfile.setImageDrawable(drawable);
        }


    }

    private void checkUpdateAnnouncement() {
        if (Config.Announcement.isActive) {
            final SweetAlertDialog dialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE);
            dialog.setTitleText(Config.Announcement.alertTitle);
            dialog.setContentText(Config.Announcement.alertMessage);
            dialog.setConfirmText(getString(R.string.ok_button));
            dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                @Override
                public void onClick(SweetAlertDialog sweetAlertDialog) {
                    sweetAlertDialog.dismissWithAnimation();
                    showUpdateDialog();
                }
            });
            dialog.show();
            return;
        }

        int versionCode = 1;
        try {
            versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (Config.versionControl.isActive && Config.versionControl.alertInteger > versionCode)
            showUpdateDialog();
    }

    private void showUpdateDialog() {

        int versionCode = 1;
        try {
            versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        if (!Config.versionControl.isActive || versionCode == Config.versionControl.alertInteger)
            return;

        SweetAlertDialog dialog = new SweetAlertDialog(MainActivity.this, SweetAlertDialog.WARNING_TYPE);
        dialog.setTitleText(Config.versionControl.alertTitle);
        dialog.setContentText(Config.versionControl.alertMessage);
        dialog.setConfirmText(getString(R.string.update_now_button));
        dialog.setCancelText(getString(R.string.later_update_button));
        dialog.showCancelButton(true);
        dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
            @Override
            public void onClick(SweetAlertDialog sweetAlertDialog) {
                final String appPackageName = getPackageName(); // getPackageName() from Context or Activity object
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
                } catch (android.content.ActivityNotFoundException anfe) {
                    UtilitiesKC.showErrorToast(getString(R.string.play_market_notreacheable), MainActivity.this);
                }
                sweetAlertDialog.dismissWithAnimation();
            }
        });
        dialog.show();
    }


    private void setSearchView() {
        searchView.setOnQueryTextListener(new MaterialSearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                //Do some magic
//                Toast.makeText(getApplicationContext(), "Arama Yap: " + query, Toast.LENGTH_SHORT).show();
                if((getSupportFragmentManager().findFragmentById(R.id.main_fragment) instanceof StoreFragment) == false){
                    return false;
                }
                StoreFragment fragment = (StoreFragment) getSupportFragmentManager().findFragmentById(R.id.main_fragment);
                fragment.search(query);
                isSearchMade = true;
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                //Do some magic
                return false;
            }
        });

        searchView.setOnSearchViewListener(new MaterialSearchView.SearchViewListener() {
            @Override
            public void onSearchViewShown() {
                //Do some magic

            }

            @Override
            public void onSearchViewClosed() {
                //Do some magic
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.profile_linear:
                getSupportFragmentManager().popBackStackImmediate("fragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                contentFragment = ProfileFragment.newInstance();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment, contentFragment, "fragment")
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
                menuGuill.close();
                allUnselected();
                tv_profile.makeSelected();
                break;
            case R.id.menu_sendFeedbackButton:
                new MaterialDialog.Builder(this)
                        .title(R.string.send_feedback_title)
                        .content(R.string.feedback_important)
                        .inputType(InputType.TYPE_CLASS_TEXT )
                        .neutralText("Cancel")
                        .input(R.string.enter_feedback, R.string.empty, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, final CharSequence input) {
                                // Do something
                                if(input.length() < 3){
                                    dialog.dismiss();
                                    return;
                                }

                                final String msg = input.toString();
                                new AsyncTask<Void,Void,Void>(){
                                    @Override
                                    protected Void doInBackground(Void... params) {
                                        MAService sv = new MAService();
                                        sv.sendFeedback(Config.user.getUserId(),Config.platformIdAndroid,msg,0);
                                        return null;
                                    }

                                    @Override
                                    protected void onPostExecute(Void aVoid) {
                                        super.onPostExecute(aVoid);
                                        UtilitiesKC.showSuccessToast(getString(R.string.thanks_feedback), MainActivity.this);
                                    }
                                }.execute();
                                dialog.dismiss();
                            }
                        }).show();
                break;
            case R.id.library_linear:
                getSupportFragmentManager().popBackStackImmediate("fragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                contentFragment = LibraryFragment.newInstance();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment, contentFragment, "fragment")
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
                menuGuill.close();
                allUnselected();
                tv_library.makeSelected();
                break;
            case R.id.store_linear:
                getSupportFragmentManager().popBackStackImmediate("fragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                getSupportFragmentManager().popBackStackImmediate(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
                contentFragment = StoreFragment.newInstance();
                getSupportFragmentManager().beginTransaction()
                        .replace(R.id.main_fragment, contentFragment, "fragment")
                        .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
                        .commit();
                menuGuill.close();
                allUnselected();
                tv_store.makeSelected();
                break;
            case R.id.menu_closeButton:
                menuGuill.close();
                break;
            case R.id.menu_logOut:
                ComplexPreferences cp = ComplexPreferences.getComplexPreferences(MainActivity.this, "prefs", Context.MODE_PRIVATE);
                if (cp.getObject("signIn", Character.class) == 'f') {
                    LoginManager.getInstance().logOut();
                }
                cp.putObject("signIn", '-');
                cp.commit();
                finish();
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
                break;
            default:
                break;
        }
    }

    public void allUnselected() {
        tv_store.makeUnselected();
        tv_library.makeUnselected();
        tv_profile.makeUnselected();
        searchView.showSearch();
        searchView.closeSearch();
    }

    @Override
    public void onGuillotineOpened() {
        isMenuOpen = true;
    }

    @Override
    public void onGuillotineClosed() {
        isMenuOpen = false;
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);

    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//
//        MenuItem item = menu.findItem(R.id.action_search);
//        searchView.setMenuItem(item);
//
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    public void onBackPressed() {
        if (isMenuOpen) {
            menuGuill.close();
        } else {
            if (isSearchMade) {
                isSearchMade = false;

                if (searchView.isSearchOpen()) {
                    searchView.closeSearch();
                } else {
                    FragmentManager fm = getSupportFragmentManager();
                    if (fm.getBackStackEntryCount() > 0) {
                        Log.i("MainActivity", "popping backstack");
                        fm.popBackStack();
                    } else {
                        super.onBackPressed();
                    }
                }

//                if(getSupportFragmentManager().findFragmentById(R.id.main_fragment) instanceof StoreFragment) {
//                    StoreFragment fragment = (StoreFragment) getSupportFragmentManager().findFragmentById(R.id.main_fragment);
//                    fragment.getBacktoStore();
//                }else if(getSupportFragmentManager().findFragmentById(R.id.main_fragment) instanceof DocumentDetailFragment){
//                    FragmentManager fm = getSupportFragmentManager();
//                    if (fm.getBackStackEntryCount() > 0) {
//                        Log.i("MainActivity", "popping backstack");
//                        fm.popBackStack();
//                    }
//                }
            } else {
                if (searchView.isSearchOpen()) {
                    searchView.closeSearch();
                } else {
                    FragmentManager fm = getSupportFragmentManager();
                    if (fm.getBackStackEntryCount() > 0) {
                        Log.i("MainActivity", "popping backstack");
                        fm.popBackStack();
                    } else {
                        super.onBackPressed();
                    }
                }
            }

        }
    }


}
