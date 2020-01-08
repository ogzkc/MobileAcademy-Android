package com.kc.mobileacademy;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.Wsdl2Code.WebServices.MAService.Comment;
import com.Wsdl2Code.WebServices.MAService.MAService;
import com.Wsdl2Code.WebServices.MAService.ResponseMA;
import com.Wsdl2Code.WebServices.MAService.VectorComment;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.kc.Adapters.CommentListAdapter;
import com.kc.Util.Config;
import com.kc.Util.UtilitiesKC;
import com.labo.kaji.swipeawaydialog.support.v4.SwipeAwayDialogFragment;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

import hani.momanii.supernova_emoji_library.Actions.EmojIconActions;
import hani.momanii.supernova_emoji_library.Helper.EmojiconEditText;


/**
 * Created by Oguz on 15.05.2016.
 */
public class CommentDialogFragment extends SwipeAwayDialogFragment implements View.OnClickListener,SwipeRefreshLayout.OnRefreshListener {

    private ListView listView;
    private LinearLayout linearLayout;
    private EmojiconEditText editText;
    private ImageView emojiButton;
    private ImageView sendButton;
    CommentListAdapter adapter;
    View rootView;
    ArrayList<Comment> comments = new ArrayList<>();
    int docId;
    EmojIconActions emojIcon;
    ViewGroup container_group;
    DisplayImageOptions options;
    ImageLoaderConfiguration config;
    Boolean isSending = false;
    ProgressBar progressBar;
//    SwipeRefreshLayout swipeRefreshLayout;

    public CommentDialogFragment() {

    }

    public static CommentDialogFragment newInstance(int docId) {
        CommentDialogFragment frag = new CommentDialogFragment();
        Bundle args = new Bundle();
        args.putInt("docId", docId);
        frag.setArguments(args);
        return frag;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
//        rootView = inflater.inflate(R.layout.fragment_comment,container);
//
//        return rootView;
        this.container_group = container;
        return inflater.inflate(R.layout.fragment_comment, container);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        this.docId = getArguments().getInt("docId", 0);
        setControls(view);
        getDialog().setTitle("Comments");
        new getComments().execute();

    }

    private void setControls(View view) {


        editText = (EmojiconEditText) view.findViewById(R.id.commentfragment_emojiconedittext);
//        listView = (ListView) view.findViewById(R.id.commentfragment_listview);
        linearLayout = (LinearLayout) view.findViewById(R.id.commentfragment_linear);
//        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_linearcomment);
        sendButton = (ImageView) view.findViewById(R.id.commentfragment_send);
        emojiButton = (ImageView) view.findViewById(R.id.commentfragment_emoji);
        progressBar = (ProgressBar) view.findViewById(R.id.commentfragment_progressbar);
        sendButton.setOnClickListener(this);
        emojIcon = new EmojIconActions(getActivity(), view, editText, emojiButton);
        emojIcon.setUseSystemEmoji(false);
        emojIcon.ShowEmojIcon();
//        swipeRefreshLayout.setOnRefreshListener(this);
//        swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright,
//         android.R.color.holo_green_light, android.R.color.holo_orange_light,
//         android.R.color.holo_red_dark);



    }

    private class getComments extends AsyncTask<Void, Void, Boolean> {
        VectorComment cmntResp = null;

        @Override
        protected void onPostExecute(Boolean succ) {
            super.onPostExecute(succ);

            progressBar.setVisibility(View.GONE);
//            swipeRefreshLayout.setRefreshing(false);

            if (!succ) {
                UtilitiesKC.showErrorDialog(getString(R.string.oopss), getString(R.string.comments_error), getString(R.string.ok_button), getActivity());
                return;
            }

            if (cmntResp == null) {
                UtilitiesKC.showErrorDialog(getString(R.string.oopss), getString(R.string.comments_error), getString(R.string.ok_button), getActivity());
                return;
            }
            comments.clear();
            for (int i = 0; i < cmntResp.size(); i++) {
                comments.add(cmntResp.elementAt(i));
            }

            prepareLinearStore();

//            adapter = new CommentListAdapter(getActivity(),R.layout.listitem_comment,comments);
//            listView.setAdapter(adapter);


        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressBar.setVisibility(View.VISIBLE);
//            swipeRefreshLayout.setRefreshing(true);
        }

        @Override
        protected Boolean doInBackground(Void... params) {

            try {
                MAService service = new MAService();
                cmntResp = service.getComments( docId);
                return true;
            } catch (Exception ex) {
                return false;
            }

        }
    }

    private class sendComment extends AsyncTask<Void,Void,Boolean>{
        String commentText = "-";
        ResponseMA responseMA = new ResponseMA();
        public sendComment(String commentText_){
            this.commentText = commentText_;
        }
        @Override
        protected void onPostExecute(Boolean succ) {
            super.onPostExecute(succ);

            isSending = false;

            if (!succ) {
                linearLayout.removeViewAt(linearLayout.getChildCount()-1);
                linearLayout.removeViewAt(linearLayout.getChildCount()-1);
                UtilitiesKC.showErrorClassicToast(getString(R.string.comment_cannot_send),getActivity());
                return;
            }

            if (responseMA == null) {
                linearLayout.removeViewAt(linearLayout.getChildCount()-1);
                linearLayout.removeViewAt(linearLayout.getChildCount()-1);
                UtilitiesKC.showErrorClassicToast(getString(R.string.comment_cannot_send),getActivity());
                return;
            }

            if(!responseMA.success){
                linearLayout.removeViewAt(linearLayout.getChildCount()-1);
                linearLayout.removeViewAt(linearLayout.getChildCount()-1);
                UtilitiesKC.showErrorClassicToast(getString(R.string.comment_cannot_send),getActivity());
                return;
            }

            if(responseMA.status.statusCode.equals("901")){
                editText.setText("");
                linearLayout.removeAllViews();
                new getComments().execute();
                return;
            }

            if(responseMA.status.statusCode.equals("902")){

                linearLayout.removeViewAt(linearLayout.getChildCount()-1);
                linearLayout.removeViewAt(linearLayout.getChildCount()-1);

                UtilitiesKC.showErrorClassicToast(getString(R.string.comment_cannot_send_2minutes),getActivity());
                return;
            }



        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            isSending = true;

            LayoutInflater layoutInflater = getActivity().getLayoutInflater();
            final RelativeLayout progressRelative = (RelativeLayout) layoutInflater.inflate(R.layout.listitem_progress, container_group, false);
            linearLayout.addView(progressRelative);
            View v = new View(getActivity());
            v.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 2));
            v.setBackgroundColor(Color.parseColor("#005a7c"));
            linearLayout.addView(v);

        }

        @Override
        protected Boolean doInBackground(Void... params) {
            try {
                MAService service = new MAService();
                responseMA = service.makeComment( Config.user.getUserId(),docId,commentText);
                return true;
            }catch (Exception ex){
                return false;
            }
        }
    }

    private void prepareLinearStore() {

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        ViewHolder holder;
        config = new ImageLoaderConfiguration.Builder(getActivity())
                .build();
        ImageLoader.getInstance().init(config);
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(false)
                .cacheOnDisk(false)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        for (int i = 0; i < comments.size(); i++) {
            Comment rowItem = comments.get(i);

                       final RelativeLayout comRelative = (RelativeLayout) layoutInflater.inflate(R.layout.listitem_comment, container_group, false);

            holder = new ViewHolder();
            holder.tv_Name = (TextView) comRelative.findViewById(R.id.commentitem_tvName);
            holder.tv_Comment = (TextView) comRelative.findViewById(R.id.commentitem_tvComment);
            holder.iv_photo = (ImageView) comRelative.findViewById(R.id.commentitem_imageview);

            holder.tv_Name.setText(rowItem.name);
            holder.tv_Comment.setText(rowItem.text);

            String[] arr = rowItem.name.split(" ");
            String isim = "";
            for (int a = 0; a < arr.length; a++) {
                isim += arr[a].charAt(0) + " ";
            }
            ColorGenerator generator = ColorGenerator.MATERIAL;
            int color1 = generator.getRandomColor();
            final TextDrawable drawable = TextDrawable.builder()
                    .beginConfig()
                    .withBorder(4)
                    .endConfig()
                    .buildRoundRect(isim, color1, 10);
            holder.iv_photo.setImageDrawable(drawable);

            if (!rowItem.photoLink.equals("-")) {
                ImageLoader.getInstance().displayImage(rowItem.photoLink, holder.iv_photo, options, new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String s, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String s, View view, FailReason failReason) {
                        ((ImageView)view).setImageDrawable(drawable);
                    }

                    @Override
                    public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                        Bitmap roundedBmp = UtilitiesKC.getRoundedBitmap(bitmap);
                        ((ImageView)view).setImageBitmap(roundedBmp);
                    }

                    @Override
                    public void onLoadingCancelled(String s, View view) {

                    }
                });
            }

            View v = new View(getActivity());
            v.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, 2));
            v.setBackgroundColor(Color.parseColor("#005a7c"));


            this.linearLayout.addView(comRelative);
            this.linearLayout.addView(v);
        }

        this.linearLayout.setScrollBarStyle(View.SCROLLBARS_INSIDE_INSET);
    }

    @Override
    public void onClick(View v) {

        if(editText.getText().toString().isEmpty()){
            return;
        }

        if(isSending){
            return;
        }

        if(editText.getText().toString().length() < 3){
            UtilitiesKC.showWarningClassicToast(getString(R.string.comment_cannot_be_short),getActivity());
            return;
        }

        new sendComment(editText.getText().toString()).execute();




    }

    @Override
    public void onRefresh() {
        linearLayout.removeAllViews();
        new getComments().execute();
    }

    private class ViewHolder {
        TextView tv_Name;
        TextView tv_Comment;
        ImageView iv_photo;
    }
}
