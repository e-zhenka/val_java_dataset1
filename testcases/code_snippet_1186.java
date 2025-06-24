protected Key engineDoPhase(
        Key     key,
        boolean lastPhase) 
        throws InvalidKeyException, IllegalStateException
    {
        if (x == null)
        {
            throw new IllegalStateException("Diffie-Hellman not initialised.");
        }

        if (!(key instanceof DHPublicKey))
        {
            throw new InvalidKeyException("DHKeyAgreement doPhase requires DHPublicKey");
        }
        DHPublicKey pubKey = (DHPublicKey)key;

        if (!pubKey.getParams().getG().equals(g) || !pubKey.getParams().getP().equals(p))
        {
            throw new InvalidKeyException("DHPublicKey not for this KeyAgreement!");
        }

        BigInteger peerY = ((DHPublicKey)key).getY();
        if (peerY == null || peerY.compareTo(TWO) < 0
            || peerY.compareTo(p.subtract(ONE)) >= 0)
        {
            throw new InvalidKeyException("Invalid DH PublicKey");
        }

        result = peerY.modPow(x, p);
        if (result.compareTo(ONE) == 0)
        {
            throw new InvalidKeyException("Shared key can't be 1");
        }

        if (lastPhase)
        {
            return null;
        }

        return new BCDHPublicKey(result, pubKey.getParams());
    }