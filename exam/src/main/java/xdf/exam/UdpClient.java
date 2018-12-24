package xdf.exam;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/***
 * UDP Client端
 ***/
public class UdpClient {
   
    private String userInfo ="{\"name\":\"JSON\",\"age\":\"24\",\"address\":\"北京市西城区\"}";
    private String netAddress = "255.255.255.255";
    private final int PORT = 5060;
    private Map<String,String> serverInfo = new HashMap<String,String>(); 
    
    private DatagramSocket datagramSocket;
    private DatagramPacket datagramPacket;
   
    public UdpClient(){
        try { 
            datagramSocket = new DatagramSocket();
            byte[] buf = userInfo.getBytes();
            InetAddress address = InetAddress.getByName(netAddress);
            datagramPacket = new DatagramPacket(buf, buf.length, address, PORT);
            datagramSocket.send(datagramPacket);
     
            
            // System.out.println(packet.getAddress().getHostAddress() + "：" + packet.getPort() + "：" + Arrays.toString(bt));
            
            byte[] receBuf = new byte[1024];
            DatagramPacket recePacket = new DatagramPacket(receBuf, receBuf.length);
            datagramSocket.receive(recePacket);
           
            String receStr = new String(recePacket.getData(), 0 , recePacket.getLength());
            // 获取服务端ip等信息
            InetAddress serverIp = recePacket.getAddress();
            serverInfo.put("ip",recePacket.getAddress().getHostAddress());
            serverInfo.put("port",""+recePacket.getPort());
            System.out.println(serverInfo+"   "+recePacket.getData());
            
        } catch (SocketException e) {
            e.printStackTrace();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭socket
            if(datagramSocket != null){
                datagramSocket.close();
            }
        }
    }
    
    public static void main(String[] args) {
        for (int i = 0; i < 100; i++) {
            new Thread(new Runnable() {
                public void run() {
                    UdpClient udpClient = new UdpClient();
                }
            }).start();
        }
    }
}