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

package com.chintans.venturebox.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.chintans.venturebox.R;

public abstract class CardView extends LinearLayout {

    private Context mContext;
    private View mView;
    private LinearLayout mCardLayout;
    private LinearLayout mCardArea;
    private TextView mCardTopTitle;
    private TextView mTitleView;
    private View mLayoutView;

    public CardView(Context context, AttributeSet attrs, Bundle savedInstanceState) {
        super(context, attrs);
        mContext = context;
        String topTitle = null;

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CardView);
        CharSequence cs = a.getString(R.styleable.CardView_cardTopTitle);

        if (cs != null) {
            topTitle = cs.toString();
        }

        a.recycle();

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.card, this, true);

        mCardLayout = (LinearLayout) mView.findViewById(R.id.card_layout);
        mCardArea = (LinearLayout) mView.findViewById(R.id.header_layout);
        mCardTopTitle = (TextView) mView.findViewById(R.id.card_top_title);
        mCardTopTitle.setText(topTitle);
    }

    public void setTopTitle(int resourceId) {
        mCardTopTitle.setText(resourceId);
    }

    public void setTopTitle(String text, int textColor) {
        mCardTopTitle.setText(text);
        mCardTopTitle.setTextColor(textColor);
    }

    protected void setLayoutId(int id) {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mLayoutView = inflater.inflate(id, mCardArea, true);
    }

    protected View findLayoutViewById(int id) {
        return mLayoutView.findViewById(id);
    }
}