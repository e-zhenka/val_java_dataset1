private static boolean contains(final String[] list, String name) {
            if (list != null) {
                for (final String white : list) {
                    if ("*".equals(white) || name.startsWith(white)) {
                        return true;
                    }
                }
            }
            return false;
        }