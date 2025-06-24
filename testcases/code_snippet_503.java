protected TlsSecret safeDecryptPreMasterSecret(TlsCryptoParameters cryptoParams, PrivateKey rsaServerPrivateKey,
                                                   byte[] encryptedPreMasterSecret)
    {
        SecureRandom secureRandom = crypto.getSecureRandom();

        /*
         * RFC 5246 7.4.7.1.
         */
        ProtocolVersion clientVersion = cryptoParams.getClientVersion();

        // TODO Provide as configuration option?
        boolean versionNumberCheckDisabled = false;

        /*
         * Generate 48 random bytes we can use as a Pre-Master-Secret, if the
         * PKCS1 padding check should fail.
         */
        byte[] fallback = new byte[48];
        secureRandom.nextBytes(fallback);

        byte[] M = Arrays.clone(fallback);
        try
        {
            Cipher c = crypto.createRSAEncryptionCipher();
            c.init(Cipher.DECRYPT_MODE, rsaServerPrivateKey);
            M = c.doFinal(encryptedPreMasterSecret);
        }
        catch (Exception e)
        {
            /*
             * A TLS server MUST NOT generate an alert if processing an
             * RSA-encrypted premaster secret message fails, or the version number is not as
             * expected. Instead, it MUST continue the handshake with a randomly generated
             * premaster secret.
             */
        }

        /*
         * If ClientHello.client_version is TLS 1.1 or higher, server implementations MUST
         * check the version number [..].
         */
        if (versionNumberCheckDisabled && clientVersion.isEqualOrEarlierVersionOf(ProtocolVersion.TLSv10))
        {
            /*
             * If the version number is TLS 1.0 or earlier, server
             * implementations SHOULD check the version number, but MAY have a
             * configuration option to disable the check.
             *
             * So there is nothing to do here.
             */
        }
        else
        {
            /*
             * OK, we need to compare the version number in the decrypted Pre-Master-Secret with the
             * clientVersion received during the handshake. If they don't match, we replace the
             * decrypted Pre-Master-Secret with a random one.
             */
            int correct = (clientVersion.getMajorVersion() ^ (M[0] & 0xff))
                | (clientVersion.getMinorVersion() ^ (M[1] & 0xff));
            correct |= correct >> 1;
            correct |= correct >> 2;
            correct |= correct >> 4;
            int mask = ~((correct & 1) - 1);

            /*
             * mask will be all bits set to 0xff if the version number differed.
             */
            for (int i = 0; i < 48; i++)
            {
                M[i] = (byte)((M[i] & (~mask)) | (fallback[i] & mask));
            }
        }
        return crypto.createSecret(M);
    }