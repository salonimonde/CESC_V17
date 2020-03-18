package com.cesc.mrbd.models;

import java.util.ArrayList;

/**
 * Created by Bynry01 on 12-09-2016.
 */
public class Response
{
  public String page_job_cards_count;
  public String is_next;
  public ArrayList<UserProfile> user_info;
  public ArrayList<JobCard> jobcards;
  public ArrayList<BillCard> billcards;
  public ArrayList<String> re_de_assigned_jobcards;
  public ArrayList<String> re_de_bd_jobcards;
  public PaymentCalculation first_month;
  public PaymentCalculation second_month;
  public PaymentCalculation third_month;
  public String error_code;
  public ArrayList<String> error;
  public ArrayList<String> new_meter_readings;
  public ArrayList<String> new_unbilled_consumers;
  public String bank_name;
  public String ac_name;
  public String ifsc;
  public String ac_no;

  public ArrayList<Disconnection> disconnectionCards;
  public ArrayList<UploadDisconnectionNotices> disconnection_jobcard;
  public ArrayList <PaymentCalculation> months;

}
