package com.cneilson.android.fantasymatchups;

import org.jsoup.nodes.Element;
import java.util.HashMap;
import java.util.Map;

public abstract class FantasyTeam {
	/* A FantasyTeam needs to hold this information*/
	protected String teamName;
	protected String leagueName;
	protected String leagueId;
	protected String teamLink;
	// playerList currently maps playerIndex(int) to playerName(String)
	protected Map<Integer, String> playerList;
	
	// Constructor
	public FantasyTeam(String team, String league)
	{
		teamName = team;
		leagueName = league;
		playerList = new HashMap<Integer, String>();
	}
	
	// Methods
	public String getTeamName()
	{
		return teamName;
	}
	
	public String getLeagueName()
	{
		return leagueName;
	}
	
	public String getId()
	{
		return leagueId;
	}
	
	public String getLink()
	{
		return teamLink;
	}
	
	abstract void setLink(Element link);
}