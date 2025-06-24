public boolean matches(String pattern, String source) {
        if (pattern == null) {
            throw new IllegalArgumentException("pattern argument cannot be null.");
        }
        Pattern p = Pattern.compile(pattern, caseInsensitive ? CASE_INSENSITIVE : DEFAULT);
        Matcher m = p.matcher(source);
        return m.matches();
    }