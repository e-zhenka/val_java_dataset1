public void doDisable(StaplerRequest req, StaplerResponse rsp) throws IOException {
        Jenkins.getInstance().checkPermission(Jenkins.ADMINISTER);
        disable(true);
        rsp.sendRedirect2(req.getContextPath()+"/manage");
    }