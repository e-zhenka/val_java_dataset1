public static int versionCompare(String fromVersion, String toVersion) {
        String[] fromArr = fromVersion.split("\\.");
        String[] toArr = toVersion.split("\\.");
        int fromFirst = Integer.parseInt(fromArr[0]);
        int fromMiddle = Integer.parseInt(fromArr[1]);
        int fromEnd = Integer.parseInt(fromArr[2]);
        int toFirst = Integer.parseInt(toArr[0]);
        int toMiddle = Integer.parseInt(toArr[1]);
        int toEnd = Integer.parseInt(toArr[2]);
        if (fromFirst - toFirst != 0) {
            return fromFirst - toFirst;
        } else if (fromMiddle - toMiddle != 0) {
            return fromMiddle - toMiddle;
        } else {
            return fromEnd - toEnd;
        }
    }