package com.igeltech.nevercrypt.fs.encfs.ciphers;

import com.igeltech.nevercrypt.crypto.EncryptionEngine;
import com.igeltech.nevercrypt.crypto.EncryptionEngineException;
import com.igeltech.nevercrypt.crypto.FileEncryptionEngine;

public class BlockAndStreamCipher implements FileEncryptionEngine
{
    private final FileEncryptionEngine _blockCipher;
    private final EncryptionEngine _streamCipher;

    public BlockAndStreamCipher(FileEncryptionEngine blockCipher, EncryptionEngine streamCipher)
    {
        _blockCipher = blockCipher;
        _streamCipher = streamCipher;
    }

    @Override
    public int getFileBlockSize()
    {
        return _blockCipher.getFileBlockSize();
    }

    @Override
    public int getEncryptionBlockSize()
    {
        return _blockCipher.getEncryptionBlockSize();
    }

    @Override
    public void setIncrementIV(boolean val)
    {
        _blockCipher.setIncrementIV(val);
    }

    @Override
    public void init() throws EncryptionEngineException
    {
        _blockCipher.init();
        _streamCipher.init();
    }

    @Override
    public void decrypt(byte[] data, int offset, int len) throws EncryptionEngineException
    {
        if (len == _blockCipher.getFileBlockSize())
            _blockCipher.decrypt(data, offset, len);
        else
            _streamCipher.decrypt(data, offset, len);
    }

    @Override
    public void encrypt(byte[] data, int offset, int len) throws EncryptionEngineException
    {
        if (len == _blockCipher.getFileBlockSize())
            _blockCipher.encrypt(data, offset, len);
        else
            _streamCipher.encrypt(data, offset, len);
    }

    @Override
    public byte[] getIV()
    {
        return _streamCipher.getIV();
    }

    @Override
    public void setIV(byte[] iv)
    {
        _blockCipher.setIV(iv);
        _streamCipher.setIV(iv);
    }

    @Override
    public int getIVSize()
    {
        return _blockCipher.getIVSize();
    }

    @Override
    public byte[] getKey()
    {
        return _blockCipher.getKey();
    }

    @Override
    public void setKey(byte[] key)
    {
        _blockCipher.setKey(key);
        _streamCipher.setKey(key);
    }

    @Override
    public int getKeySize()
    {
        return _blockCipher.getKeySize();
    }

    @Override
    public void close()
    {
        _blockCipher.close();
        _streamCipher.close();
    }

    @Override
    public String getCipherName()
    {
        return _blockCipher.getCipherName();
    }

    @Override
    public String getCipherModeName()
    {
        return _blockCipher.getCipherModeName();
    }
}
