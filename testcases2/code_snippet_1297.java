public static String compileMustache(Map<String, Object> context, String template) {
		if (context == null || StringUtils.isBlank(template)) {
			return "";
		}
		Writer writer = new StringWriter();
		try {
			Mustache.compiler().escapeHTML(false).emptyStringIsFalse(true).compile(template).execute(context, writer);
		} finally {
			try {
				writer.close();
			} catch (IOException e) {
				logger.error(null, e);
			}
		}
		return writer.toString();
	}