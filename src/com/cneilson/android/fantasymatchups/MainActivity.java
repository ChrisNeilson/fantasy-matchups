package com.cneilson.android.fantasymatchups;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
import android.widget.TextView;

public class MainActivity extends Activity 
{
    EditText inputUsername;
    EditText inputPassword;
    Spinner siteSpinner;
    String TEAMSANDLEAGUES;

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
    
    // Loads the HomePageActivity if login credentials are correct
    public void loadHomePage(View view) 
    {
        ProgressDialog progress = new ProgressDialog(this);
        progress.setMessage("Loading...");
        new GetTeamLinksTask(progress).execute();
    }
    
    public void incorrectUserOrPassword() 
    {
        TextView textview = (TextView) findViewById(R.id.incorrect);
        textview.setVisibility(0);
    }
    
    // Get team links asynchronously so we can display progress dialog
    public class GetTeamLinksTask extends AsyncTask<Void, Void, HashMap<String, String>> 
    {
        private ProgressDialog progress;
    
        public GetTeamLinksTask(ProgressDialog progress) 
        {
            this.progress = progress;
        }
    
        public void onPreExecute() 
        {
            progress.setCancelable(false);
            progress.show();
        }
    
        public HashMap<String, String> doInBackground(Void... unused) 
        {
            String site = siteSpinner.getSelectedItem().toString();
            String username = inputUsername.getText().toString();
            String password = inputPassword.getText().toString();
            
            SiteName sitename = SiteName.determineSiteName(site);
              
            String loginUrl = "";
            String topLevelUrl = "";
            String teamNameSearch = "";
            String leagueNameSearch = "";
            Map<String, String> connectionData = new HashMap<String, String>(); 
            switch (sitename)
            {
                case CBS:
                    break;
                case NFL:
                	loginUrl = "https://id2.s.nfl.com/fans/mobile/login?s=fantasy&returnTo=http%3A%2F%2Fm.fantasy.nfl.com%2F";
                	connectionData.put("username", username);
                	connectionData.put("password", password);
                	topLevelUrl = "http://m.fantasy.nfl.com/myfantasy";
                	teamNameSearch = "a[href*=teamId]";
                	leagueNameSearch = teamNameSearch + "span:not([class])";
                	break;
                case TSN:
                    break;
                case YAHOO:
                    loginUrl = "https://login.yahoo.com/config/login?.src=spt&.intl=us&.lang=en-US&.done=http://football.fantasysports.yahoo.com/";
                    connectionData.put("login", username);
                    connectionData.put("passwd", password);
                    topLevelUrl = "http://football.fantasysports.yahoo.com/";
                    teamNameSearch = "a[class][href^=http://football.fantasysports.yahoo.com/f1/]";
                    leagueNameSearch = "a[href^=http://football.fantasysports.yahoo.com/f1/]:not([class])";
                    break;
            }
              
            Connection.Response response = null;
            Map<String, String> loginCookies = new HashMap<String, String>();
              
            try 
            {
                response = Jsoup.connect(loginUrl)
                        .data(connectionData)
                        .method(Method.POST)
                        .execute();
            }
            catch (IOException e) 
            {
                // TODO Auto-generated catch block
            	System.out.println(e.getMessage());
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
                Document doc = Jsoup.connect(topLevelUrl)
                      .cookies(loginCookies)
                      .get();
                
                Elements teamLinks = doc.select(teamNameSearch);
                Elements leagueLinks = doc.select(leagueNameSearch);
                
                HashMap<String, String> teamsAndLeagues = new HashMap<String, String>();
                
                int index = 0;
                for (Element teamLink : teamLinks)
                {
                	switch (sitename)
                	{
                	case NFL:
                		String tName = teamLink.select("span[class=mfTeamName]").text();
                		String lName = teamLink.select("span:not([class])").text().substring(2);
                		teamsAndLeagues.put(tName, lName);
                		break;
                	case CBS:
                	case TSN:
                	case YAHOO:
                		teamsAndLeagues.put(nameParse(leagueLinks.get(index++)), nameParse(teamLink));
                		break;
                	default: teamsAndLeagues.put(nameParse(leagueLinks.get(index++)), nameParse(teamLink));
                	}
                	
                }
                return teamsAndLeagues;
            } 
            catch (IOException e)
            {
                // TODO Auto-generated catch block
            	System.out.println(e.getMessage());
                e.printStackTrace();
            }
              
            return null;
        }
    
        private String nameParse(Element name) 
        {
            return name.text();
        }

        public void onPostExecute(HashMap<String, String> teamsAndLeagues) 
        {
            progress.dismiss();
            if (teamsAndLeagues != null)
            {
                Intent intent = new Intent(getApplicationContext(), HomePageActivity.class);
                intent.putExtra(TEAMSANDLEAGUES, teamsAndLeagues); 
                startActivity(intent);
                finish();
            }
            else
            {
                incorrectUserOrPassword();
            }
        }
    }
}
