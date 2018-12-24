package xdf.exam;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import net.sf.json.JSONObject;

/**
 * udp连接，用于动态ip, pos向255.255.255.255：5060发送请求即可
 * 
 * 需求：应用A（通常有多个）和应用B（1个）进行 socket通讯，应用A必须知道应用B的ip地址
 * （在应用A的配置文件中写死的），这个时候就必须把应用B的ip设成固定ip（但是某些时候如更
 * 换路由后要重新设置网络，但是操作人员不知道这个规则），就有可能造成应用A和应用B无法进行
 * 正常通讯，所以要改成应用A动态获取应用B的ip地址。

*经过讨论决定采用udp协议实现，upd是一种无连接的传输层协议。应用A在不知道应用B的 ip情况
*下 可以使用广播地址255.255.255.255，将消息发送到在同一广播网络上的B。从而获取B的ip
 * **/
public class UdpServer extends Thread implements Runnable {
	Logger logger = Logger.getLogger("lavasoft");
    private final int MAX_LENGTH = 1024;
    private final int PORT = 5060;
    private DatagramSocket datagramSocket;
    private Map<String,Object> CLIENT_INFO = new HashMap<String,Object>();
    
    public void run() {
        try {
            init();
            while(true){
                try {
                    byte[] buffer = new byte[MAX_LENGTH];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    receive(packet);
                    String receStr = new String(packet.getData(), 0 , packet.getLength());
                    System.out.println("接收数据包" + receStr);
                    
                    byte[] bt = new byte[packet.getLength()];
                    
                    System.arraycopy("true", 0, bt, 0, packet.getLength());
                    System.out.println(packet.getAddress().getHostAddress() + "：" + packet.getPort());
                    
                    // 保存客户端ip及相关信息
                    Map<String,String> client = new HashMap<String,String>();
                    client.put("ip",packet.getAddress().getHostAddress());
                    client.put("port",""+packet.getPort());
                    JSONObject json = JSONObject.fromObject(receStr);
                    client.putAll((Map<String,String>)json);  
                    CLIENT_INFO.put(client.get("name"),client);
                    
                    // 回应客户端
                    packet.setData(bt);
                    response(packet);
                    
                } catch (Exception e) {
                    e.printStackTrace();
                    logger.info("udp线程出现异常：" + e.toString());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void receive(DatagramPacket packet) throws Exception {
        datagramSocket.receive(packet);
    }

    public void response(DatagramPacket packet) throws Exception {
        datagramSocket.send(packet);
    }
    
//    public void acceptReq()
    /**
     * 初始化连接
     */
    public void init(){
        try {
            datagramSocket = new DatagramSocket(PORT);
            System.out.println("udp服务端已经启动！");
        } catch (Exception e) {
            datagramSocket = null;
            System.out.println("udp服务端启动失败！");
            e.printStackTrace();
        }
    }
}