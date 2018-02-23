package com.lishuang.administrator.im.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.lishuang.administrator.im.R;

/**
 * Created by Administrator on 2015/9/1.
 */
public class ExpressionAdapter extends BaseAdapter {

    //表情数据
    private int[] mExpression = {R.mipmap.dra,R.mipmap.drb,R.mipmap.drc,R.mipmap.drd,R.mipmap.dre,R.mipmap.drf,R.mipmap.drg,
            R.mipmap.drh,R.mipmap.dri,R.mipmap.drj,R.mipmap.drk,R.mipmap.drl,R.mipmap.drm,R.mipmap.drn,
            R.mipmap.dro,R.mipmap.drp,R.mipmap.drq,R.mipmap.drr,R.mipmap.drs,R.mipmap.drt,R.mipmap.dru,
            R.mipmap.drv,R.mipmap.drw,R.mipmap.drx,R.mipmap.dry,R.mipmap.drz,R.mipmap.dra};

    private LayoutInflater mInflater;

    public ExpressionAdapter(LayoutInflater mInflater) {
        this.mInflater = mInflater;
    }

    @Override
    public int getCount() {
        return mExpression.length;
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup viewGroup) {

        ViewHolder viewHolder = null;
        if(convertView == null){
            convertView = mInflater.inflate(R.layout.item_expression, null);
            viewHolder = new ViewHolder();
            viewHolder.imageview = (ImageView) convertView.findViewById(R.id.imageview_expression);
            convertView.setTag(viewHolder);
        }else {
            viewHolder = (ViewHolder) convertView.getTag();

        }
        viewHolder.imageview.setImageResource(mExpression[position]);
        return convertView;
    }
    class ViewHolder{
        ImageView imageview;
    }
}
