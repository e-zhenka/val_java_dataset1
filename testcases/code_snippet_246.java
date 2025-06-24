@POST
    public void doConfigSubmit(StaplerRequest req, StaplerResponse rsp) throws IOException, ServletException {
        Jenkins.get().checkPermission(Jenkins.ADMINISTER);
        this.implications(req.bindJSONToList(
                Implication.class, req.getSubmittedForm().get("impl")
        ));
        rsp.sendRedirect("");
    }