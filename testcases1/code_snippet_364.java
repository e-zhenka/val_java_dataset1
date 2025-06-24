@Override
    public ScmServerEndpoint create(JSONObject request) {

        try {
            Jenkins.get().checkPermission(Item.CREATE);
        } catch (Exception e) {
            throw new ServiceException.ForbiddenException("User does not have permission to create repository", e);
        }

        List<ErrorMessage.Error> errors = new LinkedList<>();

        // Validate name
        final String name = (String) request.get(ScmServerEndpoint.NAME);
        if(StringUtils.isBlank(name)){
            errors.add(new ErrorMessage.Error(ScmServerEndpoint.NAME, ErrorMessage.Error.ErrorCodes.MISSING.toString(), ScmServerEndpoint.NAME + " is required"));
        }

        String url = (String) request.get(ScmServerEndpoint.API_URL);
        final BitbucketEndpointConfiguration endpointConfiguration = BitbucketEndpointConfiguration.get();
        if(StringUtils.isBlank(url)){
            errors.add(new ErrorMessage.Error(ScmServerEndpoint.API_URL, ErrorMessage.Error.ErrorCodes.MISSING.toString(), ScmServerEndpoint.API_URL + " is required"));
        }else {
            try {
                String version = BitbucketServerApi.getVersion(url);
                if (!BitbucketServerApi.isSupportedVersion(version)) {
                    errors.add(new ErrorMessage.Error(BitbucketServerEndpoint.API_URL, ErrorMessage.Error.ErrorCodes.INVALID.toString(),
                            Messages.bbserver_version_validation_error(
                                    version, BitbucketServerApi.MINIMUM_SUPPORTED_VERSION)));
                } else {
                    //validate presence of endpoint with same name
                    url = BitbucketEndpointConfiguration.normalizeServerUrl(url);
                    for (AbstractBitbucketEndpoint endpoint : endpointConfiguration.getEndpoints()) {
                        if (url.equals(endpoint.getServerUrl())) {
                            errors.add(new ErrorMessage.Error(ScmServerEndpoint.API_URL, ErrorMessage.Error.ErrorCodes.ALREADY_EXISTS.toString(), ScmServerEndpoint.API_URL + " already exists"));
                            break;
                        }
                    }
                }
            } catch (ServiceException e) {
                errors.add(new ErrorMessage.Error(BitbucketServerEndpoint.API_URL, ErrorMessage.Error.ErrorCodes.INVALID.toString(), StringUtils.isBlank(e.getMessage()) ? "Invalid URL" : e.getMessage()));
            }
        }

        if(!errors.isEmpty()){
            throw new ServiceException.BadRequestException(new ErrorMessage(400, "Failed to create Bitbucket server endpoint").addAll(errors));
        }
        final com.cloudbees.jenkins.plugins.bitbucket.endpoints.BitbucketServerEndpoint endpoint = new com.cloudbees.jenkins.plugins.bitbucket.endpoints.BitbucketServerEndpoint(name, url, false, null);
        SecurityContext old=null;
        try {
            // We need to escalate privilege to add user defined endpoint to
            old = ACL.impersonate(ACL.SYSTEM);
            endpointConfiguration.addEndpoint(endpoint);
        }finally {
            //reset back to original privilege level
            if(old != null){
                SecurityContextHolder.setContext(old);
            }
        }
        return new BitbucketServerEndpoint(endpoint, this);
    }