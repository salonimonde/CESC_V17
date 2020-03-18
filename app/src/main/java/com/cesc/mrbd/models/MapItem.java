package com.cesc.mrbd.models;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Created by Piyush on 07-03-2017.
 * Bynry
 */
public class MapItem implements ClusterItem
{
    private final LatLng mPosition;

    private int mType;

    private  String mTitle;

    private  String mSnippet;

    public MapItem(double lat, double lng)
    {
        mPosition = new LatLng(lat, lng);
    }

    @Override
    public LatLng getPosition()
    {
        return mPosition;
    }

    public int getmType()
    {
        return mType;
    }

    public void setmType(int mType)
    {
        this.mType = mType;
    }

    public String getmTitle()
    {
        return mTitle;
    }

    public void setmTitle(String mTitle)
    {
        this.mTitle = mTitle;
    }

    public String getmSnippet()
    {
        return mSnippet;
    }

    public void setmSnippet(String mSnippet)
    {
        this.mSnippet = mSnippet;
    }
}
