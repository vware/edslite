package com.igeltech.nevercrypt.fs.encfs.codecs.name;

import com.igeltech.nevercrypt.fs.encfs.DataCodecInfo;
import com.igeltech.nevercrypt.fs.encfs.NameCodec;
import com.igeltech.nevercrypt.fs.encfs.ciphers.BlockNameCipher;

public class BlockNameCodecInfo extends NameCodecInfoBase
{
    public static final String NAME = "nameio/block";

    @Override
    public NameCodec getEncDec()
    {
        DataCodecInfo dci = getConfig().getDataCodecInfo();
        return new BlockNameCipher(dci.getFileEncDec(), dci.getChecksumCalculator(), false);
    }

    @Override
    public String getName()
    {
        return NAME;
    }

    @Override
    public String getDescr()
    {
        return "Block: Block encoding, hides file name size somewhat";
    }

    @Override
    public int getVersion1()
    {
        return 4;
    }

    @Override
    public int getVersion2()
    {
        return 0;
    }

    @Override
    protected NameCodecInfoBase createNew()
    {
        return new BlockNameCodecInfo();
    }
}
