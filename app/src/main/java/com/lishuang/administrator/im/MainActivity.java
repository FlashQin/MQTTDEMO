package com.lishuang.administrator.im;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.Toast;

import com.lishuang.administrator.im.adapter.ExpressionAdapter;
import com.lishuang.administrator.im.adapter.MessageAdapter;
import com.lishuang.administrator.im.model.ChatMessage;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainActivity extends Activity implements View.OnClickListener {

    private String host = "tcp://103.47.83.6:1883";
    private String userName = "admin";
    private String passWord = "admin";
    private MqttClient client;
    private String myTopic = "", caizhong = "";
    private MqttConnectOptions options;
    private ScheduledExecutorService scheduler;

    private Button mButtonLeft;//左边发送按钮
    private Button mButtonRight;//右边发送按钮
    private EditText mEditTextInput;//输入框
    private ListView mListView;//显示消息的ListView
    private GridView mGridView;//显示表情的GridView
//    private PopupWindow mPopupWindow;
//    private LinearLayout mLinearLayout;
//    private LayoutInflater mInflater;

    private ImageButton mImageButtonExpression;//弹出和收回表情框的按钮
    private Spanned mSpanned;//富文本
    private Html.ImageGetter mImageGetter;//获得富文本图片
    private List<ChatMessage> mData;//消息数据
    private MessageAdapter mMessageAdapter;//消息适配器
    private ExpressionAdapter mExpressionAdapter;//表情适配器
    private InputMethodManager mInputMethodManager;//用于控制手机键盘的显示有否的对象（此处）
    //表情数据名称
    private String[] mExpression = {"dra", "drb", "drc", "drd", "dre", "drf",
            "drg", "drh", "dri", "drg", "drk", "drl",
            "drm", "drn", "dro", "drp", "drq", "drr",
            "drs", "drt", "dru", "drv", "drw", "drx",
            "dry", "drz", "dra"};
    private Handler hanlser = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {

            }
            }};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        mInflater = getLayoutInflater();
        mButtonLeft = (Button) findViewById(R.id.button_left);
        mButtonRight = (Button) findViewById(R.id.button_right);
        mEditTextInput = (EditText) findViewById(R.id.edittext_input);
        mImageButtonExpression = (ImageButton) findViewById(R.id.imagebutton_expression);
        mListView = (ListView) findViewById(R.id.listview);
//        mLinearLayout = (LinearLayout) findViewById(R.id.linearlayout);
//        mPopupWindow = new PopupWindow(MainActivity.this);
//        mPopupWindow.setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
//        mPopupWindow.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
//        View popupView = mInflater.inflate(R.layout.popupwindow, null);
//        mPopupWindow.setContentView(popupView);
//        mPopupWindow.setFocusable(true);
//        mPopupWindow.setOutsideTouchable(true);
//        mGridView = (GridView) popupView.findViewById(R.id.gridview);

        mGridView = (GridView) findViewById(R.id.gridview);
        mButtonLeft.setOnClickListener(this);
        mButtonRight.setOnClickListener(this);
        mImageButtonExpression.setOnClickListener(this);
        mInputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        //消息数据初始化
        mData = new ArrayList<ChatMessage>();
        //通过反射获得图片的id
        mImageGetter = new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(String s) {
                Drawable drawable = null;
                int id = R.mipmap.dra;
                if (s != null) {
                    Class clazz = R.mipmap.class;
                    try {
                        Field field = clazz.getDeclaredField(s);
                        id = field.getInt(s);

                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }

                }
                drawable = getResources().getDrawable(id);
                drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                return drawable;
            }
        };
        mExpressionAdapter = new ExpressionAdapter(getLayoutInflater());
        mGridView.setAdapter(mExpressionAdapter);
        //点击表情，将表情添加到输入框中。
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                //通过mImageGetter获得id获得表情图片，然后将其添加到输入框中。
                mSpanned = Html.fromHtml("<img src='" + mExpression[position] + "'/>", mImageGetter, null);
                mEditTextInput.getText().insert(mEditTextInput.getSelectionStart(), mSpanned);

            }
        });
        //点击输入框收回表情框
        mEditTextInput.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mGridView.getVisibility() == View.VISIBLE) {
                    mGridView.setVisibility(View.GONE);
                }
            }
        });
        //showListViewRight();

        getMessage();//获取服务器传过来的MQTT消息
        startReconnect();//连接服务器

    }
    private void startReconnect() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(new Runnable() {

            @Override
            public void run() {
                if (!client.isConnected()) {
                    connect();
                }
            }
        }, 0 * 1000, 10 * 1000, TimeUnit.MILLISECONDS);
    }

    private void getMessage() {

        try {
            //host为主机名，test为clientid即连接MQTT的客户端ID，一般以客户端唯一标识符表示，MemoryPersistence设置clientid的保存形式，默认为以内存保存
            client = new MqttClient(host, "testasd",
                    new MemoryPersistence());
            //MQTT的连接设置
            options = new MqttConnectOptions();
            //设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接
            options.setCleanSession(false);
            //设置连接的用户名
            options.setUserName(userName);
            //设置连接的密码
            options.setPassword(passWord.toCharArray());
            // 设置超时时间 单位为秒
            options.setConnectionTimeout(30);
            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制
            options.setKeepAliveInterval(20);
            //设置回调
            client.setCallback(new MqttCallback() {

                @Override
                public void connectionLost(Throwable cause) {
                    //连接丢失后，一般在这里面进行重连
                    connect();
                    System.out.println("connectionLost----------");
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    //publish后会执行到这里
                    System.out.println("deliveryComplete---------"
                            + token.isComplete());
                }

                @Override
                public void messageArrived(String topicName, MqttMessage message)
                        throws Exception {
                    //subscribe后得到的消息会执行到这里面
                    // System.out.println("messageArrived----------");
                    Message msg = new Message();
                    msg.obj = message;
                    msg.what = 1;
                    hanlser.sendMessage(msg);

                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void connect() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    client.connect(options);
                    Message msg = new Message();
                    msg.what = 2;
                    hanlser.sendMessage(msg);
                } catch (Exception e) {
                    e.printStackTrace();
                    Message msg = new Message();
                    msg.what = 3;
                    hanlser.sendMessage(msg);
                }
            }
        }).start();
    }

    @Override
    public void onClick(View view) {
        mMessageAdapter = new MessageAdapter(getLayoutInflater(), mData, mImageGetter);
        switch (view.getId()) {

            case R.id.button_left://左边的发送按钮
               showListViewcener();
                break;
            case R.id.button_right://右边的发送按钮
                showListViewRight();
                break;
            default:
                break;

        }

    }

    /*
    发送左边消息
     */
    private void showListViewRight() {
        ChatMessage dataRight = new ChatMessage();
        dataRight.setTextViewTime(System.currentTimeMillis());
        dataRight.setqishu("234444567期");
        dataRight.setTypee("13");
        /*
        判断发送的消息是否为空，如果为空则弹出提示不允许发送
        */
        if (filterHtml(Html.toHtml(mEditTextInput.getText())).equals("")) {
            Toast.makeText(getApplicationContext(), "发送的消息不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        dataRight.setMoney("100");
        dataRight.setType(MessageAdapter.SEND_RIGHT);
        mMessageAdapter.notifyDataSetChanged();
        mData.add(dataRight);
        mListView.setAdapter(mMessageAdapter);
        mListView.setSelection(mData.size() - 1);
        mEditTextInput.setText("");
    }

    /*
    发送右边消息
     */
    private void showListViewLeft() {
        ChatMessage dataLeft = new ChatMessage();
        dataLeft.setTextViewTime(System.currentTimeMillis());
        dataLeft.setqishu("营长");
        dataLeft.setTypee("12");
        /*
        判断发送的消息是否为空，如果为空则弹出提示不允许发送
        */
        if (filterHtml(Html.toHtml(mEditTextInput.getText())).equals("")) {
            Toast.makeText(getApplicationContext(), "发送的消息不能为空！", Toast.LENGTH_SHORT).show();
            return;
        }
        //将解析的数据添加到输入框中。
        // dataLeft.setTextViewInput(filterHtml(Html.toHtml(mEditTextInput.getText())));
        dataLeft.setMoney("100");
        dataLeft.setType(MessageAdapter.SEND_LEFT);
        mMessageAdapter.notifyDataSetChanged();
        mData.add(dataLeft);
        mListView.setAdapter(mMessageAdapter);
        mListView.setSelection(mData.size() - 1);
        mEditTextInput.setText("");
    }

    private void showListViewcener() {
        ChatMessage dataLeft = new ChatMessage();
        dataLeft.setTextViewTime(System.currentTimeMillis());
        dataLeft.setqishu("营长");
        dataLeft.setTypee("12");
        dataLeft.setname("qqqqqqqqq");
        //将解析的数据添加到输入框中。
        // dataLeft.setTextViewInput(filterHtml(Html.toHtml(mEditTextInput.getText())));
        dataLeft.setMoney("100");
        dataLeft.setType(2);
        mMessageAdapter.notifyDataSetChanged();
        mData.add(dataLeft);
        mListView.setAdapter(mMessageAdapter);
        mListView.setSelection(mData.size() - 1);
        mEditTextInput.setText("");
    }

    public String filterHtml(String str) {
        str = str.replaceAll("<(?!br|img)[^>]+>", "").trim();
        return str;
    }
}
