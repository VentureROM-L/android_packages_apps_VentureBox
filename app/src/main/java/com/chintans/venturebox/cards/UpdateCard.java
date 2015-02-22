package com.chintans.venturebox.cards;

import android.content.Context;
import android.content.res.Resources;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chintans.venturebox.R;
import com.chintans.venturebox.updater.RomUpdater;
import com.chintans.venturebox.updater.Updater.PackageInfo;
import com.chintans.venturebox.updater.Updater.UpdaterListener;
import com.chintans.venturebox.widget.CardView;
import com.chintans.venturebox.widget.Item;
import com.chintans.venturebox.widget.Item.OnItemClickListener;

import java.util.ArrayList;
import java.util.List;

public class UpdateCard extends CardView implements UpdaterListener, OnCheckedChangeListener {

    private static final String ROMS = "ROMS";

    private RomUpdater mRomUpdater;
    private Context mContext;

    private LinearLayout mLayout;
    private TextView mInfo;
    private TextView mError;
    private LinearLayout mAdditional;
    private TextView mAdditionalText;

    private Item mDownload;
    private ProgressBar mWaitProgressBar;

    private String mErrorRom;
    private String mState = null;
    private int mNumChecked = 0;

    public UpdateCard(Context context, AttributeSet attrs, RomUpdater romUpdater,
                       Bundle savedInstanceState) {
        super(context, attrs, savedInstanceState);

        mContext = context;
        mRomUpdater = romUpdater;
        mRomUpdater.addUpdaterListener(this);

        if (savedInstanceState != null) {
            List<PackageInfo> mRoms = (List) savedInstanceState.getSerializable(ROMS);

            mRomUpdater.setLastUpdates(mRoms.toArray(new PackageInfo[mRoms.size()]));
        }
        setLayoutId(R.layout.card_update);

        mLayout = (LinearLayout) findLayoutViewById(R.id.layout);
        mInfo = (TextView) findLayoutViewById(R.id.info);
        mError = (TextView) findLayoutViewById(R.id.error);
        mDownload = (Item) findLayoutViewById(R.id.download);
        mWaitProgressBar = (ProgressBar) findLayoutViewById(R.id.wait_progressbar);

        mAdditional = (LinearLayout) findLayoutViewById(R.id.additional);
        mAdditionalText = (TextView) findLayoutViewById(R.id.additional_text);

        mDownload.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onClick(int id) {
                mState = "download";
            }
        });
        mErrorRom = null;
        updateText();
    }

    public String getState() {
        return mState;
    }

    private void updateText() {
        mLayout.removeAllViews();
        mNumChecked = 0;
        mDownload.setEnabled(false);

        for (int i = mAdditional.getChildCount() - 1; i >= 0; i--) {
            if (mAdditional.getChildAt(i) instanceof TextView) {
                mAdditional.removeViewAt(i);
            }
        }

        Context context = getContext();
        Resources res = context.getResources();

        if (mRomUpdater.isScanning()) {
            if (!mLayout.equals(mWaitProgressBar.getParent())) {
                mLayout.addView(mWaitProgressBar);
            }
            setTopTitle(getResources().getString(R.string.updates_checking),
                    getResources().getColor(R.color.primary_text));
            mAdditional.addView(mAdditionalText);
        } else {
            mLayout.addView(mInfo);
            PackageInfo[] roms = mRomUpdater.getLastUpdates();
            if ((roms == null || roms.length == 0)) {
                setTopTitle(getResources().getString(R.string.updates_uptodate),
                        getResources().getColor(R.color.primary_text));
                mInfo.setText(R.string.no_updates_found);
                mAdditional.addView(mAdditionalText);
            } else {
                setTopTitle(getResources().getString(R.string.updates_found),
                        getResources().getColor(R.color.primary_text));
                mInfo.setText(res.getString(R.string.system_update));
                addPackages(roms);
            }
        }
        String error = mErrorRom;
        if (error != null) {
            mError.setText(error);
            mLayout.addView(mError);
        }
    }

    @Override
    public void startChecking(boolean isRom) {
        if (isRom) {
            mErrorRom = null;
        }
        updateText();
    }

    @Override
    public void versionFound(PackageInfo[] info, boolean isRom) {
        updateText();
    }

    @Override
    public void checkError(String cause, boolean isRom) {
        if (isRom) {
            mErrorRom = cause;
        }
        updateText();
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            mNumChecked++;
        } else {
            mNumChecked--;
        }
        mDownload.setEnabled(mNumChecked > 0);
        if (!isChecked) {
            return;
        }
        PackageInfo info = (PackageInfo) buttonView.getTag(R.id.title);
        unckeckCheckBoxes(info);
    }

    private void unckeckCheckBoxes(PackageInfo master) {
        String masterFileName = master.getFilename();
        boolean masterIsGapps = master.isGapps();
        for (int i = 0; i < mLayout.getChildCount(); i++) {
            View view = mLayout.getChildAt(i);
            if (view instanceof CheckBox) {
                PackageInfo info = (PackageInfo) view.getTag(R.id.title);
                String fileName = info.getFilename();
                boolean isGapps = info.isGapps();
                if (masterIsGapps == isGapps && !masterFileName.equals(fileName)) {
                    ((CheckBox) view).setChecked(false);
                }
            }
        }
    }

    private PackageInfo[] getPackages() {
        List<PackageInfo> list = new ArrayList<PackageInfo>();
        for (int i = 0; i < mLayout.getChildCount(); i++) {
            View view = mLayout.getChildAt(i);
            if (view instanceof CheckBox) {
                if (((CheckBox) view).isChecked()) {
                    PackageInfo info = (PackageInfo) view.getTag(R.id.title);
                    list.add(info);
                }
            }
        }
        return list.toArray(new PackageInfo[list.size()]);
    }

    private void addPackages(PackageInfo[] packages) {
        Context context = getContext();
        Resources res = context.getResources();
        for (int i = 0; packages != null && i < packages.length; i++) {
            CheckBox check = new CheckBox(context, null);
            check.setTag(R.id.title, packages[i]);
            check.setText(packages[i].getFilename());
            check.setOnCheckedChangeListener(this);
            check.setChecked(i == 0);
            mLayout.addView(check);
            TextView text = new TextView(context);
            text.setText(packages[i].getFilename());
            text.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    res.getDimension(R.dimen.card_textSize));
            text.setTextColor(getResources().getColor(R.color.secondary_text));
            text.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));
            mAdditional.addView(text);
            text = new TextView(context);
            text.setText(packages[i].getSize());
            text.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    res.getDimension(R.dimen.card_textSize));
            text.setTextColor(getResources().getColor(R.color.secondary_text));
            text.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));
            mAdditional.addView(text);
            text = new TextView(context);
            text.setText(res.getString(R.string.update_host, packages[i].getHost()));
            text.setTextSize(TypedValue.COMPLEX_UNIT_PX,
                    res.getDimension(R.dimen.card_textSize));
            text.setTextColor(getResources().getColor(R.color.secondary_text));
            text.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                    LayoutParams.WRAP_CONTENT));
            mAdditional.addView(text);
        }
    }

}