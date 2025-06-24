private boolean isInvalidEncodedPath(String resourcePath) {
		if (resourcePath.contains("%")) {
			// Use URLDecoder (vs UriUtils) to preserve potentially decoded UTF-8 chars...
			try {
				String decodedPath = URLDecoder.decode(resourcePath, "UTF-8");
				return (decodedPath.contains("../") || decodedPath.contains("..\\"));
			}
			catch (UnsupportedEncodingException ex) {
				// Should never happen...
			}
		}
		return false;
	}