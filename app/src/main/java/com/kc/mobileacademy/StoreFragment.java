package com.kc.mobileacademy;


import android.animation.Animator;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.Wsdl2Code.WebServices.MAService.Document;
import com.Wsdl2Code.WebServices.MAService.MAService;
import com.Wsdl2Code.WebServices.MAService.ResponseMA;
import com.kc.Adapters.StoreListAdapter;
import com.kc.Util.Config;
import com.kc.Util.UtilitiesKC;
import com.kc.WidgetKC.ButtonBoldType;
import com.kc.WidgetKC.TextViewBold;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.File;
import java.util.ArrayList;

import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;
import cn.pedant.SweetAlert.SweetAlertDialog;

/**
 * A simple {@link Fragment} subclass.
 */
public class StoreFragment extends Fragment implements AdapterView.OnItemClickListener, View.OnClickListener {


    public StoreFragment() {
        // Required empty public constructor
    }

    public static final String CLOSE = "Close";
    View rootView;
    private View containerView;
    private int res;
    private Bitmap bitmap;
    private ViewGroup container;

    MenuItem menuItem;
    BaseDownloadTask baseDownloadTask;
    private GetDocumentsByDepartment getDocsTask;
    private ListView listView_Store;
    private StoreListAdapter adapter;
    private ArrayList<Document> documentList = new ArrayList<>();
    int pageNumber = 1;
    int depID = 0;
    int index_Selected = 0;

    private Button addButton_temp;

    @Override
    public void onStop() {
        super.onStop();
        if(getDocsTask != null){
            getDocsTask.cancel(true);
        }
    }

    public static StoreFragment newInstance() {

        Bundle args = new Bundle();

        StoreFragment fragment = new StoreFragment();

        fragment.setArguments(args);
        fragment.setHasOptionsMenu(true);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_store, container, false);

        this.container = container;
        setControls();

        if(!UtilitiesKC.isNetworkConnected(getActivity())){
            if(getActivity().getSharedPreferences("pref_shared",Context.MODE_PRIVATE).getBoolean("isLibLoaded",false)){
                SweetAlertDialog.OnSweetClickListener listener = new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sweetAlertDialog) {

                        getActivity().getSupportFragmentManager().popBackStackImmediate("fragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
                        LibraryFragment fragment = LibraryFragment.newInstance();
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, fragment, "fragment").setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).commit();
                        Log.d("OnClick","onclick geldi sweet");
//
//                        ((MainActivity)getActivity()).allUnselected();
//                        ((MainActivity)getActivity()).tv_library.makeSelected();
                        sweetAlertDialog.dismissWithAnimation();
                    }
                };
                UtilitiesKC.showWarningDialogCustomClickListener(getString(R.string.offline_usage),getString(R.string.no_internet_library_opening),getString(R.string.ok_button),getActivity(),listener);

                return rootView;
            }
            UtilitiesKC.showWarningDialog(getString(R.string.oopss), getString(R.string.no_internet_conn), getString(R.string.ok_button), getActivity());
            return rootView;
        }

        getDocsTask = new GetDocumentsByDepartment(Config.user.getUserId(), depID, pageNumber);
        getDocsTask.execute();


        return rootView;
    }

    private void setControls() {
        listView_Store = (ListView) rootView.findViewById(R.id.storefragment_listView);


    }


    private class GetDocumentsByDepartment extends AsyncTask<Void, Void, Boolean> {

        ResponseMA responseMA;
        private SweetAlertDialog dialog;
        private int userId, depId, page;

        public GetDocumentsByDepartment(int userId, int depId, int page) {
            this.userId = userId;
            this.page = page;
            this.depId = depId;
        }


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

            if(responseMA == null){
                UtilitiesKC.showErrorDialog(getString(R.string.oopss), getString(R.string.error_text_dialog), getString(R.string.ok_button), getActivity());
                return;
            }

            if (!responseMA.success) {
                UtilitiesKC.showErrorDialog(getString(R.string.oopss), responseMA.error.messageEN, getString(R.string.ok_button), getActivity());
                return;
            }
            documentList.clear();

            if (responseMA.library.documents == null)
                return;

            for (int i = 0; i < responseMA.library.documentCount; i++)
                documentList.add(responseMA.library.documents.get(i));



            adapter = new StoreListAdapter(getActivity(), R.layout.listitem_store, documentList, StoreFragment.this);
            listView_Store.setAdapter(adapter);
//            listView_Store.setOnItemClickListener(StoreFragment.this);

//
            MainActivity.searchView.showSearch();
            MainActivity.searchView.closeSearch();
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                MAService sv = new MAService();
                responseMA = sv.getDocumentsByDepartment( this.userId, this.depId, this.page);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
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


    private class GetDocumentsByName extends AsyncTask<Void, Void, Boolean> {

        ResponseMA responseMA;
        private SweetAlertDialog dialog;
        private int userId, depId, page;
        private String queryStr;

        public GetDocumentsByName(int userId, int depId, int page,String query) {
            this.userId = userId;
            this.page = page;
            this.depId = depId;
            this.queryStr = query;
        }


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

            if (!responseMA.success) {
                UtilitiesKC.showErrorDialog(getString(R.string.oopss), responseMA.error.messageEN, getString(R.string.ok_button), getActivity());
                return;
            }
            documentList.clear();

            if (responseMA.library.documents == null)
                return;

            for (int i = 0; i < responseMA.library.documentCount; i++)
                documentList.add(responseMA.library.documents.get(i));

            adapter = new StoreListAdapter(getActivity(), R.layout.listitem_store, documentList, StoreFragment.this);
            listView_Store.setAdapter(adapter);
//            listView_Store.setOnItemClickListener(StoreFragment.this);

//
            MainActivity.searchView.showSearch();
            MainActivity.searchView.closeSearch();
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                MAService sv = new MAService();
                responseMA = sv.getDocumentsByNameAndDepartment( this.userId, this.queryStr, this.depId, this.page);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
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

        if(MainActivity.isMenuOpen)
            return;

        if(v instanceof ButtonBoldType){


            Document docTemp = (Document)v.getTag();
            DocumentDetailFragment fragment = DocumentDetailFragment.newInstance(docTemp);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.main_fragment, fragment, "fragment").setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(fragment.getClass().getName()).commit();

            return;
        }

        for (int i = 0; i < this.documentList.size(); i++) {
            if (v.getId() == this.documentList.get(i).docId) {
                index_Selected = i;
                break;
            }
        }
        final Document document = this.documentList.get(index_Selected);

        if (!this.documentList.get(index_Selected).isHave) {

            this.addButton_temp = (Button) v;
            makeTick(addButton_temp);
            this.documentList.get(index_Selected).isHave = true;
            new AddToLibraryAsync(index_Selected, Config.user.getUserId(), this.documentList.get(index_Selected).docId, 1, this.documentList.get(index_Selected).price).execute();

        }

        File file = new File(UtilitiesKC.getPDFPath(getActivity(),document)+"/v"+document.version+"/"+UtilitiesKC.getPDFName(document));
        if(file.exists()){

            DocumentViewFragment fragment = DocumentViewFragment.newInstance(UtilitiesKC.getPDFName(document), UtilitiesKC.getPDFPath(getActivity(), document)+"/v"+document.version+"/", DocumentViewFragment.MODE_OPEN);
            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.main_fragment, fragment, "fragment").setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(fragment.getClass().getName()).commit();
            return;

        }else{

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
            circleProgressView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if(baseDownloadTask.isContinue())
                        baseDownloadTask.pause();
                    else
                        baseDownloadTask.start();
                    return false;
                }
            });
            cancel_button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (baseDownloadTask != null)
                        baseDownloadTask.setListener(new FileDownloadListener() {
                            @Override
                            protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                            }

                            @Override
                            protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                            }

                            @Override
                            protected void blockComplete(BaseDownloadTask task) {

                            }

                            @Override
                            protected void completed(BaseDownloadTask task) {
                                UtilitiesKC.showSuccessToast(getString(R.string.download_background),getActivity());
                            }

                            @Override
                            protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

                            }

                            @Override
                            protected void error(BaseDownloadTask task, Throwable e) {

                            }

                            @Override
                            protected void warn(BaseDownloadTask task) {

                            }
                        });

                    getActivity().getSupportFragmentManager().popBackStack();
                    UtilitiesKC.showInfoToast("Loading is continue in background.", getActivity(),null);
                    dialog.dismiss();
                }
            });


            circleProgressView.setTextTypeface(Typeface.createFromAsset(getActivity().getAssets(), Config.ubuntuBold));
            circleProgressView.setTextMode(TextMode.PERCENT);
            circleProgressView.spin();

            dialog.show();

            baseDownloadTask = FileDownloader.getImpl().create(document.pDFLink)
                    .setPath(UtilitiesKC.getPDFPath(getActivity(), document) +"/v"+document.version+"/"+ UtilitiesKC.getPDFName(document))
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
                            circleProgressView.setValue( 100 * soFarBytes / totalBytes);

                            Log.d("DOWNLOAD", "status: " + task.getStatus() + "  Durum: " + 100 * soFarBytes / totalBytes + "  Sofar: " + soFarBytes + "  Total: " + totalBytes);
                        }

                        @Override
                        protected void blockComplete(BaseDownloadTask task) {

                        }

                        @Override
                        protected void completed(BaseDownloadTask task) {

                            dialog.dismiss();
                            DocumentViewFragment fragment = DocumentViewFragment.newInstance(UtilitiesKC.getPDFName(document), UtilitiesKC.getPDFPath(getActivity(), document)+"/v"+document.version+"/", DocumentViewFragment.MODE_OPEN);
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




    }

    private void makeTick(final Button bt) {
        bt.animate().alpha(0.0f).setDuration(400).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                bt.setBackgroundResource(R.drawable.tick_button_background);
                bt.animate().alpha(1.0f).setDuration(400);
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


            try {
                if (responseMA == null) {
                    UtilitiesKC.showErrorToast(getString(R.string.error_text_toast), getActivity());
    //                addButton_temp.setBackgroundResource(R.drawable.plus_button_background);
                    documentList.get(index_Selected).isHave = false;
                    return;
                }

                if (responseMA.success) {

                } else {
                    addButton_temp.setBackgroundResource(R.drawable.plus_button_background);
    //                UtilitiesKC.showErrorToast(getString(R.string.error_text_toast), getActivity());
                    documentList.get(index_Selected).isHave = false;
                }
            } catch (Exception e) {

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


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
//
        inflater.inflate(R.menu.menu_store, menu);
        menuItem = menu.findItem(R.id.actionstore_search);
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
//            case R.id.actionstore_filter:
//                new MaterialDialog.Builder(getActivity())
//                        .title(R.string.choose_departments)
//                        .items(R.array.departments)
//                        .itemsCallbackMultiChoice(null, new MaterialDialog.ListCallbackMultiChoice() {
//                            @Override
//                            public boolean onSelection(MaterialDialog dialog, Integer[] which, CharSequence[] text) {
//
//                                String aa = "";
//                                for (int i = 0; i < which.length; i++)
//                                    aa += " - " + which[i];
//
//                                Toast.makeText(getActivity(), aa, Toast.LENGTH_SHORT).show();
//                                dialog.dismiss();
//
//
//                                return true;
//                            }
//                        })
//                        .onNeutral(new MaterialDialog.SingleButtonCallback() {
//                            @Override
//                            public void onClick(MaterialDialog dialog, DialogAction which) {
//                                dialog.selectAllIndicies();
//                            }
//                        })
//                        .autoDismiss(false)
//                        .neutralText(R.string.choose_all)
//                        .positiveText(R.string.choose_button)
//                        .show();
//                break;
            case R.id.actionstore_search:
                MainActivity.searchView.showSearch();

                break;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }


    public void search(String str){
//        Toast.makeText(getActivity(),"geldi "+str,Toast.LENGTH_SHORT).show();
        GetDocumentsByName gp = new GetDocumentsByName(Config.user.getUserId(), depID, pageNumber,str);
        gp.execute();
    }

    public void getBacktoStore(){
        getDocsTask = new GetDocumentsByDepartment(Config.user.getUserId(), depID, pageNumber);
        getDocsTask.execute();
    }

}
