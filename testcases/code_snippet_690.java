public static String resolvePath(String uri) {
		if (uri.isEmpty()) {
			return uri;
		}

		String path = URI.create(uri.charAt(0) == '/' ? "http://localhost:8080" + uri : uri)
		                 .getPath();
		if (!path.isEmpty()) {
			if (path.charAt(0) == '/') {
				path = path.substring(1);
				if (path.isEmpty()) {
					return path;
				}
			}
			if (path.charAt(path.length() - 1) == '/') {
				return path.substring(0, path.length() - 1);
			}
		}
		return path;
	}