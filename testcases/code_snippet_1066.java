public String getPasswordValue(Object o) {
        if (o==null)    return null;
        if (o instanceof Secret) {
            StaplerRequest req = Stapler.getCurrentRequest();
            if (req != null) {
                Item item = req.findAncestorObject(Item.class);
                if (item != null && !item.hasPermission(Item.CONFIGURE)) {
                    return "<some secret>";
                }
            }
            return ((Secret) o).getEncryptedValue();
        }
        if (getIsUnitTest()) {
            throw new SecurityException("attempted to render plaintext ‘" + o + "’ in password field; use a getter of type Secret instead");
        }
        return o.toString();
    }