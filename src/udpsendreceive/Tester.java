package udpsendreceive;

import java.time.Instant;
import java.time.temporal.ChronoField;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Tester
{
    public static void main(String[] args)
    {
        Instant now = Instant.now();
        
        try
        {
            Thread.sleep(1000);
        } 
        catch (InterruptedException ex)
        {
            Logger.getLogger(Tester.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        Instant later = Instant.now();
        
        System.out.println("now.compareTo(later): " + now.compareTo(later));
        System.out.println("later.compareTo(now): " + later.compareTo(now));
        System.out.println("now.getLong(): " + now.getLong(ChronoField.INSTANT_SECONDS));
        System.out.println("later.getLong(): " + later.getLong(ChronoField.INSTANT_SECONDS));
    }
}
