@RequirePOST
    public void doDisable(StaplerRequest req, StaplerResponse rsp) throws IOException {
        disable(true);
        rsp.sendRedirect2(req.getContextPath()+"/manage");
    }