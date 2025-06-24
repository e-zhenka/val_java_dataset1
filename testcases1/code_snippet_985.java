private boolean isInvalidEncodedPath(String resourcePath) {
		if (resourcePath.contains("%")) {
			// Use URLDecoder (vs UriUtils) to preserve potentially decoded UTF-8 chars...
			try {
				String decodedPath = URLDecoder.decode(resourcePath, "UTF-8");
				if (decodedPath.contains("../") || decodedPath.contains("..\\")) {
					if (logger.isTraceEnabled()) {
						logger.trace("Ignoring invalid resource path with escape sequences [" + resourcePath + "]");
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