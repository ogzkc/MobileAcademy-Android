package com.kc.Adapters;

import android.app.Activity;
import android.content.Context;
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
import com.kc.WidgetKC.ButtonBoldType;
import com.kc.mobileacademy.R;
import com.kc.mobileacademy.StoreFragment;

import java.util.ArrayList;

/**
 * Created by Oguz on 21.02.2016.
 */
public class StoreListAdapter extends ArrayAdapter<Document>{

    Context mContext;
    Typeface ubuntuMed ,ubuntuLight;
    ArrayList<Document> orginalDocuments;
    StoreFragment storeFragment;

    public StoreListAdapter(Context context, int resource, ArrayList<Document> objects,StoreFragment fragment) {
        super(context, resource, objects);
        this.mContext = context;
        this.orginalDocuments = objects;
        ubuntuMed = Typeface.createFromAsset(mContext.getAssets(), Config.ubuntuMed);
        ubuntuLight = Typeface.createFromAsset(mContext.getAssets(),Config.ubuntuLight);
        this.storeFragment = fragment;
    }



    private class ViewHolder {
        TextView tv_DocName;
        TextView tv_PublisherName;
        ImageView iv_Department;
        TextView tv_Price;
        Button bt_addtolibrary;
        ButtonBoldType bt_preview;
    }

//    @Override
//    public boolean areAllItemsEnabled() {
//        return false;
//    }
//
//    @Override
//    public boolean isEnabled(int position) {
//        return false;
//    }

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
            convertView = mInflater.inflate(R.layout.listitem_store, parent, false);
            holder = new ViewHolder();
            holder.tv_DocName = (TextView) convertView.findViewById(R.id.listitemstore_tvDocName);
            holder.iv_Department = (ImageView) convertView.findViewById(R.id.listitemstore_ivLogo);
            holder.tv_PublisherName = (TextView) convertView.findViewById(R.id.listitemstore_tvPublisher);
            holder.tv_Price = (TextView) convertView.findViewById(R.id.listitemstore_tvPrice);
            holder.bt_addtolibrary = (Button) convertView.findViewById(R.id.listitemstore_buttonAddToLibrary);
            holder.bt_preview = (ButtonBoldType) convertView.findViewById(R.id.listitemstore_buttonPreviewLook);

            convertView.setTag(holder);
        } else
            holder = (ViewHolder) convertView.getTag();


        holder.tv_DocName.setText(rowItem.displayName);
        holder.tv_PublisherName.setText(rowItem.publisherName);
        holder.iv_Department.setImageResource(Config.departmentLogoRes.get(rowItem.departmentID));

        if(rowItem.price == 0)
            holder.tv_Price.setText("Free");
        else
            holder.tv_Price.setText(rowItem.price+" Coins");

        if(rowItem.isHave)
            holder.bt_addtolibrary.setBackgroundResource(R.drawable.bookread_button_background);
        else
            holder.bt_addtolibrary.setBackgroundResource(R.drawable.download_button_background);

        holder.bt_addtolibrary.setOnClickListener(this.storeFragment);
        holder.bt_preview.setOnClickListener(this.storeFragment);

        holder.bt_addtolibrary.setId(rowItem.docId);
        holder.bt_preview.setId(rowItem.docId + 100);
        holder.bt_preview.setTag(rowItem);

        holder.tv_DocName.setTypeface(ubuntuMed);
        holder.tv_PublisherName.setTypeface(ubuntuMed);
        holder.tv_Price.setTypeface(ubuntuLight);




        return convertView;
    }
}
