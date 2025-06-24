public static boolean isSpringJspExpressionSupportActive(PageContext pageContext) {
		ServletContext sc = pageContext.getServletContext();
		String springJspExpressionSupport = sc.getInitParameter(EXPRESSION_SUPPORT_CONTEXT_PARAM);
		if (springJspExpressionSupport != null) {
			return Boolean.valueOf(springJspExpressionSupport);
		}
		if (sc.getMajorVersion() >= 3) {
			// We're on a Servlet 3.0+ container: Let's check what the application declares...
			if (sc.getEffectiveMajorVersion() == 2 && sc.getEffectiveMinorVersion() < 4) {
				// Application declares Servlet 2.3- in its web.xml: JSP 2.0 expressions not active.
				// Activate our own expression support.
				return true;
			}
		}
		return false;
	}