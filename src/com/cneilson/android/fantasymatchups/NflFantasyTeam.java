/**
 * 
 */
package com.cneilson.android.fantasymatchups;

import org.jsoup.nodes.Element;
import java.util.HashMap;
import java.util.Map;

public class NflFantasyTeam extends FantasyTeam {

	public NflFantasyTeam(String team, String league) {
		super(team, league);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setLink(Element link){
		String linkToBe = "http://m.fantasy.nfl.com";
		String urlText = link.attr("href");
		teamLink = linkToBe + urlText;
		urlText = urlText.substring(0,urlText.lastIndexOf("teamId"));
		urlText.replaceAll("\\D+","");
		leagueId = urlText;
	}
}
