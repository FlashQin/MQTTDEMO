# MQTTDEMO
这是一篇关于Android 端使用MQTT协议实现用户之间的单聊和群聊功能的浅谈，大家可以借鉴下，我已经在我的项目中实现了

MQTT的原理我们就不用说了吧，相信大家都百度过了，下面我们来具体看看怎么实现：<br>

![闪电侠](https://github.com/FlashQin/MySendGift/blob/master/device-2018-02-23-173343.mp4)

这是连接MQTT服务器，获取推送的消息，然后将获取到的消息，根据我们自己的需求自己处理<br>
//这是开始连接的方法<br>
   private void startReconnect() {<br>
        scheduler = Executors.newSingleThreadScheduledExecutor();<br>
        scheduler.scheduleAtFixedRate(new Runnable() {<br>

            @Override<br>
            public void run() {<br>
                if (!client.isConnected()) {<br>
                    connect();<br>
                }<br>
            }<br>
        }, 0 * 1000, 10 * 1000, TimeUnit.MILLISECONDS);<br>
    }<br>
 private void getMessage() {<br>

        try {<br>
            //host=tcp://103.47.83.6:1883为主机名，test为clientid即连接MQTT的客户端ID，一般以客户端唯一标识符表示，MemoryPersistence设置             clientid的保存形式，默认为以内存保存，需要说明的是host一定 要填对，不然无法连接，端口是1883，地址是你自己的<br>
            client = new MqttClient(host, "testasd",<br>
                    new MemoryPersistence());<br>
            //MQTT的连接设置<br>
            options = new MqttConnectOptions();<br>
            //设置是否清空session,这里如果设置为false表示服务器会保留客户端的连接记录，这里设置为true表示每次连接到服务器都以新的身份连接<br>
            options.setCleanSession(false);<br>
            //设置连接的用户名<br>
            options.setUserName(userName);<br>
            //设置连接的密码<br>
            options.setPassword(passWord.toCharArray());<br>
            // 设置超时时间 单位为秒<br>
            options.setConnectionTimeout(30);<br>
            // 设置会话心跳时间 单位为秒 服务器会每隔1.5*20秒的时间向客户端发送个消息判断客户端是否在线，但这个方法并没有重连的机制<br>
            options.setKeepAliveInterval(20);<br>
            //设置回调<br>
            client.setCallback(new MqttCallback() {<br>

                @Override<br>
                public void connectionLost(Throwable cause) {<br>
                    //连接丢失后，一般在这里面进行重连<br>
                    connect();<br>
                    System.out.println("connectionLost----------");<br>
                }<br>

                @Override<br>
                public void deliveryComplete(IMqttDeliveryToken token) {<br>
                    //publish后会执行到这里<br>
                    System.out.println("deliveryComplete---------"<br>
                            + token.isComplete());<br>
                }<br>

                @Override<br>
                public void messageArrived(String topicName, MqttMessage message)<br>
                        throws Exception {<br>
                    //subscribe后得到的消息会执行到这里面，这里处理接收到的消息<br>
                    // System.out.println("messageArrived----------");<br>
                    Message msg = new Message();<br>
                    msg.obj = message;<br>
                    msg.what = 1;<br>
                    hanlser.sendMessage(msg);<br>

                }<br>
            });<br>

        } catch (Exception e) {<br>
            e.printStackTrace();<br>
        }<br>
    }<br>
    //这是重连的方法<br>
    private void connect() {<br>
        new Thread(new Runnable() {<br>

            @Override<br>
            public void run() {<br>
                try {<br>
                //连接成功<br>
                    client.connect(options);<br>
                    Message msg = new Message();<br>
                    msg.what = 2;<br>
                    hanlser.sendMessage(msg);<br>
                } catch (Exception e) { //连接失败<br>
                    e.printStackTrace();<br>
                    Message msg = new Message();<br>
                    msg.what = 3;<br>
                    hanlser.sendMessage(msg);<br>
                }<br>
            }<br>
        }).start();<br>
    }<br>

大家记得动动你的小手，给我点颗星啊，给点动力吧，谢谢了<br>
