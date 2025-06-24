@Restricted(NoExternalUse.class)
        public FormValidation doCheckItemPattern(@QueryParameter String itemPattern) {
            try {

                Pattern.compile(itemPattern);
                return FormValidation.ok();
            } catch (PatternSyntaxException ex) {

                // Wrap exception message to <pre> tag as the error messages
                // uses position indicator (^) prefixed with spaces which work
                // with monospace fonts only.
                return FormValidation.error("Not a regular expression: " + ex.getMessage());
            }
        }