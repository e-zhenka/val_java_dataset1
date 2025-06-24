@RequirePOST
    public void doConfigSubmit(StaplerRequest req, StaplerResponse rsp) throws IOException, ServletException {
        this.implications(req.bindJSONToList(
                Implication.class, req.getSubmittedForm().get("impl")
        ));
        rsp.sendRedirect("");
    }