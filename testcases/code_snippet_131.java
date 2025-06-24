private static String getIdFromToken(Element token) {
        if (token != null) {
            // Try to find the "Id" on the token.
            if (token.hasAttributeNS(WSConstants.WSU_NS, "Id")) {
                return token.getAttributeNS(WSConstants.WSU_NS, "Id");
            } else if (token.hasAttributeNS(null, "ID")) {
                return token.getAttributeNS(null, "ID");
            } else if (token.hasAttributeNS(null, "AssertionID")) {
                return token.getAttributeNS(null, "AssertionID");
            }
        }
        return "";
    }