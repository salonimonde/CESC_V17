package com.cesc.mrbd.models;

import java.io.Serializable;

/**
 * Created by Piyush on 31-05-2017.
 * Bynry
 */
public class UploadBillHistory implements Serializable
{
    public String jobcard_id;
    public String cycle_code;
    public String binder_code;
    public String meter_reader_id;
    public String billmonth;
    public String reading_date;
    public String consumer_no;
    public String consumer_name;
    public String meter_no;
    public String zone_code;
    public String zone_name;
    public String is_new;

    public UploadBillHistory() {
    }
}

