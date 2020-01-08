package com.kc.Adapters;

/**
 * Created by Oguz on 19.05.2016.
 */

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.Wsdl2Code.WebServices.MAService.Comment;
import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.kc.Util.UtilitiesKC;
import com.kc.mobileacademy.LibraryFragment;
import com.kc.mobileacademy.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.util.ArrayList;

/**
 * Created by Oguz on 21.02.2016.
 */
public class CommentListAdapter extends ArrayAdapter<Comment> {

    Context mContext;
    Typeface ubuntuMed ,ubuntuLight;
    ArrayList<Comment> orginalDocuments;
    LibraryFragment libraryFragment;
    ViewHolder holder = null;
    DisplayImageOptions options;
    ImageLoaderConfiguration config;

    public CommentListAdapter(Context context, int resource, ArrayList<Comment> objects) {
        super(context, resource, objects);

        this.mContext = context;
        this.orginalDocuments = objects;
        config = new ImageLoaderConfiguration.Builder(context)
                .build();
        ImageLoader.getInstance().init(config);
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(false)
                .cacheOnDisk(false)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }



    private class ViewHolder {
        TextView tv_Name;
        TextView tv_Comment;
        ImageView iv_photo;
    }


    @Override
    public int getCount() {
        return orginalDocuments.size();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        Comment rowItem = getItem(position);
        LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listitem_comment, parent, false);
            holder = new ViewHolder();
            holder.tv_Name = (TextView) convertView.findViewById(R.id.commentitem_tvName);
            holder.tv_Comment = (TextView) convertView.findViewById(R.id.commentitem_tvComment);
            holder.iv_photo = (ImageView) convertView.findViewById(R.id.commentitem_imageview);


            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();


        holder.tv_Name.setText(rowItem.name);
        holder.tv_Comment.setText(rowItem.text);

        String[] arr = rowItem.name.split(" ");
        String isim = "";
        for (int i = 0; i < arr.length; i++) {
            isim+=arr[i].charAt(0)+" ";
        }
        ColorGenerator generator = ColorGenerator.MATERIAL;
        int color1 = generator.getRandomColor();
        final TextDrawable drawable = TextDrawable.builder()
                .beginConfig()
                .withBorder(4)
                .endConfig()
                .buildRoundRect(isim, color1, 10);

        holder.iv_photo.setImageDrawable(drawable);

        if(rowItem.photoLink.equals("-")){


            return convertView;
        }


        ImageLoader.getInstance().displayImage(rowItem.photoLink,holder.iv_photo,options, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String s, View view) {

            }

            @Override
            public void onLoadingFailed(String s, View view, FailReason failReason) {
                holder.iv_photo.setImageDrawable(drawable);
            }

            @Override
            public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                Bitmap roundedBmp = UtilitiesKC.getRoundedBitmap(bitmap);
                holder.iv_photo.setImageBitmap(roundedBmp);
            }

            @Override
            public void onLoadingCancelled(String s, View view) {

            }
        });



        return convertView;
    }
}
