public FormValidation doCheckCommand(@QueryParameter String value) {
            if(Util.fixEmptyAndTrim(value)==null)
                return FormValidation.error(Messages.CommandLauncher_NoLaunchCommand());
            else
                return FormValidation.ok();
        }