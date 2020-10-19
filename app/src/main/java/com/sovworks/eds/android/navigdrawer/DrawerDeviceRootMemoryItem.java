package com.sovworks.eds.android.navigdrawer;


import android.content.Context;
import android.graphics.drawable.Drawable;

import com.sovworks.eds.android.R;
import com.sovworks.eds.locations.Location;

public class DrawerDeviceRootMemoryItem extends DrawerLocationMenuItem
{
    public DrawerDeviceRootMemoryItem(Location location, DrawerControllerBase drawerController)
    {
        super(location, drawerController);
    }

    @Override
    public Drawable getIcon()
    {
        return getIcon(getDrawerController().getMainActivity());
    }

    private synchronized static Drawable getIcon(Context context)
    {
        if(_icon == null)
        {
            _icon = context.getResources().getDrawable(R.drawable.ic_device_root_memory, context.getTheme());
        }
        return _icon;
    }

    private static Drawable _icon;
}
