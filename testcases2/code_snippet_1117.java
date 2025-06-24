protected boolean acceptableName(String name) {
        return isAccepted(name) && !isExcluded(name);
    }