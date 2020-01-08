package com.kc.mobileacademy;


import android.animation.Animator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.Wsdl2Code.WebServices.MAService.Document;
import com.Wsdl2Code.WebServices.MAService.MAService;
import com.Wsdl2Code.WebServices.MAService.ResponseMA;
import com.github.johnpersano.supertoasts.SuperToast;
import com.joanzapata.pdfview.PDFView;
import com.joanzapata.pdfview.listener.OnLoadCompleteListener;
import com.kc.Util.Config;
import com.kc.Util.UtilitiesKC;
import com.kc.WidgetKC.TextViewBold;
import com.kc.WidgetKC.TextViewMed;
import com.liulishuo.filedownloader.BaseDownloadTask;
import com.liulishuo.filedownloader.FileDownloadListener;
import com.liulishuo.filedownloader.FileDownloader;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;


/**
 * A simple {@link Fragment} subclass.
 */
public class DocumentDetailFragment extends Fragment implements View.OnClickListener,OnLoadCompleteListener {


    public DocumentDetailFragment() {
        // Required empty public constructor
    }

    View rootView;
    private RelativeLayout relativeDocumentdetail;
    private TextViewMed TvDocName;
    private TextViewBold TvPublisherName;
    private PDFView documentviewPreviewPDF;
    private TextViewMed Descriptiontv;
    private Button ButtonAddToLibrary;
    private Button ButtonTableOfContent;
    private Button ButtonLike;
    private Button ButtonComment;
    private Button ButtonDislike;
    private TextViewMed TvPoint;

    BaseDownloadTask baseDownloadTask;
    BaseDownloadTask baseDownloadTaskPreview;

    Button nextPage,prevPage;
    float prevButton_X;
    float nextButton_X;
    Boolean isButtonsHide = false;
    int page = 1;


    private String link_pdf;
    String fileName = null;
    private DownloadPDF downloadTask = null;
    ViewGroup container;
    Document document;

    private AlertDialog dialog;
    private View progressView;
    TextViewBold tv_loading;
    Button cancel_button;
    CircleProgressView circleProgressView;


    public static DocumentDetailFragment newInstance(Document document) {

        Bundle args = new Bundle();
        DocumentDetailFragment fragment = new DocumentDetailFragment();
        fragment.document = document;
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_documentdetail, container, false);

        this.container = container;
        findViews();
        setDetails();
//        downloadTask = new DownloadPDF();
//        downloadTask.execute(this.document.previewLink);



        prepareDialog();
        fileName = document.previewLink.substring(document.previewLink.lastIndexOf('/') + 1);
        final ContextWrapper cw = new ContextWrapper(getActivity());
        baseDownloadTaskPreview = new FileDownloader().create(this.document.previewLink)
                .setPath(cw.getFilesDir().getPath()+"/"+fileName)
                .setListener(new FileDownloadListener() {
            @Override
            protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {

            }

            @Override
            protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
                if(totalBytes == 0) {
                    tv_loading.setText("Loading");
                    return;
                }

                tv_loading.setText(getString(R.string.loading_document) + " : %" + 100 * soFarBytes / totalBytes);
                circleProgressView.stopSpinning();
                circleProgressView.setValue( 100 * soFarBytes / totalBytes);

            }

            @Override
            protected void blockComplete(BaseDownloadTask task) {

            }

            @Override
            protected void completed(BaseDownloadTask task) {

                dialog.dismiss();
                fileName = document.previewLink.substring(document.previewLink.lastIndexOf('/') + 1);
                documentviewPreviewPDF.fromFile(new File(cw.getFilesDir().getPath(), fileName)).enableSwipe(true).onLoad(DocumentDetailFragment.this).load();
            }

            @Override
            protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {

            }

            @Override
            protected void error(BaseDownloadTask task, Throwable e) {
                dialog.dismiss();

                    UtilitiesKC.showErrorDialog(getString(R.string.oopss), getString(R.string.error_text_dialog), getString(R.string.ok_button), getActivity());
            }

            @Override
            protected void warn(BaseDownloadTask task) {

            }
        });
        baseDownloadTaskPreview.start();


        rootView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                prevButton_X = prevPage.getX();
                nextButton_X = nextPage.getX();
                rootView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });


        return rootView;
    }




    private void findViews() {
        relativeDocumentdetail = (RelativeLayout) rootView.findViewById(R.id.relative_documentdetail);
        TvDocName = (TextViewMed) rootView.findViewById(R.id.documentdetail_tvDocName);
        TvPublisherName = (TextViewBold) rootView.findViewById(R.id.documentdetail_tvPublisherName);
        documentviewPreviewPDF = (PDFView) rootView.findViewById(R.id.documentview_previewPDF);
        Descriptiontv = (TextViewMed) rootView.findViewById(R.id.documentdetail_descriptiontv);
        ButtonAddToLibrary = (Button) rootView.findViewById(R.id.documentdetail_buttonAddToLibrary);
        ButtonTableOfContent = (Button) rootView.findViewById(R.id.documentdetail_buttonTableOfContent);
        ButtonLike = (Button) rootView.findViewById(R.id.documentdetail_button_like);
        ButtonDislike = (Button) rootView.findViewById(R.id.documentdetail_button_dislike);
        TvPoint = (TextViewMed) rootView.findViewById(R.id.documentdetail_tv_point);
        ButtonComment = (Button) rootView.findViewById(R.id.documentdetail_buttonComment);

        prevPage = (Button) rootView.findViewById(R.id.documentdetail_prevpage);
        nextPage = (Button) rootView.findViewById(R.id.documentdetail_nextpage);

        ButtonComment.setOnClickListener(this);
        nextPage.setOnClickListener(this);
        prevPage.setOnClickListener(this);
        ButtonAddToLibrary.setOnClickListener(this);
        ButtonTableOfContent.setOnClickListener(this);
        ButtonLike.setOnClickListener(this);
        ButtonDislike.setOnClickListener(this);

    }

    private void setDetails() {
        this.TvDocName.setText(document.displayName);
        this.TvPublisherName.setText("by " + document.publisherName);
        this.Descriptiontv.setText(document.description);
        this.TvPoint.setText(document.point + " Points");
        if (document.myRate == 1)
            this.ButtonLike.setBackgroundResource(R.drawable.ic_like_check);
        else if (document.myRate == -1)
            this.ButtonDislike.setBackgroundResource(R.drawable.ic_dislike_check);

        if (document.isHave)
            ButtonAddToLibrary.setBackgroundResource(R.drawable.bookread_button_background);
        else
            ButtonAddToLibrary.setBackgroundResource(R.drawable.plus_button_background);

    }


    @Override
    public void onClick(View v) {
        if(MainActivity.isMenuOpen)
            return;
        if (v == ButtonAddToLibrary) {

            if (document.isHave) {
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

                    cancel_button.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            if(baseDownloadTask != null)
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

                   baseDownloadTask = FileDownloader.getImpl().create(document.pDFLink)
                            .setPath(UtilitiesKC.getPDFPath(getActivity(), document)+"/v"+document.version+"/"+ UtilitiesKC.getPDFName(document))
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

                return;
            }


            makeTick(ButtonAddToLibrary);
//            UtilitiesKC.showSuccessToast(getString(R.string.added_to_library_toast), getActivity());
            SuperToast superToast = new SuperToast(getActivity());
            superToast.setDuration(SuperToast.Duration.MEDIUM);
            superToast.setText(getString(R.string.added_to_library_toast));
            superToast.setIcon(R.drawable.ic_succes2, SuperToast.IconPosition.LEFT);
            superToast.setBackground(SuperToast.Background.GREEN);
            superToast.setAnimations(SuperToast.Animations.FLYIN);
            superToast.setTextSize(SuperToast.TextSize.MEDIUM);
            superToast.show();
            new AddToLibraryAsync(Config.user.getUserId(), document.docId, 1, document.price).execute();
            document.isHave = true;
            return;

        } else if (v == ButtonTableOfContent) {

            DocumentViewFragment fragment = DocumentViewFragment.newInstance(this.document.tableofContentLink,DocumentViewFragment.MODE_DOWNLOAD);
            getActivity().getSupportFragmentManager().beginTransaction().add(R.id.main_fragment, fragment, "fragment").setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).addToBackStack(fragment.getClass().getName()).commit();
            return;

        } else if (v == ButtonLike) {
            if (document.myRate == 1) {
                new RateDocumentAsync(1, 0).execute();
                makeLikeSelected(false);
                document.myRate = 0;
                document.point--;
            } else if (document.myRate == 0) {
                new RateDocumentAsync(0, 1).execute();
                makeLikeSelected(true);
                document.myRate = 1;
                document.point++;
            } else if (document.myRate == -1) {
                new RateDocumentAsync(-1, 1).execute();
                makeUnLikeSelected(false);
                makeLikeSelected(true);
                document.myRate = 1;
                document.point += 2;
            }

            TvPoint.setText(document.point + " Points");

        } else if (v == ButtonDislike) {

            if (document.myRate == 1) {
                new RateDocumentAsync(1, -1).execute();
                makeLikeSelected(false);
                makeUnLikeSelected(true);
                document.myRate = -1;
                document.point -= 2;
            } else if (document.myRate == 0) {
                new RateDocumentAsync(0, -1).execute();
                makeLikeSelected(true);
                document.myRate = -1;
                document.point--;
            } else if (document.myRate == -1) {
                new RateDocumentAsync(-1, 0).execute();
                makeUnLikeSelected(false);
                document.myRate = 0;
                document.point++;
            }
            TvPoint.setText(document.point + " Points");
        }else if (v == prevPage){

            if(page == 1)
                return;

            documentviewPreviewPDF.jumpTo(--page);
            documentviewPreviewPDF.zoomTo(documentviewPreviewPDF.getZoomMinimum());
            documentviewPreviewPDF.loadPages();
            if (page == 1)
                prevPage.animate().alpha(0.0f).setDuration(400);


            nextPage.animate().alpha(0.6f).setDuration(400);

        }else if(v == nextPage){

            if(page == documentviewPreviewPDF.getPageCount())
                return;

            documentviewPreviewPDF.jumpTo(++page);
            documentviewPreviewPDF.zoomTo(documentviewPreviewPDF.getZoomMinimum());
            documentviewPreviewPDF.loadPages();
            if (page == (documentviewPreviewPDF.getPageCount()))
                nextPage.animate().alpha(0.0f).setDuration(400);

            prevPage.animate().alpha(0.6f).setDuration(400);
        }else if(v == ButtonComment){
//            UtilitiesKC.showWarningDialog("Coming Soon", "Comment section is under construction, next version will be released.", "OK", getActivity());
            FragmentManager fm = getActivity().getSupportFragmentManager();
            CommentDialogFragment commentDialogFragment = CommentDialogFragment.newInstance(document.docId);
            commentDialogFragment.show(fm, "fragment_edit_name");
        }
    }

    private void prepareDialog(){
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

                dialog.dismiss();
//                UtilitiesKC.showWarningToast(getString(R.string.download_canceled), getActivity());
            }
        });


        circleProgressView.setTextTypeface(Typeface.createFromAsset(getActivity().getAssets(), Config.ubuntuBold));
        circleProgressView.setTextMode(TextMode.PERCENT);
        circleProgressView.spin();

        dialog.show();
    }


    private void makeTick(final Button bt) {
        bt.animate().alpha(0.0f).setDuration(500).setListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                bt.setBackgroundResource(R.drawable.bookread_button_background);
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

    private void makeLikeSelected(boolean selected) {
        if (selected) {
            ButtonLike.animate().alpha(0.0f).setDuration(350).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    ButtonLike.setBackgroundResource(R.drawable.ic_like_check);
                    ButtonLike.animate().alpha(1.0f).setDuration(350);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        } else {
            ButtonLike.animate().alpha(0.0f).setDuration(350).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    ButtonLike.setBackgroundResource(R.drawable.ic_like);
                    ButtonLike.animate().alpha(1.0f).setDuration(350);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
    }

    private void makeUnLikeSelected(boolean selected) {
        if (selected) {
            ButtonDislike.animate().alpha(0.0f).setDuration(350).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    ButtonDislike.setBackgroundResource(R.drawable.ic_dislike_check);
                    ButtonDislike.animate().alpha(1.0f).setDuration(350);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        } else {
            ButtonDislike.animate().alpha(0.0f).setDuration(350).setListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    ButtonDislike.setBackgroundResource(R.drawable.ic_dislike);
                    ButtonDislike.animate().alpha(1.0f).setDuration(350);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
        }
    }


    private class RateDocumentAsync extends AsyncTask<Void, Void, Boolean> {

        ResponseMA responseMA;
        int point, oldpoint;

        public RateDocumentAsync(int oldpoint_, int point_) {
            this.point = point_;
            this.oldpoint = oldpoint_;
        }


        @Override
        protected void onPostExecute(Boolean succ) {
            super.onPostExecute(succ);


            if (!succ) {
                UtilitiesKC.showErrorToast(getString(R.string.error_text_toast), getActivity());
                if (oldpoint == 1) {
                    makeUnLikeSelected(false);
                    makeLikeSelected(true);
                    return;
                }
                if (oldpoint == 0) {
                    makeUnLikeSelected(false);
                    makeLikeSelected(false);
                    return;
                }
                if (oldpoint == -1) {
                    makeUnLikeSelected(true);
                    makeLikeSelected(false);
                    return;
                }
            }


            if (responseMA == null) {
                UtilitiesKC.showErrorToast(getString(R.string.error_text_toast), getActivity());
                if (oldpoint == 1) {
                    makeUnLikeSelected(false);
                    makeLikeSelected(true);
                    return;
                }
                if (oldpoint == 0) {
                    makeUnLikeSelected(false);
                    makeLikeSelected(false);
                    return;
                }
                if (oldpoint == -1) {
                    makeUnLikeSelected(true);
                    makeLikeSelected(false);
                    return;
                }
                return;
            }

            if (responseMA.success) {

            } else {
                UtilitiesKC.showErrorToast(getString(R.string.error_text_toast), getActivity());
                if (oldpoint == 1) {
                    makeUnLikeSelected(false);
                    makeLikeSelected(true);
                    return;
                }
                if (oldpoint == 0) {
                    makeUnLikeSelected(false);
                    makeLikeSelected(false);
                    return;
                }
                if (oldpoint == -1) {
                    makeUnLikeSelected(true);
                    makeLikeSelected(false);
                    return;
                }
            }

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                MAService sv = new MAService();
                responseMA = sv.rateDocument( Config.user.getUserId(), document.docId, this.point, 1);
                return true;
            } catch (Exception e) {
                return false;
            }

        }
    }

    private class AddToLibraryAsync extends AsyncTask<Void, Void, Boolean> {

        int userid, docid, platid, pric;
        ResponseMA responseMA = null;

        public AddToLibraryAsync(int userId, int docId, int platformId, int price) {
            this.userid = userId;
            this.docid = docId;
            this.platid = platformId;
            this.pric = price;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Boolean succ) {
            super.onPostExecute(succ);

            if (!succ) {
                UtilitiesKC.showErrorToast(getString(R.string.error_text_toast), getActivity());
                ButtonAddToLibrary.setBackgroundResource(R.drawable.plus_button_background);
                document.isHave = false;
                return;
            }


            if (responseMA == null) {
                UtilitiesKC.showErrorToast(getString(R.string.error_text_toast), getActivity());
                ButtonAddToLibrary.setBackgroundResource(R.drawable.plus_button_background);
                document.isHave = false;
                return;
            }

            if (responseMA.success) {

            } else {
                ButtonAddToLibrary.setBackgroundResource(R.drawable.plus_button_background);
                UtilitiesKC.showErrorToast(getString(R.string.error_text_toast), getActivity());
                document.isHave = false;
            }

        }


        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                MAService sv = new MAService();
                responseMA = sv.addToLibrary( userid, docid, platid, pric);
                return true;
            } catch (Exception e) {
                responseMA = null;
                return false;

            }
        }


    }

    private class DownloadPDF extends AsyncTask<String, Integer, Boolean> {




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
            documentviewPreviewPDF.fromFile(new File(cw.getFilesDir().getPath(), fileName)).enableSwipe(true).onLoad(DocumentDetailFragment.this).load();

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();




//            dialog.getWindow().setLayout((int)(320*getResources().getDisplayMetrics().density),(int)(240*getResources().getDisplayMetrics().density));


//            PowerManager pm = (PowerManager) getActivity().getSystemService(Context.POWER_SERVICE);
//            PowerManager.WakeLock mWakeLock;  mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
//                    getClass().getName());
//            mWakeLock.acquire();
        }

        @Override
        protected Boolean doInBackground(String... params) {

//            ContextWrapper cw = new ContextWrapper(getActivity());
//            FileDownloader.getImpl().create(params[0])
//                    .setPath(cw.getFilesDir().getPath())
//                    .setListener(new FileDownloadListener() {
//                        @Override
//                        protected void pending(BaseDownloadTask task, int soFarBytes, int totalBytes) {
//
//                        }
//
//                        @Override
//                        protected void progress(BaseDownloadTask task, int soFarBytes, int totalBytes) {
//                            publishProgress((int)soFarBytes/totalBytes);
//                        }
//
//                        @Override
//                        protected void blockComplete(BaseDownloadTask task) {
//
//                        }
//
//                        @Override
//                        protected void completed(BaseDownloadTask task) {
//
//                        }
//
//                        @Override
//                        protected void paused(BaseDownloadTask task, int soFarBytes, int totalBytes) {
//
//                        }
//
//                        @Override
//                        protected void error(BaseDownloadTask task, Throwable e) {
//
//                        }
//
//                        @Override
//                        protected void warn(BaseDownloadTask task) {
//
//                        }
//                    }).start();
//
//            return true;


            int count;
            try {
                String urlStr = params[0];
                urlStr = urlStr.replace("%20"," ");
                URL url = new URL(urlStr);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("HEAD");
                connection.connect();

                int lenghtOfFile = connection.getContentLength();

                // download the file
                InputStream input = new BufferedInputStream(url.openStream(), lenghtOfFile);

                fileName = urlStr.substring(urlStr.lastIndexOf('/') + 1);
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
                Log.d("ERROR DOWNLOAD PREVIEW",e.getMessage());
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

    @Override
    public void loadComplete(int nbPages) {


        float pageWidth = documentviewPreviewPDF.getOptimalPageWidth();
        float viewWidth = documentviewPreviewPDF.getWidth();
        float minimumZoom = viewWidth / pageWidth;
        minimumZoom--;
        documentviewPreviewPDF.zoomTo(minimumZoom);
        documentviewPreviewPDF.setZoomMinimum(minimumZoom);
        documentviewPreviewPDF.loadPages();
        prevPage.animate().alpha(0.0f).setDuration(200);

        if(documentviewPreviewPDF.getPageCount() == 1){
            prevPage.animate().alpha(0.0f).setDuration(200);
            nextPage.animate().alpha(0.0f).setDuration(200);
        }



    }

}
