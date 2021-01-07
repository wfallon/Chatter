package edu.upenn.cis350.chatter;

import android.content.Intent;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.InetAddress;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import java.io.File;
import edu.upenn.cis350.chatter.MainActivity;

import static androidx.core.content.ContextCompat.startActivity;

class AuthenticateTask extends AsyncTask<String, Void, Integer> {

    private String userAttempt;
    private String pwAttempt;
    private LoginActivity mine;

    public AuthenticateTask(String user, String pw, LoginActivity a) {
        userAttempt = user;
        pwAttempt = pw;
        mine = a;
    }

    protected Integer doInBackground(String... username) {
        HttpURLConnection connected;
        try {
            //get current IP address
            InetAddress myAddress = InetAddress.getLocalHost();
            String ip = myAddress.getHostAddress().trim();

            URL url = new URL("http://10.0.2.2:3000/userByUsername/" + userAttempt);
            try {
                connected = (HttpURLConnection) url.openConnection();
                connected.setRequestMethod("GET");

                int status = connected.getResponseCode();


                //need JSON parsing package

                if (status == 200) {
                    BufferedReader br = new BufferedReader(new InputStreamReader(connected.getInputStream()));
                    JSONObject user = new JSONObject(br.readLine());
                    if (pwAttempt.equals(user.get("password"))) {
                        mine.launcher();
                    }
                }

                connected.disconnect();
                return status;
            } catch (Exception a) {
                a.printStackTrace();
                return -1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    protected boolean execute(Integer status) {
        if(status == 200) {
            System.out.println("Good");
            return true;
        } else {
            System.out.println("Bad");
            return false;
        }
    }
}
