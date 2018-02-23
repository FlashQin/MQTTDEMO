# MQTTDEMO
这是一篇关于Android 端使用MQTT协议实现用户之间的单聊和群聊功能的浅谈，大家可以借鉴下，我已经在我的项目中实现了

MQTT的原理我们就不用说了吧，相信大家都百度过了，下面我们来具体看看怎么实现：

这是连接MQTT服务器，获取推送的消息，然后将获取到的消息，根据我们自己的需求自己处理
//这是开始连接的方法
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
            //host=tcp://103.47.83.6:1883为主机名，test为clientid即连接MQTT的客户端ID，一般以客户端唯一标识符表示，MemoryPersistence设置             clientid的保存形式，默认为以内存保存，需要说明的是host一定 要填对，不然无法连接，端口是1883，地址是你自己的
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
                    //subscribe后得到的消息会执行到这里面，这里处理接收到的消息
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
    //这是重连的方法
    private void connect() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                //连接成功
                    client.connect(options);
                    Message msg = new Message();
                    msg.what = 2;
                    hanlser.sendMessage(msg);
                } catch (Exception e) { //连接失败
                    e.printStackTrace();
                    Message msg = new Message();
                    msg.what = 3;
                    hanlser.sendMessage(msg);
                }
            }
        }).start();
    }

大家记得动动你的小手，给我点颗星啊，给点动力吧，谢谢了
