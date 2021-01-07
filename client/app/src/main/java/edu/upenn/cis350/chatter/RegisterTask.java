package edu.upenn.cis350.chatter;

import android.os.AsyncTask;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.URL;

class RegisterTask extends AsyncTask<String, Void, Integer> {

    Signup mine;

    public RegisterTask(Signup a) {
        mine = a;
    }

    protected Integer doInBackground(String... profile) {
        HttpURLConnection connected;
        try {
            //get current IP address
            InetAddress myAddress = InetAddress.getLocalHost();
            String ip = myAddress.getHostAddress().trim();
 //           System.out.println(ip);

//            //edit xml file to allow from current IP address
//            System.out.println(System.getProperty("java.class.path"));
//
//            String filepath = "res/xml/network_security_config.xml";
//            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
//            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();
//            Document doc = docBuilder.parse(filepath);
//            Node domain = doc.getElementById("domain");
//            domain.setTextContent(ip);



            ///change url and take input from xml file
            URL url = new URL("http://" + "10.0.2.2" + ":3000/addUser");
            try {
                connected = (HttpURLConnection) url.openConnection();
                connected.setRequestMethod("POST");
                connected.setRequestProperty("Content-Type","application/json");

                //Sending parameters to body of request
                String interests = "[\"" + profile[2] + "\", \"" + profile[3] + "\"]";
                String body = "{\"username\": \"" + profile[0] + "\", \"password\": \"" +
                        profile[1] + "\", \"interests\": " + interests + "}";

                byte[] outputInBytes = body.getBytes("UTF-8");
                OutputStream os = connected.getOutputStream();
                os.write(outputInBytes);
                os.close();

                int status = connected.getResponseCode();

                if (status == 201) {
                    mine.launcher();
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

    protected void onPostExecute(Integer status) {
        if(status == 201) {
            System.out.println("Good");
        } else if (status == 403) {
            System.out.println("Bad");
        } else {
            System.out.println("Worse");
        }
    }
}
