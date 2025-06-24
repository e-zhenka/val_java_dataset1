protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
		String targetUrl = null;
		HttpSession session = request.getSession(false);
		if (session != null && targetUrlSessionAttribute != null) {
			targetUrl = (String) session.getAttribute(targetUrlSessionAttribute);
			session.removeAttribute(targetUrlSessionAttribute);
		}

		if (isAlwaysUseDefaultTargetUrl() || !StringUtils.hasText(targetUrl) || (getTargetUrlParameter() != null && StringUtils.hasText(request.getParameter(getTargetUrlParameter())))) {
			return super.determineTargetUrl(request, response);
		}

		logger.debug("Found targetUrlSessionAttribute in request: " + targetUrl);

		// URL returned from determineTargetUrl() is resolved against the context path,
		// whereas the "from" URL is resolved against the top of the website, so adjust this.
		if (targetUrl.startsWith(request.getContextPath()))
			return targetUrl.substring(request.getContextPath().length());

		return targetUrl;
	}