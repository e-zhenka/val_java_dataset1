private void doDsaTest(String sigName, BigInteger s, KeyFactory ecKeyFact, DSAPublicKeySpec pubKey, DSAPrivateKeySpec priKey)
        throws NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, InvalidKeySpecException, SignatureException
    {
        SecureRandom k = new TestRandomBigInteger(BigIntegers.asUnsignedByteArray(new BigInteger("72546832179840998877302529996971396893172522460793442785601695562409154906335")));

        byte[] M = Hex.decode("1BD4ED430B0F384B4E8D458EFF1A8A553286D7AC21CB2F6806172EF5F94A06AD");

        Signature dsa = Signature.getInstance(sigName, "BC");

        dsa.initSign(ecKeyFact.generatePrivate(priKey), k);

        dsa.update(M, 0, M.length);

        byte[] encSig = dsa.sign();

        ASN1Sequence sig = ASN1Sequence.getInstance(encSig);

        BigInteger r = new BigInteger("4864074fe30e6601268ee663440e4d9b703f62673419864e91e9edb0338ce510", 16);

        BigInteger sigR = ASN1Integer.getInstance(sig.getObjectAt(0)).getValue();
        if (!r.equals(sigR))
        {
            fail("r component wrong." + Strings.lineSeparator()
                + " expecting: " + r.toString(16) + Strings.lineSeparator()
                + " got      : " + sigR.toString(16));
        }

        BigInteger sigS = ASN1Integer.getInstance(sig.getObjectAt(1)).getValue();
        if (!s.equals(sigS))
        {
            fail("s component wrong." + Strings.lineSeparator()
                + " expecting: " + s.toString(16) + Strings.lineSeparator()
                + " got      : " + sigS.toString(16));
        }

        // Verify the signature
        dsa.initVerify(ecKeyFact.generatePublic(pubKey));

        dsa.update(M, 0, M.length);

        if (!dsa.verify(encSig))
        {
            fail("signature fails");
        }
    }