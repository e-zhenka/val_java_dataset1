protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response) {
		String targetUrl = null;
		HttpSession session = request.getSession(false);
		if (session != null && targetUrlSessionAttribute != null) {
			targetUrl = (String) session.getAttribute(targetUrlSessionAttribute);
			session.removeAttribute(targetUrlSessionAttribute);
		}

		if (isAlwaysUseDefaultTargetUrl() || !StringUtils.hasText(targetUrl) || (getTargetUrlParameter() != null && StringUtils.hasText(request.getParameter(getTargetUrlParameter())))) {
			targetUrl = super.determineTargetUrl(request, response);
		} else {
			logger.debug("Found targetUrlSessionAttribute in request: " + targetUrl);
		}

		// URL returned from determineTargetUrl() is resolved against the context path,
		// whereas the "from" URL is resolved against the top of the website, so adjust this.
		if (targetUrl.startsWith(request.getContextPath())) {
			targetUrl = targetUrl.substring(request.getContextPath().length());
		}

		if (!Util.isSafeToRedirectTo(targetUrl)) {
			logger.debug("Target URL is not safe to redirect to and will be ignored: " + targetUrl);
			targetUrl = getDefaultTargetUrl();
		}

		return targetUrl;
	}