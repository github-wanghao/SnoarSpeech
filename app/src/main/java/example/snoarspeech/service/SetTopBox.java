package example.snoarspeech.service;

import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.net.UnknownHostException;



/**
 * Created by jf on 5/1/2016.
 */
public class SetTopBox {

    public OutputStream os;
    public Socket socket;
    public void openSocket(){
        new Thread() {
            @Override
            public void run() {
                try {
                    socket = new Socket("172.20.10.3", 27015);
                    os =socket.getOutputStream();
                    os.write(123);
                }
                catch (UnknownHostException e) {
                    System.out.println("Tvsr:Server err!");
                    e.printStackTrace();
                } catch (IOException e) {
                    System.out.println("Tvsr:Server err!");
                    e.printStackTrace();
                }
            }
        }.start();
    }
    public void onKeyDown(byte keyCode) {

        try {
            if(os !=null) {
                os.write(keyCode);
            }
        } catch (UnknownHostException e) {
            System.out.println("Tvsr:Server err!");
            e.printStackTrace();
        } catch (IOException e) {
            System.out.println("Tvsr:Server err!");
            e.printStackTrace();
        }

    }

}
