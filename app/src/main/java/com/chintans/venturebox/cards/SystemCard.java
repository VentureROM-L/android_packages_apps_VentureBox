/*
 * Copyright 2014 ParanoidAndroid Project
 *
 * This file is part of Paranoid OTA.
 *
 * Paranoid OTA is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Paranoid OTA is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Paranoid OTA.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.chintans.venturebox.cards;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.chintans.venturebox.R;
import com.chintans.venturebox.updater.RomUpdater;
import com.chintans.venturebox.util.Utils;
import com.chintans.venturebox.widget.CardView;
import com.chintans.venturebox.widget.Item;

import org.json.JSONObject;

public class SystemCard extends CardView {

    private RomUpdater mRomUpdater;
    private Context mContext;

    private View mSeparator;
    private Item mUpdateItem;

    private boolean mUpdateAvailable;
    private boolean mUpdateDownloading;
    private boolean mUpdateDownloaded;

    public SystemCard(Context context, AttributeSet attrs, RomUpdater romUpdater,
                      Bundle savedInstanceState) {
        super(context, attrs, savedInstanceState);
        setLayoutId(R.layout.card_system);
        setTopTitle(getResources().getString(R.string.system_title), getResources().getColor(
                        R.color.primary_text)
        );
        mContext = context;
        mRomUpdater = new RomUpdater(mContext, false);

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.card_system, this);

        TextView romView = (TextView) view.findViewById(R.id.rom);
        romView.setText(getResources().getString(R.string.system_rom,
                romUpdater.getVersion().toString(false)));

        TextView maintainerView = (TextView) view.findViewById(R.id.maintainer);
        maintainerView.setText(getResources().getString(R.string.system_maintainer,
                romUpdater.getMaintainer()));

        mSeparator = view.findViewById(R.id.separator);
        mUpdateItem = (Item) view.findViewById(R.id.update_found);
        refreshCard();
    }

    /**
     * Refreshes the card while checking for new updates
     */
    public void refreshCard() {
        checkForUpdates();
        setText();
    }

    /**
     * Refreshes the SystemCard texts to reflect any new changes from updates checks
     */
    private void setText() {
        if (mUpdateAvailable == true || mUpdateDownloading == true
                || mUpdateDownloaded == true) {
            mSeparator.setVisibility(View.VISIBLE);
            mUpdateItem.setVisibility(View.VISIBLE);
        } else {
            mSeparator.setVisibility(View.GONE);
            mUpdateItem.setVisibility(View.GONE);
        }

        // TODO: Change to real strings
        if (mUpdateAvailable == true) {
            mUpdateItem.setTitle("Update is available! Download?");
        } else if (mUpdateDownloading == true) {
            mUpdateItem.setTitle("Update is downloading!");
        } else if (mUpdateDownloaded == true) {
            mUpdateItem.setTitle("Update is downloaded! Install?");
        }
    }

    private void checkForUpdates() {
        RequestQueue queue = Volley.newRequestQueue(mContext);
        String url = "http://api.venturerom.com/updates/" + Utils.getProp("ro.build.product");

        JsonObjectRequest jsObjRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        mUpdateAvailable = mRomUpdater.isUpdateAvailable(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.e("lol", "fku");
                    }
                });
        queue.add(jsObjRequest);
    }
}