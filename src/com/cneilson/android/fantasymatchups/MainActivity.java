package com.cneilson.android.fantasymatchups;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class MainActivity extends Activity 
{
    EditText inputUsername;
    EditText inputPassword;
    Spinner siteSpinner;
    String USERNAME;

    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_android);
        setUpSiteSpinner();
        
        inputUsername = (EditText) findViewById(R.id.username);
        inputPassword = (EditText) findViewById(R.id.password);
        siteSpinner = (Spinner) findViewById(R.id.site_spinner);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) 
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    public void setUpSiteSpinner() 
    {
        Spinner siteSpinner = (Spinner) findViewById(R.id.site_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.site_array, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        siteSpinner.setAdapter(adapter);
    }
    
    // Connects to site to scrape your teams roster, and will then start home page activity.
    // Home page will show your roster and whatnot
    // Possibly home page will list your teams at that site?
    public void loadHomePage(View view) 
    {
        ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("Loading...");
        new GetTeamLinksTask(progress).execute();
    }
        
    
    public class GetTeamLinksTask extends AsyncTask<Void, Void, Elements> 
    {
        private ProgressDialog progress;
    
        public GetTeamLinksTask(ProgressDialog progress) 
        {
            this.progress = progress;
        }
    
        public void onPreExecute() 
        {
            progress.show();
        }
    
        public Elements doInBackground(Void... unused) 
        {
            String site = siteSpinner.getSelectedItem().toString();
            String username = inputUsername.getText().toString();
            String password = inputPassword.getText().toString();
            
            SiteName sitename = SiteName.YAHOO;
              
            String loginUrl = "";
            String loginField = "";
            String passwordField = "";
            switch (sitename)
            {
                case CBS:
                    break;
                case NFL:
                    break;
                case TSN:
                    break;
                case YAHOO:
                    loginUrl = "https://login.yahoo.com/config/login?.src=spt&.intl=us&.lang=en-US&.done=http://football.fantasysports.yahoo.com/";
                    loginField = "login";
                    passwordField = "passwd";
                    break;
            }
              
            Connection.Response response = null;
            Map<String, String> loginCookies = new HashMap<String, String>();
              
            try 
            {
                response = Jsoup.connect(loginUrl)
                        .data(loginField, username, passwordField, password)
                        .method(Method.POST)
                        .execute();
            }
            catch (IOException e) 
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
              
            if (response != null)
            {
                loginCookies = response.cookies();
            }
              
            // If the login was incorrect, no cookies will be returned
            if (loginCookies.size() == 0) 
            {
                return null;
            }
              
            // Get all the links to your teams on this site
            try 
            {
                Document doc = Jsoup.connect("http://football.fantasysports.yahoo.com/")
                      .cookies(loginCookies)
                      .get();
                
                Elements teamLinks = doc.select("a[class][href^=http://football.fantasysports.yahoo.com/f1/]");
                return teamLinks;
            } 
            catch (IOException e)
            {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
              
            return null;
        }
    
        public void onPostExecute(Elements teamLinks) 
        {
            progress.dismiss();
            Intent intent = new Intent(getApplicationContext(), HomePageActivity.class);
            intent.putExtra(USERNAME, "teh_neilson");
            startActivity(intent);
            finish();
        }
    }
}
