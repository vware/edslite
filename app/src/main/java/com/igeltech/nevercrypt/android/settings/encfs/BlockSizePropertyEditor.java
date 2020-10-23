package com.igeltech.nevercrypt.android.settings.encfs;

import com.igeltech.nevercrypt.android.R;
import com.igeltech.nevercrypt.android.locations.fragments.CreateLocationFragment;
import com.igeltech.nevercrypt.android.locations.tasks.CreateEncFsTaskFragment;
import com.igeltech.nevercrypt.android.settings.IntPropertyEditor;

public class BlockSizePropertyEditor extends IntPropertyEditor
{
    public BlockSizePropertyEditor(CreateLocationFragment hostFragment)
    {
        super(
                hostFragment,
                R.string.block_size,
                R.string.block_size_descr,
                hostFragment.getTag()
        );
    }

    @Override
    protected int loadValue()
    {
        return getHostFragment().getState().getInt(CreateEncFsTaskFragment.ARG_BLOCK_SIZE, 1024);
    }

    @Override
    protected void saveValue(int value)
    {
        value -= value % 64;
        if(value < 64)
            value = 64;
        if(value > 4096)
            value = 4096;
        getHostFragment().getState().putInt(CreateEncFsTaskFragment.ARG_BLOCK_SIZE, value);
    }


    protected CreateLocationFragment getHostFragment()
    {
        return (CreateLocationFragment) getHost();
    }
}
