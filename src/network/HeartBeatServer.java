package network;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;

public class HeartBeatServer
{
    private boolean stopped = true;
    private volatile Instant lastReceivedTime;
    private final int heartBeatInterval = 10; // milliseconds
    
    // Receive
    private Thread receiveThread;
    private DatagramSocket receiveSocket = null;
    private final int receivePort = 49676;
        
    // Send
    private Thread sendThread;
    private DatagramSocket sendSocket = null;
    private InetAddress sendAddress = null;
    private int localSendPort = 58735;
    
    public HeartBeatServer()
    {
        sendAddress = InetAddress.getLoopbackAddress();
    }
    
    private Runnable getSendRunnable() 
    {
        Runnable runner = new Runnable()
        {
            @Override
            public void run()
            {
                String indents = "-----";
                long count = 0;
                while (!stopped) 
                {
                    try 
                    {
                        Thread.sleep(heartBeatInterval);
                        byte[] buf = new byte[1024];

                        // figure out response
                        HeartBeatMessage message = new HeartBeatMessage();
                        createPacketFromMessage(message);

                        // send the response to the client at "address" and "port"
                        DatagramPacket packet = createPacketFromMessage(message);
                        packet.setAddress(sendAddress);
                        packet.setPort(receivePort);
                        
                        sendSocket.send(packet);
                        print(indents, "HeartBeat message sent: " + ++count);
                    }
                    catch (IOException ex) 
                    {
                        if(stopped)
                            print(indents, "Send: IOException due to stopping server: " + ex.getMessage());
                        else
                            print(indents, "Send: IOException: " + ex.getMessage());
                    } 
                    catch (InterruptedException ex) 
                    {
                        print(indents, "Thread interrupted while sleeping before heartbeat: " + ex.getMessage());
                    }
                }
                sendSocket.close();
                print(indents, "Send server stopped");
            }
        };
        return runner;
    }
    
    private Runnable getReceiveRunnable() 
    {
        Runnable runner = new Runnable()
        {
            @Override
            public void run()
            {
                String indents = "*****";
                print(indents, "Accepting heartbeats...");
                while (!stopped) 
                {
                    try 
                    {
                        byte[] buf = new byte[1024];

                        // receive request
                        DatagramPacket packet = new DatagramPacket(buf, buf.length);
                        receiveSocket.receive(packet);
                        
                        HeartBeatMessage message = getObjectFromPacket(packet);
                        print(indents, "Heartbeat message received, payload: " + message.getPayload());
                                                                        
                        //Set time to now instead of using time from message
                        updateLastReceivedTime(message.getPayload());
                        //System.out.println("Heartbeat message received, new time: " + getLastReceivedTime());
                    }
                    catch (IOException ex) 
                    {
                        if(stopped)
                            print(indents, "Receive: IOException due to stopping receive server: " + ex.getMessage());
                        else
                            print(indents, "Receive: IOException: " + ex.getMessage());
                    }
                }
                receiveSocket.close();
                print(indents, "Receive server stopped");
            }
        };
        return runner;
    }
    
    public void stopSendServer() throws IllegalStateException
    {
        if(stopped)
            throw new IllegalStateException("Send server is already stopped");
        
        stopped = true;
        sendSocket.close();
        sendThread.interrupt();
    }
    
    public void startSendServer() throws SocketException, IllegalStateException
    {
        if(!stopped)
            throw new IllegalStateException("Send server is already running");
       
        stopped = false;
        sendSocket = new DatagramSocket(localSendPort);
        
        print("", "Starting send server...");
        sendThread = new Thread(getSendRunnable());
        sendThread.setDaemon(true);
        sendThread.start();
    }
    
    public void stopReceiveServer() throws IllegalStateException
    {
        if(stopped)
            throw new IllegalStateException("Receive server is already stopped");
        
        stopped = true;
        receiveSocket.close();
    }
    
    public void startReceiveServer() throws SocketException, IllegalStateException
    {
        if(!stopped)
            throw new IllegalStateException("Receive Server is already running");
        
        lastReceivedTime = Instant.now();
        
        stopped = false;
        receiveSocket = new DatagramSocket(receivePort);
        
        print("", "Starting receive server...");
        receiveThread = new Thread(getReceiveRunnable());
        receiveThread.setDaemon(true);
        receiveThread.start();
    }
    
    public DatagramPacket createPacketFromMessage(HeartBeatMessage message)
    {
        //Send objects in datagrams
        ByteArrayOutputStream baos = new ByteArrayOutputStream(6400);
        ObjectOutputStream oos;
        DatagramPacket packet = null;
                
        try 
        {
            oos = new ObjectOutputStream(baos);
            oos.writeObject(message);
            byte[] data = baos.toByteArray();
            //print("", "Packet size: " + data.length);
            packet = new DatagramPacket(data, data.length);
        }
        catch (IOException ex) 
        {
            print("", "Exception while creating byte stream: " + ex.getMessage());
        }
        
        return packet;
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
            print("", "Exception while creating object input stream: " + ex.getMessage());
        } 
        catch (ClassNotFoundException ex) 
        {
            print("", "Exception while reading object from stream: " + ex.getMessage());
        }
        return message;
    }
    
    public synchronized void updateLastReceivedTime(Instant instant)
    {
        boolean wasAfter = false;
        if(instant.isAfter(lastReceivedTime))
        {
            lastReceivedTime = instant;
            wasAfter = true;
        }
       
        //print("", "Last received changed: " + wasAfter);
    }
    
    public synchronized Instant getLastReceivedTime()
    {
        return this.lastReceivedTime;
    }
    
    private void print(String indents, String message)
    {
        System.out.println(indents + message);
    }
}

