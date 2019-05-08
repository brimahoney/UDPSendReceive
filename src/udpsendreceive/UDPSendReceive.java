package udpsendreceive;

import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import network.QuoteClient;
import network.HeartBeatServer;

public class UDPSendReceive extends Application 
{
    private HeartBeatServer client;
    private HeartBeatServer server;
    
    private Button serverBtn;
    private Button clientBtn;
    private TextField ipField;
    private TextArea outputArea;
    
    private boolean serverState = false;
    private boolean clientState = false;
    
    @Override
    public void start(Stage primaryStage) 
    {
        server = new HeartBeatServer(outputArea);
        client = new HeartBeatServer(outputArea);
        ipField = new TextField();
        ipField.setPromptText("Enter IP address of client");
        outputArea = new TextArea();
        outputArea.setEditable(false);
        
        
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
                
        vBox.getChildren().addAll(serverBtn, clientBtn, ipField, outputArea);
        
        Scene scene = new Scene(vBox, 300, 250);
        
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
        this.serverState = !serverState;

        if(serverState)
        {
            try 
            {
                String ipAddress = ipField.getText();
                server.startSendServer(ipAddress);
            }
            catch (SocketException ex) 
            {
                System.out.println("Failed to start server: " + ex.getMessage());
            }
            catch (IllegalStateException ex) 
            {
                System.out.println("Failed to start server: " + ex.getMessage());
            }
        }
        else
        {
            server.stopSendServer();
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
            }
            catch (IllegalStateException ex) 
            {
                System.out.println("B) Failed to start client: " + ex.getMessage());
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
