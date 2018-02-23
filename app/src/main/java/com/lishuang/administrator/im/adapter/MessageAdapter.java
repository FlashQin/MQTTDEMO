package com.lishuang.administrator.im.adapter;

import android.text.Html;
import android.text.Spanned;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.lishuang.administrator.im.R;
import com.lishuang.administrator.im.model.ChatMessage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Created by Administrator on 2015/8/31.
 */
public class MessageAdapter extends BaseAdapter {
    public final static int SEND_LEFT = 0;
    public final static int SEND_RIGHT = 1;
    public final static int SEND_CENER = 2;
    private LayoutInflater mInflater;
    private List<ChatMessage> mData;
    private Html.ImageGetter mImageGetter;
    private SimpleDateFormat format;
    private int which;

    @Override
    public int getViewTypeCount() {
        return 3;
    }

    @Override
    public int getItemViewType(int position) {

        if (0 == mData.get(position).getType()) {
            which=0;
            return SEND_LEFT;//消息类型在左边
        } else if (1 == mData.get(position).getType()) {
            which=1;
            return SEND_RIGHT;//消息类型在右边
        } else if (2 == mData.get(position).getType()) {
            which=2;
            return SEND_CENER;//消息类型在右边
        }else {
            which=0;
            return 0;
        }
    }

    public MessageAdapter(LayoutInflater mInflater, List<ChatMessage> mData, Html.ImageGetter mImageGetter) {
        this.mInflater = mInflater;
        this.mData = mData;
        this.mImageGetter = mImageGetter;
    }

    @Override
    public int getCount() {
        return mData.size();
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
        if (convertView == null) {
            viewHolder = new ViewHolder();
            /*
            通过判断消息类型的不同，加载不同的布局。这里使用到了getItemViewType()方法。
             */
            switch (getItemViewType(position)) {
                case SEND_LEFT:
                    convertView = mInflater.inflate(R.layout.listview_lyaout_left, null);
                    break;
                case SEND_RIGHT:
                    convertView = mInflater.inflate(R.layout.listview_layout_right, null);
                    break;
                case SEND_CENER:
                    convertView = mInflater.inflate(R.layout.listview_layout_center, null);
                    break;
                default:
                    break;
            }
            viewHolder.imageViewPerson = (ImageView) convertView.findViewById(R.id.imageview_person);
            viewHolder.textViewTime = (TextView) convertView.findViewById(R.id.textview_time);
            viewHolder.leixing = (TextView) convertView.findViewById(R.id.txtleixing);
            viewHolder.textViewHonour = (TextView) convertView.findViewById(R.id.textview_honor);
            viewHolder.textViewName = (ImageView) convertView.findViewById(R.id.textview_name);
            viewHolder.textViewInput = (TextView) convertView.findViewById(R.id.textview_input);
            viewHolder.name = (TextView) convertView.findViewById(R.id.txt_name_user_meassge);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        ChatMessage data = mData.get(position);
        //如果不是第一个Item且发送消息的两次时间在1min之内，则不再显示时间；否则显示时间
        if (position != 0) {
            ChatMessage dataBefore = mData.get(position - 1);
            long dateDifference = data.getTextViewTime() - dataBefore.getTextViewTime();
            if (dateDifference < 60000) {
               //viewHolder.textViewTime.setVisibility(View.GONE);
            } else {
                format = new SimpleDateFormat("EEE HH:mm:ss");
                String time = format.format(new Date(data.getTextViewTime()));
                viewHolder.textViewTime.setText(time);
            }
        } else {
            format = new SimpleDateFormat("EEE HH:mm:ss");
            String time = format.format(new Date(data.getTextViewTime()));
            viewHolder.textViewTime.setText(time);
        }

        //viewHolder.imageViewPerson.setImageResource(data.getImageViewPerson());
        switch (which) {
            case 0:
                viewHolder.textViewHonour.setText(data.getqishu());
                viewHolder.leixing.setText(data.getTypee());
                //将受到的数据以富文本的形式显示。
                Spanned spanned = Html.fromHtml(data.getMoney(), mImageGetter, null);
                viewHolder.textViewInput.setText(spanned);
                break;
            case 1:
                viewHolder.textViewHonour.setText(data.getqishu());
                viewHolder.leixing.setText(data.getTypee());
                //将受到的数据以富文本的形式显示。
                Spanned spannedd = Html.fromHtml(data.getMoney(), mImageGetter, null);
                viewHolder.textViewInput.setText(spannedd);
                break;
            case 2:
                viewHolder.name.setText(data.getname());
                break;
            default:
                break;
        }


        return convertView;
    }
    class ViewHolder {
        ImageView imageViewPerson;
        TextView textViewHonour;
        ImageView textViewName;
        TextView textViewTime;
        TextView textViewInput;
        TextView leixing;
        TextView name;
    }
}
