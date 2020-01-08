package com.kc.mobileacademy;


import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.github.johnpersano.supertoasts.SuperToast;
import com.github.johnpersano.supertoasts.util.OnClickWrapper;
import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnLoadCompleteListener;
import com.joanzapata.pdfview.util.DragPinchListener;
import com.kc.Util.Config;
import com.kc.Util.UtilitiesKC;
import com.kc.WidgetKC.TextViewBold;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;


/**
 * A simple {@link Fragment} subclass.
 */
public class DocumentViewFragment extends Fragment implements OnLoadCompleteListener, DragPinchListener.OnDoubleTapListener, View.OnClickListener {

    PDFView pdfView;

    @Override
    public void onStop() {
        super.onStop();
        if(this.mode == MODE_OPEN) {
            getActivity().getSharedPreferences("pref_doc", Context.MODE_PRIVATE).edit().putInt(this.fileName, this.page).commit();
        }
    }

    public DocumentViewFragment() {
        // Required empty public constructor
    }

    public static int MODE_DOWNLOAD = 1;
    public static int MODE_OPEN = 2;


    View rootView;

    Boolean firstOpening = true;
    private String pdf_path;
    private String link_pdf;
    private int mode;

    String fileName = null;

    CountDownTimer timerClose;


    private DownloadPDF downloadTask = null;
    ViewGroup container;
    int page = 1;
    float minimumZoom;
    Button button_nextpage;
    Button button_prevpage;
    TextViewBold tv_page;

    Boolean isNavHide = false;

    ArrayList<String> pages = new ArrayList<>();
    RelativeLayout relative_nav;
    float relative_y;


    public static DocumentViewFragment newInstance(String link, int mode) {

        Bundle args = new Bundle();
        args.putString("link", link);
        args.putInt("mode", MODE_DOWNLOAD);
        DocumentViewFragment fragment = new DocumentViewFragment();
        fragment.setArguments(args);
        fragment.setHasOptionsMenu(true);
        return fragment;
    }

    public static DocumentViewFragment newInstance(String fileName_, String path_, int mode) {

        Bundle args = new Bundle();
        args.putString("path", path_);
        args.putString("fileName", fileName_);
        args.putInt("mode", MODE_OPEN);
        DocumentViewFragment fragment = new DocumentViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_documentview, container, false);
        this.container = container;

        setHasOptionsMenu(true);


        Bundle bundle = this.getArguments();
        mode = bundle.getInt("mode");

        setControls();

        if (mode == MODE_DOWNLOAD) {
            link_pdf = bundle.getString("link");
            downloadTask = new DownloadPDF();
            downloadTask.execute(link_pdf);
        } else {
            pdf_path = bundle.getString("path");
            fileName = bundle.getString("fileName");
            openPDF(pdf_path, fileName);

        }





        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                relative_y = relative_nav.getY();
                rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

//        pdfView.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                return false;5
//            }
//        });


        return rootView;
    }



    @Override
    public void onDoubleTap(float x, float y) {

        getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        if(isNavHide)
            showNavigationalButtons();
        else
            hideNavigationalButtons();

    }

    @Override
    public void onClick(View v) {

        getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

        if(MainActivity.isMenuOpen)
            return;


        if (v == button_nextpage) {

            if(pdfView.getPageCount() == 1)
                return;

            if(button_nextpage.getAlpha() == 0)
                return;

            pdfView.jumpTo(++page);
            tv_page.setText((pdfView.getCurrentPage() + 1) + "/" + pdfView.getPageCount());
            pdfView.zoomTo(pdfView.getZoomMinimum());
            pdfView.loadPages();
            if (page == (pdfView.getPageCount()))
                button_nextpage.animate().alpha(0.0f).setDuration(400);

            button_prevpage.animate().alpha(1.0f).setDuration(400);


        } else if (v == button_prevpage) {

            if(pdfView.getPageCount() == 1)
                return;

            if(button_prevpage.getAlpha() == 0)
                return;

            pdfView.jumpTo(--page);
            tv_page.setText((pdfView.getCurrentPage() + 1) + "/" + pdfView.getPageCount());
            pdfView.zoomTo(pdfView.getZoomMinimum());
            pdfView.loadPages();
            if (page == 1)
                button_prevpage.animate().alpha(0.0f).setDuration(400);


            button_nextpage.animate().alpha(1.0f).setDuration(400);
        }else if(v == tv_page){
            new MaterialDialog.Builder(getActivity())
                    .title(R.string.title_choose_page)
                    .items(pages)
                    .itemsCallback(new MaterialDialog.ListCallback() {
                        @Override
                        public void onSelection(MaterialDialog dialog, View itemView, int which, CharSequence text) {

                            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE
                                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

                            int pageselected = Integer.parseInt(pages.get(which).substring(5));

                            if(pageselected == 1){
                                button_prevpage.animate().alpha(0.0f).setDuration(400);
                                button_nextpage.animate().alpha(1.0f).setDuration(400);
                            }else if(pageselected == pdfView.getPageCount()){
                                button_nextpage.animate().alpha(0.0f).setDuration(400);
                                button_prevpage.animate().alpha(1.0f).setDuration(400);
                            }else{
                                button_nextpage.animate().alpha(1.0f).setDuration(400);
                                button_prevpage.animate().alpha(1.0f).setDuration(400);
                            }


                            pdfView.jumpTo(pageselected);
                            pdfView.zoomTo(pdfView.getZoomMinimum());
                            pdfView.loadPages();
                            page = pageselected;
                            tv_page.setText((pdfView.getCurrentPage() + 1) + "/" + pdfView.getPageCount());

                            dialog.dismiss();
                        }
                    })
                    .dividerColor(Color.parseColor("#005a7c"))
                    .negativeText(R.string.close)
                    .show();
        }


    }

    private void setControls() {
        pdfView = (PDFView) rootView.findViewById(R.id.pdfview);

        button_nextpage = (Button) rootView.findViewById(R.id.documentview_nextpage);
        button_prevpage = (Button) rootView.findViewById(R.id.documentview_prevpage);
        tv_page = (TextViewBold) rootView.findViewById(R.id.documentview_tvpage);
        relative_nav = (RelativeLayout) rootView.findViewById(R.id.documentview_relativeNav);

        button_nextpage.setOnClickListener(this);
        button_prevpage.setOnClickListener(this);
        tv_page.setOnClickListener(this);

        pdfView.setDoubleTapListener(this);


    }

    private void hideNavigationalButtons(){
        isNavHide = true;
        relative_nav.animate().alpha(0.0f).y(relative_y + 300).setDuration(500);
    }

    private void showNavigationalButtons(){
        isNavHide = false;
        relative_nav.animate().alpha(1.0f).y(relative_y).setDuration(500);
    }

//    private void hideButtons() {
//        isNavHide = true;
//        button_nextpage.animate().alpha(0.0f).x(nextButton_X + 300).rotationY(180).setDuration(500);
//        button_prevpage.animate().alpha(0.0f).x(-300).rotationY(-180).setDuration(500);
//        tv_page.animate().alpha(0.0f).y(-300).rotationX(180).setDuration(500);
//    }
//
//    private void showButtons() {
//        isNavHide = false;
//
//        if (page != (pdfView.getPageCount()))
//            button_nextpage.animate().alpha(1.0f).x(nextButton_X).rotationY(0).setDuration(500);
//
//        if (page != 1)
//            button_prevpage.animate().alpha(1.0f).x(prevButton_X).rotationY(0).setDuration(500);
//
//        tv_page.animate().alpha(1.0f).y(tvpage_Y).rotationX(0).setDuration(500);
//    }


//    private void hidePageTV(){
////        getActivity().runOnUiThread(new Runnable() {
////
////            @Override
////            public void run() {
////                final Handler handler = new Handler();
////                handler.postDelayed(new Runnable() {
////                    @Override
////                    public void run() {
////                        //add your code here
////                        tv_page.animate().alpha(0.0f).y(-300).rotationX(180).setDuration(400);
////                    }
////                }, 1800);
////
////            }
////        });
//
//        if(timerClose != null)
//            timerClose.cancel();
//
//        timerClose = new CountDownTimer(1200,1000) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//
//            }
//
//            @Override
//            public void onFinish() {
//                tv_page.animate().alpha(0.0f).y(-300).rotationX(180).setDuration(400);
//            }
//        }.start();
//    }
//
//    private void showPageTV(){
//        tv_page.animate().alpha(1.0f).y(tvpage_Y).rotationX(0).setDuration(150);
//    }


    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.actiondocview_showhide){

            getActivity().getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);

            if(isNavHide)
                showNavigationalButtons();
            else
                hideNavigationalButtons();


            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_docview, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(false);
//        setHasOptionsMenu(true);
//    }

    private void openPDF(String path_, String fileName_) {
        File file = new File(path_,fileName_);
        if (file.exists() && file.canRead())
            pdfView.fromFile(file).enableSwipe(false).showMinimap(false).onLoad(DocumentViewFragment.this).load();
        else
            Toast.makeText(getActivity(),"Dosya Yok",Toast.LENGTH_SHORT).show();
    }


    private class DownloadPDF extends AsyncTask<String, Integer, Boolean> {

        private AlertDialog dialog;
        private View progressView;
        TextViewBold tv_loading;
        Button cancel_button;
        CircleProgressView circleProgressView;


        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
//            dialog.setContentText(getString(R.string.loading_document) + " : %" + values[0]);
            Log.d("LOADING", getString(R.string.loading_document) + " : " + values[0]);
            tv_loading.setText(getString(R.string.loading_document) + " : %" + values[0]);
//            circleProgressView.setValueAnimated((float) values[0]);
            circleProgressView.stopSpinning();
            circleProgressView.setValue((float) values[0]);
        }

        @Override
        protected void onPostExecute(Boolean succ) {
            super.onPostExecute(succ);

            dialog.dismiss();

            if (!succ) {
                UtilitiesKC.showErrorDialog(getString(R.string.oopss), getString(R.string.error_text_dialog), getString(R.string.ok_button), getActivity());
                return;
            }


            ContextWrapper cw = new ContextWrapper(getActivity());
            pdfView.fromFile(new File(cw.getFilesDir().getPath(), fileName)).enableSwipe(false).showMinimap(false).onLoad(DocumentViewFragment.this).load();
//            pdfView.enableDoubletap(false);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

//            dialog = UtilitiesKC.showProgressDialog(getString(R.string.loading_pleasewait), getActivity());

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
                    downloadTask.cancel(true);
                    dialog.dismiss();
                    getActivity().getSupportFragmentManager().popBackStack();
                    UtilitiesKC.showWarningToast("Loading is canceled.", getActivity());
                }
            });


            circleProgressView.setTextTypeface(Typeface.createFromAsset(getActivity().getAssets(), Config.ubuntuBold));
            circleProgressView.setTextMode(TextMode.PERCENT);
            circleProgressView.spin();

            dialog.show();

//            dialog.getWindow().setLayout((int)(320*getResources().getDisplayMetrics().density),(int)(240*getResources().getDisplayMetrics().density));


//            PowerManager pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
//            PowerManager.WakeLock mWakeLock;  mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
//                    getClass().getName());
//            mWakeLock.acquire();
        }

        @Override
        protected Boolean doInBackground(String... params) {

            int count;
            try {
                URL url = new URL(params[0]);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("HEAD");
                connection.connect();

                int lenghtOfFile = connection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(), 8192);
                fileName = params[0].substring(params[0].lastIndexOf('/') + 1);
                ContextWrapper cw = new ContextWrapper(getActivity());
                // Output stream
                OutputStream output = new FileOutputStream(new File(cw.getFilesDir().getPath(), fileName));

                byte data[] = new byte[1024];

                long total = 0;

                while ((count = input.read(data)) != -1) {
                    total += count;
                    // publishing the progress....
                    // After this onProgressUpdate will be called
                    publishProgress((int) ((total * 100) / lenghtOfFile));

                    // writing data to file
                    output.write(data, 0, count);
                }

                // flushing output
                output.flush();

                // closing streams
                output.close();
                input.close();
                return true;
            } catch (Exception e) {
                return false;
            }


//            return Download(link_pdf);

        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            dialog.dismiss();

//            UtilitiesKC.showErrorDialog(getString(R.string.oopss), getString(R.string.error_text_dialog), getString(R.string.ok_button), getActivity());
            return;

        }
    }

    private void checkTempPage(){
        int pageTemp = getActivity().getSharedPreferences("pref_doc",Context.MODE_PRIVATE).getInt(fileName,-1);
        if(pageTemp != -1 && pageTemp != 1){

            this.pdfView.jumpTo(pageTemp);
            pdfView.zoomTo(pdfView.getZoomMinimum());
            pdfView.loadPages();
            this.page = pageTemp;
            this.tv_page.setText((pdfView.getCurrentPage() + 1) + "/" + pdfView.getPageCount());

            OnClickWrapper onClickWrapper = new OnClickWrapper("supercardtoast", new SuperToast.OnClickListener() {

                @Override
                public void onClick(View view, Parcelable token) {

                    /** On click event */
                    pdfView.jumpTo(1);
                    pdfView.zoomTo(pdfView.getZoomMinimum());
                    pdfView.loadPages();
                    tv_page.setText((pdfView.getCurrentPage() + 1) + "/" + pdfView.getPageCount());
                    page = 1;
                    button_prevpage.animate().alpha(0.0f).setDuration(400);

                }

            });

            if(pageTemp != 1){
                button_prevpage.animate().alpha(1.0f).setDuration(400);
            }
            if(pageTemp == pdfView.getPageCount()){
                button_nextpage.animate().alpha(0.0f).setDuration(400);
            }

            UtilitiesKC.showInfoToastForDocumentTempPage(getString(R.string.document_page_temp),getActivity(),onClickWrapper);
        }
    }

    @Override
    public void loadComplete(int nbPages) {

        for (int i = 0; i < pdfView.getPageCount(); i++) {
            pages.add("Page "+String.valueOf(i+1));
        }

        float pageWidth = pdfView.getOptimalPageWidth();
        float viewWidth = pdfView.getWidth();
        minimumZoom = viewWidth / pageWidth;
        pdfView.zoomTo(minimumZoom);
        pdfView.setZoomMinimum(minimumZoom);
        pdfView.loadPages();
        tv_page.setText((pdfView.getCurrentPage() + 1) + "/" + pdfView.getPageCount());
        button_prevpage.animate().alpha(0.0f).setDuration(200);

        if(firstOpening) {
            checkTempPage();
            firstOpening = false;
        }


//        UtilitiesKC.showInfoToast(getString(R.string.doubletap_information), getActivity());


    }

}
