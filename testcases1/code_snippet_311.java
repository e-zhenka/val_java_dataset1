private static String localeToString(Locale locale) {
        if (locale != null) {
            return locale.toString();//locale.getDisplayName();
        } else {
            return "";
        }
    }