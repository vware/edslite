package com.igeltech.nevercrypt.crypto;

public interface FileEncryptionEngine extends EncryptionEngine
{
    /**
     * Returns encryption sector size
     * @return encryption sector size
     */
    int getFileBlockSize();

    int getEncryptionBlockSize();

    void setIncrementIV(boolean val);
}
