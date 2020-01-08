package com.kc.mobileacademy;


import android.animation.Animator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.Wsdl2Code.WebServices.MAService.Document;
import com.Wsdl2Code.WebServices.MAService.MAService;
import com.Wsdl2Code.WebServices.MAService.ResponseMA;
import com.Wsdl2Code.WebServices.MAService.VectorDocument;
import com.kc.Model.User;
import com.kc.Util.Config;
import com.kc.Util.UtilitiesKC;
import com.kc.WidgetKC.ButtonBoldType;
import com.kc.WidgetKC.TextViewBold;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;
import br.com.kots.mob.complex.preferences.ComplexPreferences;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class LibraryFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {


    public LibraryFragment() {
        // Required empty public constructor
    }

    @Override
    public void onStop() {
        super.onStop();
        if (getDocsTask != null) {
            getDocsTask.cancel(true);
        }
    }

    public static final String CLOSE = "Close";
    View rootView;
    private View containerView;
    private int res;
    private Bitmap bitmap;
    SweetAlertDialog dialog;
    BaseDownloadTask baseDownloadTask;
    private GetLibrary getDocsTask;
    //    private ListView listView_library;
//    private LibraryListAdapter adapter;
    private LinearLayout library_linear;
    private ArrayList<Document> documentList = new ArrayList<>();
    int pageNumber = 1;
    int depID = 0;
    int index_Selected = 0;
    ViewGroup container;

    private Button addButton_temp;
    ViewGroup container_group;
    Typeface ubuntuMed, ubuntuLight;

    public static LibraryFragment newInstance() {

        Bundle args = new Bundle();

        LibraryFragment fragment = new LibraryFragment();

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_library, container, false);

        this.container = container;
        setControls();
        this.container_group = container;

        if (!UtilitiesKC.isNetworkConnected(getActivity())) {
            if (getActivity().getSharedPreferences("pref_shared", Context.MODE_PRIVATE).getBoolean("isLibLoaded", false)) {
                ComplexPreferences cp = ComplexPreferences.getComplexPreferences(getActivity(),"prefs",Context.MODE_PRIVATE);
                 VectorDocument vd = cp.getObject("library",VectorDocument.class);
                for (int i = 0; i < vd.size(); i++) {
                    this.documentList.add(vd.elementAt(i));
                }
                prepareLinearStore();
                MainActivity.searchView.showSearch();
                MainActivity.searchView.closeSearch();
                return rootView;
            }
            return rootView;
        }
        getDocsTask = new GetLibrary();
        getDocsTask.execute();


        return rootView;
    }

    private void setControls() {
//        listView_library = (ListView) rootView.findViewById(R.id.libraryfragment_listView);
        library_linear = (LinearLayout) rootView.findViewById(R.id.libraryfragment_linear);
        ubuntuMed = Typeface.createFromAsset(getActivity().getAssets(), Config.ubuntuMed);
        ubuntuLight = Typeface.createFromAsset(getActivity().getAssets(), Config.ubuntuLight);

    }


    private class GetLibrary extends AsyncTask<Void, Void, Boolean> {

        ResponseMA responseMA;
        private SweetAlertDialog dialog;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog = UtilitiesKC.showProgressDialog(getString(R.string.loading), getActivity());

        }

        @Override
        protected void onPostExecute(Boolean succ) {
            super.onPostExecute(succ);

            dialog.dismissWithAnimation();

            if (!succ) {
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
            documentList.clear();

            if (responseMA.library.documents.equals(null)) {
                UtilitiesKC.showErrorDialog(getString(R.string.oopss), getString(R.string.error_text_dialog), getString(R.string.ok_button), getActivity());
                return;
            }

            if (responseMA.library.documents == null) {
                UtilitiesKC.showErrorDialog(getString(R.string.oopss), getString(R.string.error_text_dialog), getString(R.string.ok_button), getActivity());
                return;
            }

            for (int i = 0; i < responseMA.library.documentCount; i++)
                documentList.add(responseMA.library.documents.get(i));

            ComplexPreferences cp = ComplexPreferences.getComplexPreferences(getActivity(), "prefs", Context.MODE_PRIVATE);
            cp.putObject("library", responseMA.library.documents);
            cp.commit();
            getActivity().getSharedPreferences("pref_shared", Context.MODE_PRIVATE).edit().putBoolean("isLibLoaded", true).commit();

            prepareLinearStore();
//            adapter = new LibraryListAdapter(getActivity(),R.layout.listitem_library,documentList,LibraryFragment.this);
//            listView_library.setAdapter(adapter);
//            listView_library.setOnItemClickListener(LibraryFragment.this);


            MainActivity.searchView.showSearch();
            MainActivity.searchView.closeSearch();
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            User usr = Config.user;
            try {
                MAService sv = new MAService();
                sv.setTimeOut(15);
                switch (Config.user.getRegisterType()) {
                    case 'g':
                        responseMA = sv.getLibraryByUser( Config.user.getUserId(), String.valueOf(Config.user.getRegisterType()), Config.user.getGPLink(), Config.user.getEmail(), Config.user.getPassword());
                        break;
                    case 'm':
                        responseMA = sv.getLibraryByUser( Config.user.getUserId(), String.valueOf(Config.user.getRegisterType()), Config.user.getGPLink(), Config.user.getEmail(), Config.user.getPassword());
                        break;
                    case 'f':
                        responseMA = sv.getLibraryByUser( Config.user.getUserId(), String.valueOf(Config.user.getRegisterType()), Config.user.getFbID(), Config.user.getEmail(), Config.user.getPassword());
                        break;
                    default:
                        break;
                }
                return true;
            } catch (Exception e) {
                return false;
            }

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            dialog.dismissWithAnimation();
            UtilitiesKC.showErrorDialog(getString(R.string.oopss), getString(R.string.error_text_dialog), getString(R.string.ok_button), getActivity());
        }
    }

    private void prepareLinearStore() {
        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        ViewHolder holder;
        ContextWrapper cw = new ContextWrapper(getActivity());

        for (int i = 0; i < documentList.size(); i++) {
            Document rowItem = documentList.get(i);

            final RelativeLayout docRelative = (RelativeLayout) layoutInflater.inflate(R.layout.listitem_library, container_group, false);

            holder = new ViewHolder();
            holder.tv_DocName = (TextView) docRelative.findViewById(R.id.listItemLibrary_tvDocName);
            holder.iv_Department = (ImageView) docRelative.findViewById(R.id.listItemLibrary_ivLogo);
            holder.tv_PublisherName = (TextView) docRelative.findViewById(R.id.listItemLibrary_tvPublisher);
            holder.bt_download = (ButtonBoldType) docRelative.findViewById(R.id.listItemLibrary_buttonDownloadOpen);

//            holder.bt_download = (CircularProgressButton) docRelative.findViewById(R.id.listItemLibrary_buttonDownloadOpen);


            holder.tv_DocName.setText(rowItem.displayName);
            holder.tv_PublisherName.setText(rowItem.publisherName);
            holder.iv_Department.setImageResource(Config.departmentLogoRes.get(rowItem.departmentID));


//        if(rowItem.isDownloaded)
//            holder.bt_download.setBackgroundResource(R.drawable.download_button_background);
//        else
//            holder.bt_download.setBackgroundResource(R.drawable.tick_button_background);

            holder.bt_download.setId(rowItem.docId);
            holder.bt_download.setOnClickListener(this);
            holder.tv_DocName.setTypeface(ubuntuMed);
            holder.tv_PublisherName.setTypeface(ubuntuMed);

            File file = new File(UtilitiesKC.getPDFPath(getActivity(), rowItem) + "/v" + rowItem.version + "/" + UtilitiesKC.getPDFName(rowItem));
            if (file.exists())
                documentList.get(i).isDown = true;
            else
                documentList.get(i).isDown = false;

            holder.bt_download.setTag(rowItem);
            holder.bt_download.setId(rowItem.docId);


            View v = new View(getActivity());
            v.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 2));
            v.setBackgroundColor(Color.rgb(0, 0, 0));

            final Document docRow = rowItem;
            docRelative.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if (!UtilitiesKC.isNetworkConnected(getActivity())) {
                        UtilitiesKC.showWarningDialog(getString(R.string.offline_usage),getString(R.string.no_internet_docdetail_not_opening),getString(R.string.ok_button),getActivity());
                        return;
                    }

                    DocumentDetailFragment fragment = DocumentDetailFragment.newInstance(docRow);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, fragment, "fragment").setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(fragment.getClass().getName()).commit();

                }
            });

            this.library_linear.addView(docRelative);
            this.library_linear.addView(v);
        }

        this.library_linear.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
    }

    private class ViewHolder {
        TextView tv_DocName;
        TextView tv_PublisherName;
        ImageView iv_Department;
        //        Button bt_download;
        ButtonBoldType bt_download;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        if (MainActivity.isMenuOpen)
            return;

//        DocumentViewFragment fragment = new DocumentViewFragment();
        DocumentDetailFragment fragment = DocumentDetailFragment.newInstance(documentList.get(position));
//        Bundle bundle = new Bundle();
//        bundle.putString("link", documentList.get(position).pDFLink);
//        fragment.setArguments(bundle);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, fragment, "fragment").setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(fragment.getClass().getName()).commit();

    }

    @Override
    public void onClick(View v) {

        if (MainActivity.isMenuOpen)
            return;


//        Toast.makeText(getActivity(),"geldi",Toast.LENGTH_SHORT).show();
//        return;
        for (int i = 0; i < this.documentList.size(); i++) {
            if (v.getId() == this.documentList.get(i).docId) {
                index_Selected = i;
                break;
            }
        }

        final Document doc = documentList.get(index_Selected);


        if (doc.isDown) {

            DocumentViewFragment fragment = DocumentViewFragment.newInstance(UtilitiesKC.getPDFName(doc), UtilitiesKC.getPDFPath(getActivity(), doc) + "/v" + doc.version + "/", DocumentViewFragment.MODE_OPEN);
            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.main_fragment, fragment, "fragment").setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(fragment.getClass().getName()).commit();
            return;
        }

        if (!UtilitiesKC.isNetworkConnected(getActivity())) {
            UtilitiesKC.showWarningDialog(getString(R.string.offline_usage),getString(R.string.no_internet_no_download),getString(R.string.ok_button),getActivity());
            return;
        }


        new AsyncTask<Void, Void, Void>() {
            int lenghtOfFile = -1;

            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                if (lenghtOfFile > 0) {
                    dialog.setTitleText(getString(R.string.are_you_sure_download));
                    dialog.setContentText(getString(R.string.size_of_file) + String.format("%.1f", (double) lenghtOfFile / 1000000) + " MB");
                    dialog.setConfirmText(getString(R.string.download_now));
                    dialog.setCancelText(getString(R.string.dialog_cancel));
                    dialog.setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sweetAlertDialog) {

                            sweetAlertDialog.dismissWithAnimation();

                            final AlertDialog dialog;
                            View progressView;
                            final TextViewBold tv_loading;
                            Button cancel_button;
                            final CircleProgressView circleProgressView;


                            progressView = ((LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.download_progressview, container, false);


                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setView(progressView);
                            builder.setCancelable(false);
                            dialog = builder.create();

                            cancel_button = (Button) progressView.findViewById(R.id.progressview_cancelButton);
                            tv_loading = (TextViewBold) progressView.findViewById(R.id.progressview_tvloading);
                            circleProgressView = (CircleProgressView) progressView.findViewById(R.id.circleView);

                            cancel_button.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    if (baseDownloadTask != null)
                                        baseDownloadTask.setListener(null);
                                    getActivity().getSupportFragmentManager().popBackStack();
                                    UtilitiesKC.showWarningToast("Loading is canceled.", getActivity());
                                    dialog.dismiss();
                                }
                            });


                            circleProgressView.setTextTypeface(Typeface.createFromAsset(getActivity().getAssets(), Config.ubuntuBold));
                            circleProgressView.setTextMode(TextMode.PERCENT);
                            circleProgressView.spin();

                            dialog.show();

                            baseDownloadTask = FileDownloader.getImpl().create(doc.pDFLink)
                                    .setPath(UtilitiesKC.getPDFPath(getActivity(), doc) + "/v" + doc.version + "/" + UtilitiesKC.getPDFName(doc))
                                    .setListener(new FileDownloadListener() {
                                        @Override
                                        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                                        }

                                        @Override
                                        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                                            if (totalBytes == 0)
                                                return;

                                            tv_loading.setText(getString(R.string.loading_document) + " : %" + 100 * soFarBytes / totalBytes);
                                            circleProgressView.stopSpinning();
                                            circleProgressView.setValue(100 * soFarBytes / totalBytes);

                                            Log.d("DOWNLOAD", "status: " + task.getStatus() + "  Durum: " + 100 * soFarBytes / totalBytes + "  Sofar: " + soFarBytes + "  Total: " + totalBytes);
                                        }

                                        @Override
                                        protected void blockComplete(BaseDownloadTask task) {

                                        }

                                        @Override
                                        protected void completed(BaseDownloadTask task) {

                                            dialog.dismiss();
                                            DocumentViewFragment fragment = DocumentViewFragment.newInstance(UtilitiesKC.getPDFName(doc), UtilitiesKC.getPDFPath(getActivity(), doc) + "/v" + doc.version + "/", DocumentViewFragment.MODE_OPEN);
                                            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.main_fragment, fragment, "fragment").setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(fragment.getClass().getName()).commit();


                                        }

                                        @Override
                                        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                                        }

                                        @Override
                                        protected void error(BaseDownloadTask task, Throwable e) {
                                            Log.d("ERROR DOWNLOAD", task.getPath() + "  - " + e.getMessage());
                                            UtilitiesKC.showErrorDialog(getString(R.string.oopss), getString(R.string.error_text_dialog), getString(R.string.ok_button), getActivity());
                                            return;

                                        }

                                        @Override
                                        protected void warn(BaseDownloadTask task) {

                                        }
                                    });
                            baseDownloadTask.start();
                        }
                    });
                    dialog.changeAlertType(SweetAlertDialog.WARNING_TYPE);
                }
            }

            @Override
            protected Void doInBackground(Void... params) {
                try {
                    URL url = new URL(doc.pDFLink);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("HEAD");
                    connection.connect();
                    lenghtOfFile = connection.getContentLength();
                } catch (IOException e) {

                }
                return null;
            }

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                dialog = UtilitiesKC.showProgressDialog(getString(R.string.loading), getActivity());


            }
        }.execute();


        //DOWNLOAD BAŞLIYORRR


//        button.setIndeterminateProgressMode(true);
//        button.setProgress(50);


//        DownloadManager downloadManager = (DownloadManager)getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
//        Uri Download_Uri = Uri.parse(documentList.get(index_Selected).pDFLink);
//        DownloadManager.Request request = new DownloadManager.Request(Download_Uri);
//
//        //Restrict the types of networks over which this download may proceed.
//        request.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI | DownloadManager.Request.NETWORK_MOBILE);
//        //Set whether this download may proceed over a roaming connection.
//        request.setAllowedOverRoaming(false);
//        //Set the title of this download, to be displayed in notifications (if enabled).
//        request.setTitle("Mobile Academy");
//        //Set a description of this download, to be displayed in notifications (if enabled)
//        request.setDescription(documentList.get(index_Selected).displayName+" is downloading..");
//        //Set the local destination for the downloaded file to a path within the application's external files directory
//        request.setDestinationUri(Uri.parse(UtilitiesKC.getPDFPath(getActivity(), doc)+ UtilitiesKC.getPDFName(doc)));
//
//
//        //Enqueue a new download and same the referenceId
//        long downloadReference;downloadReference = downloadManager.enqueue(request);


//        FileDownloader.getImpl().create(documentList.get(index_Selected).pDFLink)
//                .setPath(UtilitiesKC.getPDFPath(getActivity(), doc) + UtilitiesKC.getPDFName(doc))
//                .setListener(new FileDownloadListener() {
//                    @Override
//                    protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
//                        button.setIndeterminateProgressMode(true);
//                        button.setProgress(50);
//                        Log.d("PENDING", "SoFar: " + soFarBytes + "  |  total: " + totalBytes);
//                    }
//
//                    @Override
//                    protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
////                        publishProgress((int) soFarBytes / totalBytes);
//                        if (totalBytes == 0)
//                            return;
//
//                        if (button.isIndeterminateProgressMode())
//                            button.setIndeterminateProgressMode(false);
//
//                        if (100 * soFarBytes / totalBytes != 0)
//                            button.setProgress(100 * soFarBytes / totalBytes);
//
//                        Log.d("PROGRESS", "Durum: " + 100 * soFarBytes / totalBytes + "|   Sofar: " + soFarBytes + "  |Total: " + totalBytes);
//                    }
//
//                    @Override
//                    protected void blockComplete(BaseDownloadTask task) {
//
//                    }
//
//                    @Override
//                    protected void completed(BaseDownloadTask task) {
//                        button.setProgress(100);
//                    }
//
//                    @Override
//                    protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
//
//                    }
//
//                    @Override
//                    protected void error(BaseDownloadTask task, Throwable e) {
//                        button.setProgress(-1);
//                        Log.d("ERROR DOWNLOAD", task.getPath() + "  - " + e.getMessage());
//                    }
//
//                    @Override
//                    protected void warn(BaseDownloadTask task) {
//
//                    }
//                }).start();


//        this.addButton_temp = (Button)v;
//        makeTick(addButton_temp);


//
//        DocumentViewFragment fragment = DocumentViewFragment.newInstance(this.documentList.get(index_Selected).pDFLink);
//        getActivity().getSupportFragmentManager().beginTransaction().add(R.id.main_fragment, fragment, "fragment").setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(fragment.getClass().getName()).commit();


//        UtilitiesKC.showSuccessToast(getString(R.string.added_to_library_toast), getActivity());
//        this.documentList.get(index_Selected).isHave = true;
//        new AddToLibraryAsync(index_Selected,Config.user.getUserId(),this.documentList.get(index_Selected).docId,1,this.documentList.get(index_Selected).price).execute();


    }

    private void makeTick(final Button bt) {
        bt.animate().alpha(0.0f).setDuration(500).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                bt.setBackgroundResource(R.drawable.tick_button_background);
                bt.animate().alpha(1.0f).setDuration(500);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });

    }

    private class AddToLibraryAsync extends AsyncTask<Void, Void, Void> {

        int userid, docid, platid, pric;
        ResponseMA responseMA = null;
        int index;

        public AddToLibraryAsync(int index_, int userId, int docId, int platformId, int price) {
            this.userid = userId;
            this.docid = docId;
            this.platid = platformId;
            this.pric = price;
            this.index = index_;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            Log.d("EKLE", responseMA.message);

            if (responseMA == null) {
                UtilitiesKC.showErrorToast(getString(R.string.error_text_toast), getActivity());
                addButton_temp.setBackgroundResource(R.drawable.plus_button_background);
                documentList.get(index_Selected).isHave = false;
                return;
            }

            if (responseMA.success) {

            } else {
                addButton_temp.setBackgroundResource(R.drawable.plus_button_background);
                UtilitiesKC.showErrorToast(getString(R.string.error_text_toast), getActivity());
                documentList.get(index_Selected).isHave = false;
            }

        }


        @Override
        protected Void doInBackground(Void... params) {

            try {
                MAService sv = new MAService();
                responseMA = sv.addToLibrary( userid, docid, platid, pric);
            } catch (Exception e) {
                responseMA = null;
            }

            return null;
        }
    }


}
