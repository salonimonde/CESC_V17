package com.cesc.mrbd.fragments;

        import android.content.Context;
        import android.graphics.Typeface;
        import android.net.Uri;
        import android.os.Bundle;
        import android.support.v4.app.Fragment;
        import android.support.v7.widget.LinearLayoutManager;
        import android.support.v7.widget.RecyclerView;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.TextView;

        import com.cesc.mrbd.R;
        import com.cesc.mrbd.activity.LandingActivity;
        import com.cesc.mrbd.adapters.HistoryCardAdapter;
        import com.cesc.mrbd.configuration.AppConstants;
        import com.cesc.mrbd.db.DatabaseManager;
        import com.cesc.mrbd.models.HistoryCard;
        import com.cesc.mrbd.models.UploadsHistory;
        import com.cesc.mrbd.models.UserProfile;
        import com.cesc.mrbd.preferences.SharedPrefManager;
        import com.cesc.mrbd.utils.App;
        import com.cesc.mrbd.utils.CommonUtils;
        import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.MapView;

        import java.util.ArrayList;

/**
 * Created by Bynry01 on 10/14/2016.
 */

public class LandingHistoryFragment extends Fragment
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1, meter_reader_id;
    private String mParam2;
    Context mContext;
    LinearLayoutManager layoutManager;
    RecyclerView recyclerView;
    private View mRootView;
    private UserProfile userProfile;

    private LandingSummaryFragment.OnFragmentInteractionListener mListener;
    private ArrayList<HistoryCard> mHistoryCardsArray;

    private TextView lblBlankScreenMsg;

    public LandingHistoryFragment()
    {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LandingSummaryFragment.
     */

    // TODO: Rename and change types and number of parameters
    public static LandingHistoryFragment newInstance(String param1, String param2)
    {
        LandingHistoryFragment fragment = new LandingHistoryFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null)
        {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        mRootView =inflater.inflate(R.layout.fragment_landing_history, container, false);
        mContext = getActivity();
        getUserProfileDetails();
        loadRecyclerView();
//        loadMapView(rootView,savedInstanceState);

        return mRootView;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        if(mRootView!=null)
            loadRecyclerView();
    }

    private void loadRecyclerView()
    {
        lblBlankScreenMsg = (TextView) mRootView.findViewById(R.id.lbl_blank_msg);
        Typeface reg = App.getSansationRegularFont();
        lblBlankScreenMsg.setTypeface(reg);
        recyclerView = (RecyclerView) mRootView.findViewById(R.id.recycler_view);
        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        getHistoryDatesArray();

        HistoryCardAdapter adapter = new HistoryCardAdapter(mContext, mHistoryCardsArray);
        recyclerView.setAdapter(adapter);

        if(mHistoryCardsArray != null && mHistoryCardsArray.size() > 0)
            lblBlankScreenMsg.setVisibility(View.GONE);
        else
            lblBlankScreenMsg.setVisibility(View.VISIBLE);
    }


    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri)
    {
        if (mListener != null)
        {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context)
    {
        super.onAttach(context);
    }

    @Override
    public void onDetach()
    {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    private void getUserProfileDetails()
    {
        userProfile = DatabaseManager.getUserProfile(mContext, SharedPrefManager.getStringValue(mContext, SharedPrefManager.USER_ID));
        if (userProfile != null)
        {
            meter_reader_id = userProfile.meter_reader_id;
        }
    }

    private void getHistoryDatesArray()
    {
        try{
            //delete old records which are not in range of days.
            DatabaseManager.deleteUploadsHistory(getActivity(),meter_reader_id);

            int lTotalRevisit = 0;
            int lTotalNormal = 0;
            int lTotalNewConsumer = 0;
            mHistoryCardsArray=new ArrayList<>();
            for (int i = 0; i < AppConstants.UPLOAD_HISTORY_DATE_COUNT; i++)
            {
                String date = CommonUtils.getPreviousDate(i);
                ArrayList<String> uploadsHistoryRoutes = DatabaseManager.getUploadsHistoryRoutes(mContext,date);
                if(uploadsHistoryRoutes != null)
                {
                    for (int j = 0; j < uploadsHistoryRoutes.size(); j++)
                    {
                        String route = uploadsHistoryRoutes.get(j);
                        ArrayList<UploadsHistory> uploadsHistory = DatabaseManager.getUploadsHistory(mContext,CommonUtils.getPreviousDate(i),route,meter_reader_id);
                        String billcycle=uploadsHistory.get(0).bill_cycle_code;

                        int revisit = 0;
                        int normal = 0;
                        int newConsumer = 0;
                        for (int i2 = 0; i2 < uploadsHistory.size(); i2++)
                        {
                            if(uploadsHistory.get(i2).upload_status.equalsIgnoreCase(getString(R.string.addnewconsumer)))
                            {
                                newConsumer+=1;
                                lTotalNewConsumer+=1;
                            }
                            else if(uploadsHistory.get(i2).upload_status.equalsIgnoreCase(getString(R.string.meter_status_normal)))
                            {
                                normal+=1;
                                lTotalNormal+=1;
                            }
                            else if(uploadsHistory.get(i2).upload_status.equalsIgnoreCase(getString(R.string.revisit)))
                            {
                                revisit+=1;
                                lTotalRevisit+=1;
                            }
                        }
                        HistoryCard historyCard = new HistoryCard();
                        historyCard.date = date;
                        historyCard.route = route;
                        historyCard.open = normal;
                        historyCard.unbill = newConsumer;
                        historyCard.revisit = revisit;
                        historyCard.billcycle=billcycle;

                        mHistoryCardsArray.add(historyCard);

                        ((LandingActivity) getActivity()).loadHistoryData(lTotalNormal, lTotalRevisit, lTotalNewConsumer);
                    }
                }
            }
        }catch (Exception e) {}
    }
}