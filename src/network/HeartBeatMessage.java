package network;

import java.io.Serializable;
import java.time.Instant;

public class HeartBeatMessage implements Serializable
{
    Instant payload;
    
    public HeartBeatMessage()
    {
        payload = Instant.now();
    }
    
    public HeartBeatMessage(Instant instant)
    {
        payload = instant;
    }
    
    public void setPayload(Instant payload) 
    {
        this.payload = payload;
    }

    public Instant getPayload() 
    {
        return this.payload;
    }
    
    public String toString()
    {
        return payload.toString();
    }
    
}