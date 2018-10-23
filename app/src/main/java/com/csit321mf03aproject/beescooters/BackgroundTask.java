package com.csit321mf03aproject.beescooters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

//Background Task Description: Function used to register or login a user

public class BackgroundTask extends AsyncTask<String,Void,String> {

    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    String password;

    public Context context;

    BackgroundTask(Context ctx){
        this.context = ctx;
    }

    @Override
    protected String doInBackground(String... params) {

        preferences = context.getSharedPreferences("MYPREFS", Context.MODE_PRIVATE);
        editor = preferences.edit();
        editor.putString("flag","0");
        editor.apply();

        String urlRegistration = "http://beescooters.net/LoginAndRegister-register.php";
        String urlLogin  = "http://beescooters.net/LoginAndRegister-login.php";
        String task = params[0];

        if(task.equals("register")){
            String regUsername = params[1];
            String regFirstName = params[2];
            String regLastName = params[3];
            String regEmail = params[4];
            String regAddress = params[5];
            String regPassword = params[6];

            try {
                URL url = new URL(urlRegistration);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                OutputStream outputStream = httpURLConnection.getOutputStream();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream,"UTF-8");
                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                String myData = URLEncoder.encode("identifier_name","UTF-8")+"="+URLEncoder.encode(regUsername,"UTF-8")+"&"
                        +URLEncoder.encode("identifier_given","UTF-8")+"="+URLEncoder.encode(regFirstName,"UTF-8")+"&"
                        +URLEncoder.encode("identifier_family","UTF-8")+"="+URLEncoder.encode(regLastName,"UTF-8")+"&"
                        +URLEncoder.encode("identifier_type","UTF-8")+"="+URLEncoder.encode("normal","UTF-8")+"&"
                        +URLEncoder.encode("identifier_email","UTF-8")+"="+URLEncoder.encode(regEmail,"UTF-8")+"&"
                        +URLEncoder.encode("identifier_address","UTF-8")+"="+URLEncoder.encode(regAddress,"UTF-8")+"&"
                        +URLEncoder.encode("identifier_password","UTF-8")+"="+URLEncoder.encode(regPassword,"UTF-8");
                bufferedWriter.write(myData);
                bufferedWriter.flush();
                bufferedWriter.close();
                InputStream inputStream = httpURLConnection.getInputStream();
                inputStream.close();

                editor.putString("flag","register");
                editor.commit();
                return "Successfully Registered " + regUsername;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        if(task.equals("login")){
            String loginEmail = params[1];
            String loginPassword = params[2];
            try {
                URL url = new URL(urlLogin);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setDoInput(true);

                //send the email and password to the database
                OutputStream outputStream = httpURLConnection.getOutputStream();
                OutputStreamWriter outputStreamWriter = new OutputStreamWriter(outputStream,"UTF-8");
                BufferedWriter bufferedWriter = new BufferedWriter(outputStreamWriter);
                String myData = URLEncoder.encode("identifier_loginEmail","UTF-8")+"="+URLEncoder.encode(loginEmail,"UTF-8")+"&"
                        +URLEncoder.encode("identifier_loginPassword","UTF-8")+"="+URLEncoder.encode(loginPassword,"UTF-8");
                bufferedWriter.write(myData);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();

                int num = httpURLConnection.getResponseCode();
                System.out.println(num);

                InputStream inputStream = httpURLConnection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream,"UTF-8");
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String dataResponse = "";
                String inputLine;
                while((inputLine = bufferedReader.readLine()) != null){
                    dataResponse += inputLine;
                }
                bufferedReader.close();
                inputStream.close();

                httpURLConnection.disconnect();

                System.out.println(dataResponse);

                editor.putString("flag","login");
                editor.commit();
                password = loginPassword;
                return  dataResponse;

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    @Override

    protected void onPreExecute() {
        super.onPreExecute();
    }

    //This method will be called when doInBackground completes... and it will return the completion string which
    //will display this toast.
    @Override
    protected void onPostExecute(String s) {
        String flag = preferences.getString("flag","0");

        if(flag.equals("register")) {
            Toast.makeText(context,s,Toast.LENGTH_LONG).show();
            Intent intent = new Intent(context,HowToRide2Screen.class);
                context.startActivity(intent);
        }
        else if(flag.equals("login")){
            String test = "false";
            String name = "";
            String email = "";
            String userID = "";
            String userType = "";
            String userGivenName = "";
            String userFamilyName = "";
            String registerDate = "";
            String address = "";
            String creditBalance = "";
            String customerID = "";
            String[] serverResponse = s.split("[,]");
            test = serverResponse[0];
            email = serverResponse[1];
            name = serverResponse[2];
            userID = serverResponse[3];

            userType = serverResponse[4];
            userGivenName = serverResponse[5];
            userFamilyName = serverResponse[6];
            registerDate = serverResponse[7];
            address = serverResponse[8];
            creditBalance = serverResponse[9];
            customerID = serverResponse[10];

            String fullName = userGivenName + " " + userFamilyName;

            if(test.equals("true")){
                editor.putString("name",name);
                editor.commit();
                editor.putString("email",email);
                editor.commit();
                editor.putString("userID",userID);
                editor.commit();
                editor.putBoolean("logged",true);

                editor.putString("userType",userType);
                editor.commit();
                editor.putString("fullName",fullName);
                editor.commit();
                editor.putString("registerDate", registerDate);
                editor.commit();
                editor.putString("address", address);
                editor.commit();
                editor.putString("creditBalance", creditBalance);
                editor.commit();
                editor.putString("userGivenName",userGivenName);
                editor.commit();
                editor.putString("userFamilyName",userFamilyName);
                editor.commit();
                editor.putString("customerID", customerID);
                editor.commit();

                Intent intent = new Intent(context,MainScreen.class);
                context.startActivity(intent);
            }else{
                display("Login Failed", "That email and password do not match our records.");
            }
        }else{
            display ("Login Failed", flag);
        }
    }

    @Override
    protected void onProgressUpdate(Void... values) {
        super.onProgressUpdate(values);
    }

    private void display(String title, String message){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
    }


}
