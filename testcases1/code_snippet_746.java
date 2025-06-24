private static byte[] getRandomKey(List<String> dataRefURIs, Document doc, WSDocInfo wsDocInfo) throws WSSecurityException {
        try {
            String alg = "AES";
            int size = 128;
            if (!dataRefURIs.isEmpty()) {
                String uri = dataRefURIs.iterator().next();
                Element ee = ReferenceListProcessor.findEncryptedDataElement(doc, wsDocInfo, uri);
                String algorithmURI = X509Util.getEncAlgo(ee);
                alg = JCEMapper.getJCEKeyAlgorithmFromURI(algorithmURI);
                size = KeyUtils.getKeyLength(algorithmURI);
            }
            KeyGenerator kgen = KeyGenerator.getInstance(alg);
            kgen.init(size * 8);
            SecretKey k = kgen.generateKey();
            return k.getEncoded();
        } catch (Exception ex) {
            throw new WSSecurityException(WSSecurityException.ErrorCode.FAILED_CHECK, ex);
        }
    }