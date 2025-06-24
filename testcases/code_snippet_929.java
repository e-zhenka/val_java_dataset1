public void testEncryptHeader() throws Exception {
        ENCRYPT.EncryptHeader hdr=new ENCRYPT.EncryptHeader((short)1, null);
        _testSize(hdr);
        hdr=new ENCRYPT.EncryptHeader((short)2, "Hello world");
        _testSize(hdr);
        EncryptHeader hdr2=new EncryptHeader((byte)1, new byte[]{'b','e', 'l', 'a'});
        _testSize(hdr2);
        hdr2=new EncryptHeader((byte)2, "Hello world".getBytes());
        _testSize(hdr2);
    }