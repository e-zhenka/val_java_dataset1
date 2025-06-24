public String getDivUUID() {
        StringBuilder randomSelectName = new StringBuilder();
        randomSelectName.append(getName().replaceAll("\\W", "_")).append("-").append(uuid);
        return randomSelectName.toString();
    }