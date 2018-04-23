package com.tdr.yunwei.adapter;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;


import com.tdr.yunwei.R;
import com.tdr.yunwei.util.ActivityUtil;
import com.tdr.yunwei.util.LOG;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Administrator on 2017/3/17.
 */

public class PhotoListAdapter extends RecyclerView.Adapter<PhotoListAdapter.MyViewHolder> {


    private List<Bitmap> DrawableList;
    private Activity mActivity;
    private OnItemClickLitener mOnItemClickLitener;
    private final int number = 3;

    /**
     * 订制点击事件
     */
    public interface OnItemClickLitener {
        void onItemClick(String tage, int position);

        void onItemClearClick(View view, int position);
    }

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener) {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    public int getPhotoNum(){
        return DrawableList.size();
    }

    /**
     * @param activity
     */
    public PhotoListAdapter(Activity activity) {
        mActivity = activity;
        if (DrawableList == null) {
            DrawableList = new ArrayList<Bitmap>();
        }
    }

    @Override
    public void onViewAttachedToWindow(MyViewHolder holder) {
        super.onViewAttachedToWindow(holder);
    }

    public void UpDate(List<Bitmap> drawableList) {
        DrawableList = drawableList;
        notifyDataSetChanged();
        LOG.E("UpDate");
    }

    @Override
    public int getItemCount() {
        if (DrawableList.size() == number) {
            return number;
        } else {
            return DrawableList.size() + 1;
        }
    }


    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mActivity).inflate(R.layout.item_photo, null));
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
    @Override
    public void onBindViewHolder(final MyViewHolder holder, int position) {
        if (position == DrawableList.size()) {
            holder.clear.setVisibility(View.GONE);
            holder.photo.setBackgroundResource(R.mipmap.add_photo);
            holder.photo.setTag("Add");
            holder.name.setVisibility(View.VISIBLE);
            if (position == number) {
                holder.itemView.setVisibility(View.GONE);
            }
        } else {
            holder.clear.setVisibility(View.VISIBLE);
            holder.name.setVisibility(View.GONE);
            Bitmap db = DrawableList.get(position);
            Drawable drawable = new BitmapDrawable(db);
            if (db != null) {
                LOG.E("---"+position);
                holder.photo.setBackground(drawable);
//                holder.photo.setImageBitmap(db);
                holder.photo.setTag("Photo");
            }
        }

        if (mOnItemClickLitener != null) {
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();
                    mOnItemClickLitener.onItemClick((String)holder.photo.getTag(), pos);
                }
            });

            holder.clear.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = holder.getLayoutPosition();

                    mOnItemClickLitener.onItemClearClick(holder.clear, pos);
                }
            });
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public ImageView photo;
        public ImageView clear;
        public TextView name;
        public MyViewHolder(View view) {
            super(view);
            view.setLayoutParams(new RelativeLayout.LayoutParams(ActivityUtil.dp2px(110), ActivityUtil.dp2px(100)));
            photo = (ImageView) view.findViewById(R.id.iv_photo);
            clear = (ImageView) view.findViewById(R.id.iv_clear);
            name = (TextView) view.findViewById(R.id.tv_name);

        }

    }



}
