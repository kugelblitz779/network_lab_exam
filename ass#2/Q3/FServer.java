@author Soumik_Laik
@author Shibashis_Banerjee

import java.net.*;
import java.io.*;
import java.util.*;

//SOULTEAM

public class FServer {
    public static void main(String[] args) {

        DatagramSocket serverSocket = null;
        FileInputStream fis = null;
        DatagramPacket receivedPacket, sendPacket;
        byte[] receivedData, sendData;
        String reply;
        InetAddress ip;
        int port;

        try {
            serverSocket = new DatagramSocket(Integer.parseInt(args[0]));
            serverSocket.setSoTimeout(3000);
            System.out.println("Server is up....");
            receivedData = new byte[512];
            sendData = new byte[512];


            receivedPacket = new DatagramPacket(receivedData,receivedData.length);
            serverSocket.receive(receivedPacket);

            port = receivedPacket.getPort();
            ip = receivedPacket.getAddress();
            reply = new String(receivedPacket.getData());

            System.out.println("CLIENT Says= " + reply);
            String filename = reply.substring(8, reply.length()).trim();
            //System.out.println(filename);

            int consignment;
            String strConsignment;
            int result = 0; // number of bytes read
            int count = 0;

            fis = new FileInputStream(filename);

            while(true && result!=-1){
                receivedData = new byte[512];
                sendData = new byte[512];

                //getting data from file and sending to client
                result = fis.read(sendData);

                if(result == -1) {
                    sendData = new String("RDT " + count + " END \r\n").getBytes();
                }else{
                    sendData = new String("RDT " + count + " " + new String(sendData) + " \r\n").getBytes();
                }
                
                while(true){
                    // flag while loop not needed

                    sendPacket = new DatagramPacket(sendData,sendData.length,ip,port);
                    serverSocket.send(sendPacket);

                    //getting acknowledgement from client

                    try{
                        receivedPacket = new DatagramPacket(receivedData,receivedData.length);
                        serverSocket.receive(receivedPacket);
                        break;
                    }catch(Exception e){ //socket time out exception
                        System.out.println(e.toString());
                        System.out.println("Timeout as acknowledgement not received and resending.....");
                    }
                }

                String ackMsg = new String(receivedPacket.getData());
                System.out.println(ackMsg);
                //

                receivedPacket = null;
                sendPacket = null;
                count++;
            }


        } catch (IOException ex) {
            System.out.println(ex.getMessage());

        } finally {
            try {
                if (fis != null)
                    fis.close();
            } catch (IOException ex) {
                System.out.println(ex.getMessage());
            }
        }
    }
}
