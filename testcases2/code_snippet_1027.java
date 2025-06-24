private void doInclude(HttpServletRequest request, HttpServletResponse response, String resourceUrl)
		throws ServletException, IOException {

		if (this.contentType != null) {
			response.setContentType(this.contentType);
		}
		String[] resourceUrls =
			StringUtils.tokenizeToStringArray(resourceUrl, RESOURCE_URL_DELIMITERS);
		for (int i = 0; i < resourceUrls.length; i++) {
			// check whether URL matches allowed resources
			if (this.allowedResources != null && !this.pathMatcher.match(this.allowedResources, resourceUrls[i])) {
				throw new ServletException("Resource [" + resourceUrls[i] +
						"] does not match allowed pattern [" + this.allowedResources + "]");
			}
			if (logger.isDebugEnabled()) {
				logger.debug("Including resource [" + resourceUrls[i] + "]");
			}
			RequestDispatcher rd = request.getRequestDispatcher(resourceUrls[i]);
			rd.include(request, response);
		}
	}