package com.igeltech.nevercrypt.android.settings;

import android.content.Context;

import java.io.File;

public abstract class SystemConfigCommon extends com.igeltech.nevercrypt.settings.SystemConfig
{
    protected final Context _context;

    public SystemConfigCommon(Context context)
    {
        _context = context;
    }

    @Override
    public File getTmpFolderPath()
    {
        return _context.getFilesDir();
    }

    @Override
    public File getCacheFolderPath()
    {
        return _context.getCacheDir();
    }

    @Override
    public File getPrivateExecFolderPath()
    {
        return _context.getFilesDir();
    }

    @Override
    public File getFSMFolderPath()
    {
        return new File(_context.getFilesDir(), "fsm");
    }
}
