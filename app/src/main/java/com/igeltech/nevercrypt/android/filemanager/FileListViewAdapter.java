package com.igeltech.nevercrypt.android.filemanager;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import com.igeltech.nevercrypt.android.R;
import com.igeltech.nevercrypt.android.filemanager.records.BrowserRecord;
import com.igeltech.nevercrypt.android.helpers.ExtendedFileInfoLoader;

public class FileListViewAdapter extends ArrayAdapter<BrowserRecord>
{
    private String _currentLocationId;

    public FileListViewAdapter(Context context)
    {
        super(context.getApplicationContext(), R.layout.fs_browser_row);
    }

    public void setCurrentLocationId(String locationId)
    {
        _currentLocationId = locationId;
    }

    @Override
    public int getItemViewType(int position)
    {
        BrowserRecord rec = getItem(position);
        return rec == null ? 0 : rec.getViewType();
    }

    @Override
    public int getViewTypeCount()
    {
        return 2;
    }

    @NonNull
    @Override
    public View getView(int position, View convertView, @NonNull ViewGroup parent)
    {
        BrowserRecord rec = getItem(position);
        if (rec == null)
            return new View(getContext());
        if (rec.needLoadExtendedInfo() && _currentLocationId != null)
            ExtendedFileInfoLoader.getInstance().requestExtendedInfo(_currentLocationId, rec);
        View v;
        if (convertView != null)
        {
            v = convertView;
            rec.updateView(v, position);
        }
        else
            v = rec.createView(position, parent);
        v.setTag(rec);
        return v;
    }
}
