package com.cneilson.android.fantasymatchups;

public enum SiteName {
    CBS,
    NFL,
    TSN,
    YAHOO;

    public static SiteName determineSiteName(String site) 
    {
        if (site.equals("CBS"))
        {
            return CBS;
        }
        else if (site.equals("NFL.com"))
        {
            return NFL;
        }
        else if (site.equals("TSN"))
        {
            return TSN;
        }
        else if (site.equals("Yahoo!"))
        {
            return YAHOO;
        }
        else 
        {
            return null;
        }
    }
}
