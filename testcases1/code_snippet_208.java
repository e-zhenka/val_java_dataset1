private static String getIdFromToken(Element token) {
        if (token != null) {
            // For SAML tokens get the ID/AssertionID
            if ("Assertion".equals(token.getLocalName())
                && WSConstants.SAML2_NS.equals(token.getNamespaceURI())) {
                return token.getAttributeNS(null, "ID");
            } else if ("Assertion".equals(token.getLocalName())
                && WSConstants.SAML_NS.equals(token.getNamespaceURI())) {
                return token.getAttributeNS(null, "AssertionID");
            }

            // For UsernameTokens get the username
            if (WSConstants.USERNAME_TOKEN_LN.equals(token.getLocalName())
                && WSConstants.WSSE_NS.equals(token.getNamespaceURI())) {
                Element usernameElement =
                    XMLUtils.getDirectChildElement(token, WSConstants.USERNAME_LN, WSConstants.WSSE_NS);
                if (usernameElement != null) {
                    return XMLUtils.getElementText(usernameElement);
                }
            }

            // For BinarySecurityTokens take the hash of the value
            if (WSConstants.BINARY_TOKEN_LN.equals(token.getLocalName())
                && WSConstants.WSSE_NS.equals(token.getNamespaceURI())) {
                String text = XMLUtils.getElementText(token);
                if (text != null && !"".equals(text)) {
                    try {
                        MessageDigest digest = MessageDigest.getInstance("SHA-256");
                        byte[] bytes = digest.digest(text.getBytes());
                        return Base64.getMimeEncoder().encodeToString(bytes);
                    } catch (NoSuchAlgorithmException e) {
                        // SHA-256 must be supported so not going to happen...
                    }
                }
            }
        }
        return "";
    }