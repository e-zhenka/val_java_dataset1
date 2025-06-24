public FormValidation doRegexCheck(@QueryParameter final String value)
            throws IOException, ServletException {
        // No permission needed for simple syntax check
        try {
            Pattern.compile(value);
            return FormValidation.ok();
        } catch (Exception ex) {
            return FormValidation.errorWithMarkup("Invalid <a href=\""
                    + "http://docs.oracle.com/javase/7/docs/api/java/util/regex/Pattern.html"
                    + "\">regular expression</a> (" + ex.getMessage() + ")");
        }
    }