@RequirePOST
    public synchronized void doConfigure(StaplerRequest req, StaplerResponse rsp) throws IOException, ServletException, Descriptor.FormException {
        // for compatibility reasons, the actual value is stored in Jenkins
        BulkChange bc = new BulkChange(Jenkins.get());
        try{
            boolean result = configure(req, req.getSubmittedForm());
            LOGGER.log(Level.FINE, "security saved: "+result);
            Jenkins.get().save();
            FormApply.success(req.getContextPath()+"/manage").generateResponse(req, rsp, null);
        } finally {
            bc.commit();
        }
    }