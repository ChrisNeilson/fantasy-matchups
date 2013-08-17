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
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class MainActivity extends Activity 
{
    
    private static final String USERNAME = "com.cneilson.android.fantasymatchups.USERNAME";

    @Override
    protected void onCreate(Bundle savedInstanceState) 
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_android);
        setUpSiteSpinner();
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
        Intent intent = new Intent(this, LoadHomePageActivity.class);
        EditText editTextUsername = (EditText) findViewById(R.id.username);
        EditText editTextPassword = (EditText) findViewById(R.id.password);
        Spinner siteSpinner = (Spinner) findViewById(R.id.site_spinner);
        
        String username = editTextUsername.getText().toString();
        String password = editTextPassword.getText().toString();
        String site = siteSpinner.getSelectedItem().toString();

        try {
            getTeamLinks(username, password, SiteName.YAHOO);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        
        intent.putExtra(USERNAME, username);
        startActivity(intent);
    }
    
    // Currently site parameter is ignored - only connects to Y!.
    public static void getTeamLinks(String username, String password, SiteName site) throws IOException
    {
        String loginUrl = "";
        String loginField = "";
        String passwordField = "";
        switch (site)
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
        
        response = Jsoup.connect(loginUrl)
                .data(loginField, username, passwordField, password)
                .method(Method.POST)
                .execute();
        
        if (response != null)
        {
            loginCookies = response.cookies();
        }
        
        // If the login was incorrect, no cookies will be returned
        if (loginCookies.size() == 0) 
        {
            return;
        }
        
        // Get all the links to your teams on this site
        Document doc = Jsoup.connect("http://football.fantasysports.yahoo.com/")
            .cookies(loginCookies)
            .get();
        
        Elements teamLinks = doc.select("a[class][href^=http://football.fantasysports.yahoo.com/f1/]");
      
    }
    
}
