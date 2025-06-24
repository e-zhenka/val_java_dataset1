public void performTest()
        throws Exception
    {
        byte[]  testIv = { 1, 2, 3, 4, 5, 6, 7, 8 };
        
        ASN1Encodable[]     values = {
            new CAST5CBCParameters(testIv, 128), 
            new NetscapeCertType(NetscapeCertType.smime),    
            new VerisignCzagExtension(new DERIA5String("hello")),
            new IDEACBCPar(testIv),        
            new NetscapeRevocationURL(new DERIA5String("http://test"))
        };
        
        byte[] data = Base64.decode("MA4ECAECAwQFBgcIAgIAgAMCBSAWBWhlbGxvMAoECAECAwQFBgcIFgtodHRwOi8vdGVzdA==");

        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        ASN1OutputStream aOut = new ASN1OutputStream(bOut);

        for (int i = 0; i != values.length; i++)
        {
            aOut.writeObject(values[i]);
        }

        ASN1Primitive[] readValues = new ASN1Primitive[values.length];

        if (!isSameAs(bOut.toByteArray(), data))
        {
            fail("Failed data check");
        }

        ByteArrayInputStream bIn = new ByteArrayInputStream(bOut.toByteArray());
        ASN1InputStream aIn = new ASN1InputStream(bIn);

        for (int i = 0; i != values.length; i++)
        {
            ASN1Primitive o = aIn.readObject();
            if (!values[i].equals(o))
            {
                fail("Failed equality test for " + o);
            }

            if (o.hashCode() != values[i].hashCode())
            {
                fail("Failed hashCode test for " + o);
            }
        }

        shouldFailOnExtraData();
    }