package com.lishuang.administrator.im.model;

import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

/**
 * Created by Administrator on 2015/8/31.
 */
public class ChatMessage {

    private int imageViewPerson;//人物头像
    private long textViewTime;//显示的时间
    private String Name;//其数
    private String qishu;//其数
    private String Type;//投注类型
    private String money;//数量
    private int type;//信息类型，是在左边显示还是右边显示。

    /*
    定义两个构造器，一个无参，一个传值。
     */
    public ChatMessage() {
    }

    public ChatMessage(int imageViewPerson, long textViewTime, String Qishu, String Typee, String nums, String mname) {
        this.imageViewPerson = imageViewPerson;
        this.textViewTime = textViewTime;
        this.qishu = Qishu;
        this.Type = Typee;
        this.money = nums;
        this.Name = mname;
    }

    public String getname() {
        return Name;
    }

    public void setname(String type) {
        this.Name = type;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getImageViewPerson() {
        return imageViewPerson;
    }

    public void setImageViewPerson(int imageViewPerson) {
        this.imageViewPerson = imageViewPerson;
    }

    public long getTextViewTime() {
        return textViewTime;
    }

    public void setTextViewTime(long textViewTime) {
        this.textViewTime = textViewTime;
    }

    public String getqishu() {
        return qishu;
    }

    public void setqishu(String textViewHonour) {
        this.qishu = textViewHonour;
    }

    public String getTypee() {
        return Type;
    }

    public void setTypee(String textviewName) {
        this.Type = textviewName;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String textViewInput) {
        this.money = textViewInput;
    }
}
