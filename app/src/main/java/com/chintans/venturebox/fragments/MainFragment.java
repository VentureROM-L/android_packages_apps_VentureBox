package com.chintans.venturebox.fragments;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.chintans.venturebox.R;
import com.chintans.venturebox.cards.NoticesCard;
import com.chintans.venturebox.cards.SystemCard;
import com.chintans.venturebox.updater.RomUpdater;
import com.chintans.venturebox.util.IOUtils;
import com.chintans.venturebox.widget.CardView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MainFragment extends Fragment implements
        Response.Listener<JSONObject>, Response.ErrorListener {
    private LinearLayout mCardsLayout;
    private RequestQueue mQueue;
    private Bundle mSavedInstanceState;
    private Context mContext;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private JsonObjectRequest mJsObjRequest;

    private SystemCard mSystemCard;
    private RomUpdater mRomUpdater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.main_fragment, container, false);
        mCardsLayout = (LinearLayout) root.findViewById(R.id.cards_layout);
        mSwipeRefreshLayout = (SwipeRefreshLayout)
                root.findViewById(R.id.main_fragment);
        mSavedInstanceState = savedInstanceState;
        mQueue = Volley.newRequestQueue(getActivity());
        mContext = getActivity().getApplicationContext();
        mRomUpdater = new RomUpdater(mContext, false);

        mSwipeRefreshLayout.setColorSchemeColors(getResources().getColor(R.color.toolbar_bg),
                getResources().getColor(R.color.accent), getResources().getColor(R.color.toolbar_tint));
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                refreshContent();
            }
        });

        if (mSystemCard == null) {
            mSystemCard = new SystemCard(mContext, null, mRomUpdater,
                    mSavedInstanceState);
        }

        // Add in the 'constant' cards
        mSystemCard.refreshCard();
        addCards(new CardView[] {mSystemCard}, true, true);

        mJsObjRequest = new JsonObjectRequest(Request.Method.GET, "http://api.venturerom.com/notices/", null, this, this);
        mQueue.add(mJsObjRequest);

        if (mSavedInstanceState == null) {
            IOUtils.init(getActivity());
            mCardsLayout.setAnimation(AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in));
        }
        return root;
    }

    /*
     * Refresh the content being displayed
     * Get new information from online for OTA
     * and notices
     */
    private void refreshContent() {
        mJsObjRequest = new JsonObjectRequest(Request.Method.GET, "http://api.venturerom.com/notices/",
                null, this, this);
        mQueue.add(mJsObjRequest);
        mSystemCard.refreshCard();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(false);
            }
        }, 3000);
    }

    public void addCards(CardView[] cardViews, boolean animate, boolean remove) {
        mCardsLayout.clearAnimation();
        if (remove) {
            mCardsLayout.removeAllViews();
        }
        if (animate) {
            mCardsLayout.setAnimation(AnimationUtils.loadAnimation(mContext, android.R.anim.fade_in));
        }
        for (CardView cardView : cardViews) {
            mCardsLayout.addView(cardView);
        }
    }

    @Override
    public void onResponse(JSONObject response) {
        if(response.has("notices")){
            JSONArray updates;
            String aPriority[];
            String aDate[];
            String aNotice[];
            try {
                int notices = Integer.parseInt(response.getString("notices"));
                int count = 0;
                aPriority = new String[notices];
                aDate = new String[notices];
                aNotice = new String[notices];
                if (notices != 0) {
                    updates = response.getJSONArray("data");
                    for (int i = updates.length() - 1; i >= 0; i--) {
                        JSONObject file = updates.getJSONObject(i);
                        String date = file.optString("date");
                        String notice = file.optString("notice");
                        String priority = file.optString("priority");
                        aPriority[count] = priority;
                        aDate[count] = date;
                        aNotice[count] = notice;
                        count++;
                    }
                    CardView[] cardViews = new CardView[notices];
                    for (int i = 0; i < notices; i++) {
                         cardViews[i] = new NoticesCard(mContext, null, mSavedInstanceState, aPriority[i], aDate[i], aNotice[i]);
                    }
                    addCards(cardViews, true, true);
                    addCards(new CardView[] {mSystemCard}, true, false);
                }
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch(NumberFormatException nfe){
                nfe.printStackTrace();
            }
        } else if (response.has("changelog")) {
            //Add changelog code here
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        // Empty
    }
}
