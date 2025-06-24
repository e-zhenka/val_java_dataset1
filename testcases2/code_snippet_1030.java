public static String resolvePath(String uri) {
		String path = URI.create(uri).getPath();
		if (!path.isEmpty()) {
			if(path.charAt(0) == '/'){
				path = path.substring(1);
				if(path.length() <= 1){
					return path;
				}
			}
			if(path.charAt(path.length() - 1) == '/'){
				return path.substring(0, path.length() - 1);
			}
		}
		return path;
	}