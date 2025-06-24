public static View create(StaplerRequest req, StaplerResponse rsp, ViewGroup owner)
            throws FormException, IOException, ServletException {
        String mode = req.getParameter("mode");

        String requestContentType = req.getContentType();
        if (requestContentType == null
                && !(mode != null && mode.equals("copy")))
            throw new Failure("No Content-Type header set");

        boolean isXmlSubmission = requestContentType != null
                && (requestContentType.startsWith("application/xml")
                        || requestContentType.startsWith("text/xml"));

        String name = req.getParameter("name");
        Jenkins.checkGoodName(name);
        if(owner.getView(name)!=null)
            throw new Failure(Messages.Hudson_ViewAlreadyExists(name));

        if (mode==null || mode.length()==0) {
            if(isXmlSubmission) {
                View v = createViewFromXML(name, req.getInputStream());
                owner.getACL().checkCreatePermission(owner, v.getDescriptor());
                v.owner = owner;
                rsp.setStatus(HttpServletResponse.SC_OK);
                return v;
            } else
                throw new Failure(Messages.View_MissingMode());
        }

        View v;
        if ("copy".equals(mode)) {
            v = copy(req, owner, name);
        } else {
            ViewDescriptor descriptor = all().findByName(mode);
            if (descriptor == null) {
                throw new Failure("No view type ‘" + mode + "’ is known");
            }

            // create a view
            JSONObject submittedForm = req.getSubmittedForm();
            submittedForm.put("name", name);
            v = descriptor.newInstance(req, submittedForm);
        }
        owner.getACL().checkCreatePermission(owner, v.getDescriptor());
        v.owner = owner;

        // redirect to the config screen
        rsp.sendRedirect2(req.getContextPath()+'/'+v.getUrl()+v.getPostConstructLandingPage());

        return v;
    }