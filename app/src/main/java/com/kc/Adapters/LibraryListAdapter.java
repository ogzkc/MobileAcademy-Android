package com.kc.Adapters;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.Wsdl2Code.WebServices.MAService.Document;
import com.kc.Util.Config;
import com.kc.mobileacademy.LibraryFragment;
import com.kc.mobileacademy.R;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;

/**
 * Created by Oguz on 21.02.2016.
 */
public class LibraryListAdapter extends ArrayAdapter<Document>{

    Context mContext;
    Typeface ubuntuMed ,ubuntuLight;
    ArrayList<Document> orginalDocuments;
    LibraryFragment libraryFragment;

    DisplayImageOptions options;
    ImageLoaderConfiguration config;

    public LibraryListAdapter(Context context, int resource, ArrayList<Document> objects, LibraryFragment fragment) {
        super(context, resource, objects);
        this.mContext = context;
        this.orginalDocuments = objects;
        ubuntuMed = Typeface.createFromAsset(mContext.getAssets(), Config.ubuntuMed);
        ubuntuLight = Typeface.createFromAsset(mContext.getAssets(),Config.ubuntuLight);
        this.libraryFragment = fragment;

        config = new ImageLoaderConfiguration.Builder(context)
                .diskCacheFileCount(300)
                .build();
        ImageLoader.getInstance().init(config);
        options = new DisplayImageOptions.Builder()
                .cacheInMemory(true)
                .cacheOnDisk(true)
                .considerExifParams(true)
                .bitmapConfig(Bitmap.Config.RGB_565)
                .build();
    }



    private class ViewHolder {
        TextView tv_DocName;
        TextView tv_PublisherName;
        ImageView iv_Department;
        Button bt_download;
    }


    @Override
    public int getCount() {
        return orginalDocuments.size();
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        Document rowItem = getItem(position);
        LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.listitem_library, parent, false);
            holder = new ViewHolder();
            holder.tv_DocName = (TextView) convertView.findViewById(R.id.listItemLibrary_tvDocName);
            holder.iv_Department = (ImageView) convertView.findViewById(R.id.listItemLibrary_ivLogo);
            holder.tv_PublisherName = (TextView) convertView.findViewById(R.id.listItemLibrary_tvPublisher);

            holder.bt_download = (Button) convertView.findViewById(R.id.listItemLibrary_buttonDownloadOpen);

            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();


        holder.tv_DocName.setText(rowItem.displayName);
        holder.tv_PublisherName.setText(rowItem.publisherName);
//        holder.iv_Department.setImageResource(Config.departmentLogoRes.get(rowItem.departmentID));

        ImageLoader.getInstance().displayImage(rowItem.departmentLogo,holder.iv_Department,options);


//        if(rowItem.isDownloaded)
//            holder.bt_download.setBackgroundResource(R.drawable.download_button_background);
//        else
//            holder.bt_download.setBackgroundResource(R.drawable.tick_button_background);

        holder.bt_download.setOnClickListener(this.libraryFragment);

        holder.bt_download.setId(rowItem.docId);

        holder.tv_DocName.setTypeface(ubuntuMed);
        holder.tv_PublisherName.setTypeface(ubuntuMed);




        return convertView;
    }
}
