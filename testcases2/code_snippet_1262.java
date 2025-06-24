@POST
        public FormValidation doCheckDriver(@QueryParameter String value) {
            Jenkins.get().checkPermission(Jenkins.ADMINISTER);
            
            if (value.length()==0)
                return FormValidation.ok(); // no value typed yet.

            try {
                getClassLoader().loadClass(value);
                return FormValidation.ok();
            } catch (ClassNotFoundException e) {
                return FormValidation.error("No such class: "+value);
            }
        }