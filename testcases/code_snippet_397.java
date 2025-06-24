public FormValidation doRegexCheck(@QueryParameter final String value)
            throws IOException, ServletException {
        // No permission needed for simple syntax check
        try {
            Pattern.compile(value);
            return FormValidation.ok();
        } catch (Exception ex) {
            // SECURITY-1722: As the exception message will contain the user input Pattern,
            // it needs to be escaped to prevent an XSS attack
            return FormValidation.errorWithMarkup("Invalid <a href=\""
                    + "https://docs.oracle.com/javase/8/docs/api/java/util/regex/Pattern.html"
                    + "\">regular expression</a> (" + Util.escape(ex.getMessage()) + ")");
        }
    }