package com.chintans.venturebox.cards;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.chintans.venturebox.R;
import com.chintans.venturebox.widget.CardView;

public class NoticesCard extends CardView {

	public NoticesCard(Context context, AttributeSet attrs, Bundle savedInstanceState,
                       String priority, String date, String notice) {
        super(context, attrs, savedInstanceState);
        setLayoutId(R.layout.card_notices);

        String noticeTitle;
        int textColor = getResources().getColor(R.color.primary_text);

        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.card_notices, this);
        TextView noticeText = (TextView) view.findViewById(R.id.notice_text);

        switch (priority) {
            case "low":
                textColor = getResources().getColor(R.color.material_blue);
                break;
            case "normal":
                textColor = getResources().getColor(R.color.material_green);
                break;
            case "warning":
                textColor = getResources().getColor(R.color.material_orange);
                break;
            case "urgent":
                textColor = getResources().getColor(R.color.material_red);
                break;
        }
        noticeTitle = getResources().getString(R.string.notices) + ": " + date;
        setTopTitle(noticeTitle, textColor);
        noticeText.setText(notice);
    }
}
