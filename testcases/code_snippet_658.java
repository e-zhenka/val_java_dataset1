public static String resolvePath(String uri) {
		if (uri.isEmpty()) {
			return uri;
		}

		String path;
		if (uri.charAt(0) == '/') {
			path = uri;
			for (int i = 0; i < path.length(); i++) {
				char c = path.charAt(i);
				if (c == '?' || c == '#') {
					path = path.substring(0, i);
					break;
				}
			}
		}
		else {
			path = URI.create(uri).getPath();
		}
		if (!path.isEmpty()) {
			if (path.charAt(0) == '/') {
				path = path.substring(1);
				if (path.length() <= 1) {
					return path;
				}
			}
			if (path.charAt(path.length() - 1) == '/') {
				return path.substring(0, path.length() - 1);
			}
		}
		return path;
	}