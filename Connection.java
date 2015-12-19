package client;

import java.net.*;
import java.io.*;

public class Connection
{
    final static String IP_SERVER = "127.0.0.1";
    final static int PORT_SERVER = 7666;
    Socket sock;
    DataOutputStream writeData;
    DataInputStream handleData;
    
    public void initialize()
    {
        try
        {
            sock = new Socket(IP_SERVER, PORT_SERVER);
            writeData = new DataOutputStream(sock.getOutputStream());
            writeData.writeByte(1);
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
}
