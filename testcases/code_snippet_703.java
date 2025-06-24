@RequirePOST
    public void doLaunchSlaveAgent(StaplerRequest req, StaplerResponse rsp) throws IOException, ServletException {
        checkPermission(CONNECT);
            
        if(channel!=null) {
            req.getView(this,"already-launched.jelly").forward(req, rsp);
            return;
        }

        connect(true);

        // TODO: would be nice to redirect the user to "launching..." wait page,
        // then spend a few seconds there and poll for the completion periodically.
        rsp.sendRedirect("log");
    }