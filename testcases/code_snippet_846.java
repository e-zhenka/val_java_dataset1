public void testEncryptHeader() throws Exception {
        ENCRYPT.EncryptHeader hdr=new ENCRYPT.EncryptHeader((short)1, null);
        _testSize(hdr);
        hdr=new ENCRYPT.EncryptHeader((short)2, "Hello world");
        _testSize(hdr);
    }