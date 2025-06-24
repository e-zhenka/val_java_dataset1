protected BigInteger chooseRandomPrime(int bitlength, BigInteger e, BigInteger sqrdBound)
    {
        for (int i = 0; i != 5 * bitlength; i++)
        {
            BigInteger p = new BigInteger(bitlength, 1, param.getRandom());

            if (p.mod(e).equals(ONE))
            {
                continue;
            }

            if (p.multiply(p).compareTo(sqrdBound) < 0)
            {
                continue;
            }

            if (!isProbablePrime(p))
            {
                continue;
            }

            if (!e.gcd(p.subtract(ONE)).equals(ONE))
            {
                continue;
            }

            return p;
        }

        throw new IllegalStateException("unable to generate prime number for RSA key");
    }