private boolean isInvalidEncodedPath(String resourcePath) {
		if (resourcePath.contains("%")) {
			// Use URLDecoder (vs UriUtils) to preserve potentially decoded UTF-8 chars...
			try {
				String decodedPath = URLDecoder.decode(resourcePath, "UTF-8");
				int separatorIndex = decodedPath.indexOf("..") + 2;
				if (separatorIndex > 1 && separatorIndex < decodedPath.length()) {
					char separator = decodedPath.charAt(separatorIndex);
					if (separator == '/' || separator == '\\') {
						if (logger.isTraceEnabled()) {
							logger.trace("Resolved resource path contains \"../\" after decoding: " + resourcePath);
						}
					}
					return true;
				}
			}
			catch (UnsupportedEncodingException ex) {
				// Should never happen...
			}
		}
		return false;
	}