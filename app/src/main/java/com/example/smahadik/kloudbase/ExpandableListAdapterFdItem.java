package com.example.smahadik.kloudbase;

/**
 * Created by smahadik on 6/5/18.
 */

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.StringSignature;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.LinkedHashMap;
import java.util.List;

//package info.androidhive.expandablelistview;


public class ExpandableListAdapterFdItem extends BaseExpandableListAdapter {

    private Context _context;
    private List<String> _listDataHeader; // header titles
    // child data in format of header title, child title
    private LinkedHashMap<String, List<String>> _listDataChildLable;
    private LinkedHashMap<String, List<String>> _listDataChildValue;
    LayoutInflater infalInflater;
    StorageReference storageRefLogo;
    FirebaseFirestore firestoreRef;

    public ExpandableListAdapterFdItem(Context context, List<String> listDataHeader, LinkedHashMap<String, List<String>> listChildDataLable, LinkedHashMap<String, List<String>> listChildDataValue) {
        this._context = context;
        this._listDataHeader = listDataHeader;
        this._listDataChildLable = listChildDataLable;
        this._listDataChildValue = listChildDataValue;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        String [] string = new String[2];
        string[0] = _listDataChildLable.get(this._listDataHeader.get(groupPosition)).get(childPosition);
        string[1] = _listDataChildValue.get(this._listDataHeader.get(groupPosition)).get(childPosition);
        return string;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        Log.i("Child" , String.valueOf(groupPosition));
        final String[] childText = (String[]) getChild(groupPosition, childPosition);

        if (convertView == null) {
            infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        if (childPosition == 0) {
            if (infalInflater != null) {
                convertView = infalInflater.inflate(R.layout.list_item_with_image,parent, false);
                ImageView fditemImage = convertView.findViewById(R.id.fdItem);
                final ProgressBar progressBar = convertView.findViewById(R.id.progressBar);


                String pic = VenHome.foodItemArr.get(FdItemDetails.position).get(FdItemDetails.previousGroup).get("pic").toString();
                storageRefLogo = VenHome.storageRef.child(pic);
                Glide.with(fditemImage.getContext()).using(new FirebaseImageLoader()).load(storageRefLogo)
                        .signature(new StringSignature(String.valueOf(System.currentTimeMillis())))
                        .listener(new RequestListener<StorageReference, GlideDrawable>() {
                            @Override
                            public boolean onException(Exception e, StorageReference model, Target<GlideDrawable> target, boolean isFirstResource) {
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(GlideDrawable resource, StorageReference model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
                                progressBar.setVisibility(View.GONE);
                                return false;
                            }
                        })
                        .into(fditemImage);
            }
        }else if(isLastChild){
            if (infalInflater != null) {
                convertView = infalInflater.inflate(R.layout.list_item_with_button,parent, false);
            }
        } else {
            if (infalInflater != null) {
                convertView = infalInflater.inflate(R.layout.list_item, parent, false);
                TextView txtListChildLable = (TextView) convertView.findViewById(R.id.lblListItemLable);
                txtListChildLable.setText(childText[0]);
                TextView txtListChildValue = (TextView) convertView.findViewById(R.id.lblListItemValue);
                txtListChildValue.setText(childText[1]);
            }
        }


        return convertView;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return this._listDataChildLable.get(this._listDataHeader.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this._listDataHeader.get(groupPosition);
    }

    @Override
    public int getGroupCount() {
        return this._listDataHeader.size();
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        String headerTitle = (String) getGroup(groupPosition);

        if (convertView == null) {
            infalInflater = (LayoutInflater) this._context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            if (infalInflater != null) {
                convertView = infalInflater.inflate(R.layout.list_group, parent, false);
            }
        }

        TextView lblListHeader = (TextView) convertView.findViewById(R.id.lblListHeader);
//        lblListHeader.setTypeface(null, Typeface.BOLD);
        lblListHeader.setText(headerTitle);


        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
