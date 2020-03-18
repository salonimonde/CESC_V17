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
import com.cesc.mrbd.adapters.SummaryCardAdapter;
import com.cesc.mrbd.db.DatabaseManager;
import com.cesc.mrbd.models.SummaryCard;
import com.cesc.mrbd.utils.App;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LandingSummaryFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link LandingSummaryFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class  LandingSummaryFragment extends Fragment
{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private Context mContext;
    private LinearLayoutManager layoutManager;
    private RecyclerView recyclerView;

    private OnFragmentInteractionListener mListener;
    private ArrayList<SummaryCard> mSummaryCardsArray;

    private TextView lblBlankScreenMsg;

    public LandingSummaryFragment()
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
    public static LandingSummaryFragment newInstance(String param1, String param2)
    {
        LandingSummaryFragment fragment = new LandingSummaryFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View rootView =inflater.inflate(R.layout.fragment_landing_summary, container, false);
        mContext = getActivity();
        loadRecyclerView(rootView);

        return rootView;
    }

    private void loadRecyclerView(View rootView)
    {

        Typeface reg = App.getSansationRegularFont();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view);
        lblBlankScreenMsg = (TextView) rootView.findViewById(R.id.lbl_blank_msg);
        lblBlankScreenMsg.setTypeface(reg);
        layoutManager = new LinearLayoutManager(getActivity().getApplicationContext());
        recyclerView.setLayoutManager(layoutManager);
        mSummaryCardsArray = DatabaseManager.getSummaryCard(mContext, LandingActivity.meter_reader_id);
        SummaryCardAdapter adapter = new SummaryCardAdapter(mContext,mSummaryCardsArray);
        recyclerView.setAdapter(adapter);

        if(mSummaryCardsArray != null && mSummaryCardsArray.size() > 0)
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
}
