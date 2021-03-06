package com.igeltech.nevercrypt.crypto.modes;

import com.igeltech.nevercrypt.crypto.BlockCipherNative;
import com.igeltech.nevercrypt.crypto.CipherFactory;
import com.igeltech.nevercrypt.crypto.EncryptionEngine;
import com.igeltech.nevercrypt.crypto.EncryptionEngineException;
import com.igeltech.nevercrypt.crypto.SecureBuffer;

import java.util.ArrayList;
import java.util.Arrays;

public abstract class CTR implements EncryptionEngine
{
    static
    {
        System.loadLibrary("cryptctr");
    }

    protected final CipherFactory _cf;
    protected final ArrayList<BlockCipherNative> _blockCiphers = new ArrayList<>();
    protected byte[] _iv;
    protected byte[] _key;
    private long _ctrContextPointer;

    protected CTR(CipherFactory cf)
    {
        _cf = cf;
    }

    @Override
    public synchronized void init() throws EncryptionEngineException
    {
        closeCiphers();
        closeContext();
        _ctrContextPointer = initContext();
        if (_ctrContextPointer == 0)
            throw new EncryptionEngineException("CTR context initialization failed");
        addBlockCiphers(_cf);
        if (_key == null)
            throw new EncryptionEngineException("Encryption key is not set");
        int keyOffset = 0;
        for (BlockCipherNative p : _blockCiphers)
        {
            int ks = p.getKeySize();
            byte[] tmp = new byte[ks];
            try
            {
                System.arraycopy(_key, keyOffset, tmp, 0, ks);
                p.init(tmp);
                attachNativeCipher(_ctrContextPointer, p.getNativeInterfacePointer());
            }
            finally
            {
                Arrays.fill(tmp, (byte) 0);
            }
            keyOffset += ks;
        }
    }

    @Override
    public byte[] getIV()
    {
        return _iv;
    }

    @Override
    public void setIV(byte[] iv)
    {
        _iv = iv;
    }

    @Override
    public int getIVSize()
    {
        return 16;
    }

    @Override
    public int getKeySize()
    {
        int res = 0;
        for (BlockCipherNative c : _blockCiphers)
            res += c.getKeySize();
        return res;
    }

    public void close()
    {
        closeCiphers();
        closeContext();
        clearAll();
    }

    @Override
    public void encrypt(byte[] data, int offset, int len) throws EncryptionEngineException
    {
        if (_ctrContextPointer == 0)
            throw new EncryptionEngineException("Engine is closed");
        if (len == 0)
            return;
        if ((offset + len) > data.length)
            throw new IllegalArgumentException("Wrong length or offset");
        if (encrypt(data, offset, len, _iv, _ctrContextPointer) != 0)
            throw new EncryptionEngineException("Failed encrypting data");
    }

    @Override
    public void decrypt(byte[] data, int offset, int len) throws EncryptionEngineException
    {
        if (_ctrContextPointer == 0)
            throw new EncryptionEngineException("Engine is closed");
        if (len == 0)
            return;
        if ((offset + len) > data.length)
            throw new IllegalArgumentException("Wrong length or offset");
        if (decrypt(data, offset, len, _iv, _ctrContextPointer) != 0)
            throw new EncryptionEngineException("Failed decrypting data");
    }

    @Override
    public byte[] getKey()
    {
        return _key;
    }

    @Override
    public void setKey(byte[] key)
    {
        clearKey();
        _key = key == null ? null : Arrays.copyOf(key, getKeySize());
    }

    @Override
    public String getCipherModeName()
    {
        return "ctr-plain";
    }

    protected void closeCiphers()
    {
        for (BlockCipherNative p : _blockCiphers)
            p.close();
        _blockCiphers.clear();
    }

    protected void closeContext()
    {
        if (_ctrContextPointer != 0)
        {
            closeContext(_ctrContextPointer);
            _ctrContextPointer = 0;
        }
    }

    private native long initContext();

    private native void closeContext(long contextPointer);

    private native void attachNativeCipher(long contextPointer, long nativeCipherInterfacePointer);

    private native int encrypt(byte[] data, int offset, int len, byte[] iv, long contextPointer);

    private native int decrypt(byte[] data, int offset, int len, byte[] iv, long contextPointer);

    private void addBlockCiphers(CipherFactory cipherFactory)
    {
        for (int i = 0; i < cipherFactory.getNumberOfCiphers(); i++)
            _blockCiphers.add(cipherFactory.createCipher(i));
    }

    private void clearAll()
    {
        clearKey();
    }

    private void clearKey()
    {
        if (_key != null)
        {
            SecureBuffer.eraseData(_key);
            _key = null;
        }
    }
}

    