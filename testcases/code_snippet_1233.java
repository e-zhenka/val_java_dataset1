private static boolean contains(final String[] list, final String name) {
        if (list != null) {
            for (final String white : list) {
                if (name.startsWith(white)) {
                    return true;
                }
            }
        }
        return false;
    }