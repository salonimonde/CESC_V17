package com.cesc.mrbd.models;

import java.io.Serializable;

/**
 * Created by Bynry01 on 12-09-2016.
 */
public class MeterImage implements Serializable
{
    public String name;
    public String image;
    public String content_type;

    public MeterImage(String name, String image, String content_type)
    {
        this.name = name;
        this.image = image;
        this.content_type = content_type;
    }

    public MeterImage()
    {
    }
}
