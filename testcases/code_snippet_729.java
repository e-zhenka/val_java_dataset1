@Override
    public boolean willAttributeDistribute(String name, Object value) {
        Pattern sessionAttributeNamePattern = getSessionAttributeNamePattern();
        if (sessionAttributeNamePattern == null) {
            return true;
        }
        return sessionAttributeNamePattern.matcher(name).matches();
    }