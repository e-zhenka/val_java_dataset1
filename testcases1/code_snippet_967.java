@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		if (!(request instanceof HttpServletRequest) || !(response instanceof HttpServletResponse)
				|| getNodesCollector().isMonitoringDisabled()) {
			super.doFilter(request, response, chain);
			return;
		}
		final HttpServletRequest httpRequest = (HttpServletRequest) request;

		final String requestURI = httpRequest.getRequestURI();
		final String monitoringUrl = getMonitoringUrl(httpRequest);
		final String monitoringSlavesUrl = monitoringUrl + "/nodes";
		if (!PLUGIN_AUTHENTICATION_DISABLED
				&& (requestURI.equals(monitoringUrl) || requestURI.equals(monitoringSlavesUrl))) {
			// only the Hudson/Jenkins administrator can view the monitoring report
			Jenkins.getInstance().checkPermission(Jenkins.ADMINISTER);
		}

		if (requestURI.startsWith(monitoringSlavesUrl)) {
			final String nodeName;
			if (requestURI.equals(monitoringSlavesUrl)) {
				nodeName = null;
			} else {
				nodeName = requestURI.substring(monitoringSlavesUrl.length()).replace("/", "");
			}
			final HttpServletResponse httpResponse = (HttpServletResponse) response;
			doMonitoring(httpRequest, httpResponse, nodeName);
			return;
		}

		super.doFilter(request, response, chain);
	}