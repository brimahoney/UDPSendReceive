package network;

//import java.io.*;
//import java.net.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
 
public class QuoteClient 
{
    private boolean stopped = true;
    private DatagramSocket socket;
    private Thread clientThread;
    
    private Runnable getClientRunnable() 
    {
        Runnable runner = new Runnable()
        {
            public void run()
            {
                while(!stopped)
                {
                    try 
                    {
                        // send request
                        byte[] buf = new byte[256];
                        InetAddress address = InetAddress.getByName("localhost");
                        DatagramPacket packet = new DatagramPacket(buf, buf.length, address, 4445);
                        socket.send(packet);
                        System.out.println("Request sent...");
                        
                        // get response
                        packet = new DatagramPacket(buf, buf.length);
                        socket.receive(packet);
                        
                        // display response
                        String received = new String(packet.getData(), 0, packet.getLength());
                        System.out.println("Response: " + received);
                        
                        try
                        {
                            Thread.sleep(1000);
                        }
                        catch (InterruptedException ex)
                        {
                            Logger.getLogger(QuoteClient.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    catch (UnknownHostException ex) 
                    {
                        Logger.getLogger(QuoteClient.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(QuoteClient.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                socket.close();
                System.out.println("Client stopped");
            }
        };     
        return runner;
    }
    
    public void stopClient() throws IllegalStateException
    {
        if(stopped)
            throw new IllegalStateException("Client is already stopped");
        
        stopped = true;
        socket.close();
    }
    
    public void startClient() throws SocketException, IllegalStateException
    {
        if(!stopped)
            throw new IllegalStateException("Client is already running");
        
        stopped = false;
        socket = new DatagramSocket(4445);
        
        System.out.println("Starting client...");
        clientThread = new Thread(getClientRunnable());
        clientThread.setDaemon(true);
        clientThread.start();
    }
    
    public HeartBeatMessage getObjectFromPacket(DatagramPacket packet)
    {
        //recieve object from packet
        byte[] data = packet.getData();
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        HeartBeatMessage message = null;
        
        try 
        {
            ObjectInputStream ois = new ObjectInputStream(bais);
            message = (HeartBeatMessage) ois.readObject();
        }
        catch (IOException ex) 
        {
            System.out.println("Exception while creating object input stream: " + ex.getMessage());
        } 
        catch (ClassNotFoundException ex) 
        {
            System.out.println("Exception while reading object from stream: " + ex.getMessage());
        }
        return message;
    }
}
