package udpsendreceive;

import java.time.Instant;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ChangeListener;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Main
{
    public static void main(String[] args)
    {
        try
        {
            Bill electricBill = new Bill();
            
            electricBill.amountDueProperty().addListener(new ChangeListener()
            {
                @Override
                public void changed(ObservableValue o, Object oldVal,
                        Object newVal)
                {
                    System.out.println("Electric bill has changed! - " + Instant.now());
                }
            });
            
            electricBill.setAmountDue(100.00);
            Thread.sleep(2000);
            electricBill.setAmountDue(200.00);
                        Thread.sleep(2000);
            electricBill.setAmountDue(300.00);
            Thread.sleep(2000);
            electricBill.setAmountDue(400.00);
                        Thread.sleep(2000);
            electricBill.setAmountDue(500.00);
                        Thread.sleep(2000);
        } catch (InterruptedException ex)
        {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

}
