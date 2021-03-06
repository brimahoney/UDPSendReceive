package udpsendreceive;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import network.HeartBeatServer;

public class UDPSendReceive extends Application 
{
    private HeartBeatServer client;
    private HeartBeatServer server;
    
    private Button serverBtn;
    private Button clientBtn;
    private TextField ipField;
    private TextField timeBetweenMessageField;
    
    
    private Label lastTimeLabel;
    
    private boolean serverState = false;
    private boolean clientState = false;
    
    @Override
    public void start(Stage primaryStage) 
    {
        ipField = new TextField();
        try
        {
            ipField.setPromptText(InetAddress.getLocalHost().toString());
        }
        catch (UnknownHostException ex)
        {
            System.out.println("Unknown host: " + ex.getMessage());
            ex.printStackTrace();
        }
        
        timeBetweenMessageField = new TextField();
        timeBetweenMessageField.setPromptText("Enter time in milliseconds between messages");
        
        lastTimeLabel = new Label("Last Updated Time");
        
        serverBtn = new Button("Start sending");
        clientBtn = new Button("Start receiving");
                
        serverBtn.setOnAction(new EventHandler<ActionEvent>() 
        {
            @Override
            public void handle(ActionEvent event) 
            {
                toggleServer();
            }
            
        });
        
        clientBtn.setOnAction(new EventHandler<ActionEvent>() 
        {
            @Override
            public void handle(ActionEvent event) 
            {
                toggleClient();
            }
        });

        
        //StackPane root = new StackPane();
        VBox vBox = new VBox(8);
        vBox.setStyle("-fx-padding: 10;" +
         "-fx-border-style: solid inside;" +
         "-fx-border-width: 2;" +
         "-fx-border-insets: 5;" +
         "-fx-border-radius: 5;" +
         "-fx-border-color: blue;");
        //root.getChildren().add(vBox);
                
        vBox.getChildren().addAll(serverBtn, clientBtn, ipField, timeBetweenMessageField, lastTimeLabel);
        
        Scene scene = new Scene(vBox, 300, 250);

        server = new HeartBeatServer(lastTimeLabel);
        client = new HeartBeatServer(lastTimeLabel);        
        
        primaryStage.setTitle("Datagram");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) 
    {
        launch(args);
    }
    
    private void toggleServer()
    {
        if(!serverState)
        {
            int timeBetweenMessages; // default
            try
            {
                timeBetweenMessages = Integer.parseInt(timeBetweenMessageField.getText());
            }
            catch(NumberFormatException nfe)
            {
                Alert errorAlert = new Alert(AlertType.NONE, "Please enter a time between messages value", ButtonType.OK);
                errorAlert.showAndWait();
                return;
            }
            String ipAddress = ipField.getText();

            this.serverState = !serverState;
            
            try
            {
                server.startSendServer(ipAddress, timeBetweenMessages);
            }
            catch (SocketException ex) 
            {
                System.out.println("A) Failed to start server: " + ex.getMessage());
                ex.printStackTrace();
            }
            catch (IllegalStateException ex) 
            {
                System.out.println("B) Failed to start server: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
        else
        {
            server.stopSendServer();
            this.serverState = !serverState;
        }
        
        String buttonText = this.serverState ? "Stop sending" : "Start sending";
        serverBtn.setText(buttonText);
    }
    
    private void toggleClient()
    {
        this.clientState = !clientState;
        
        if(clientState)
        {
            try 
            {
                client.startReceiveServer();
            }
            catch (SocketException ex) 
            {
                System.out.println("A) Failed to start client: " + ex.getMessage());
                ex.printStackTrace();
            }
            catch (IllegalStateException ex) 
            {
                System.out.println("B) Failed to start client: " + ex.getMessage());
                ex.printStackTrace();
            }
        }
        else
        {
            client.stopReceiveServer();
        }
        
        String buttonText = this.clientState ? "Stop receiving" : "Start receiving";
        clientBtn.setText(buttonText);
    }
}
