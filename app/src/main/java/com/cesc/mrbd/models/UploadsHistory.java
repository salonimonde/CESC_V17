package com.cesc.mrbd.models;

import java.io.Serializable;

/**
 * Created by Bynry01 on 24-08-2016.
 */
public class UploadsHistory implements Serializable
{
    public String consumer_no;
    public String bill_cycle_code;
    public String route_code;
    public String upload_status;
    public String month;
    public String reading_date;
    public String meter_reader_id;


    public UploadsHistory()
    {

    }

    public UploadsHistory(String consumer_name, String consumer_id, String meter_no, String dt_code, String bil_cycle_code, String pol_no, String route_id, String connection_status, String month, String email_id)
    {
        this.consumer_no = consumer_id;
        this.bill_cycle_code = bil_cycle_code;
        this.route_code = route_id;
        this.month = month;
    }

}
