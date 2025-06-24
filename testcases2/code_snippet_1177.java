public String getDivUUID() {
        StringBuilder randomSelectName = new StringBuilder();
        randomSelectName.append(getName()).append("-").append(uuid);
        return randomSelectName.toString();
    }