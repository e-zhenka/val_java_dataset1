@Override
	public synchronized Resource findOne(String application, String profile, String label,
			String path) {

		if (StringUtils.hasText(path)) {
			String[] locations = this.service.getLocations(application, profile, label)
					.getLocations();
			ArrayList<Resource> locationResources = new ArrayList<>();
			for (int i = locations.length; i-- > 0;) {
				String location = locations[i];
				if (!PathUtils.isInvalidEncodedLocation(location)) {
					locationResources.add(this.resourceLoader.getResource(location));
				}
			}

			try {
				for (Resource location : locationResources) {
					for (String local : getProfilePaths(profile, path)) {
						if (!PathUtils.isInvalidPath(local)
								&& !PathUtils.isInvalidEncodedPath(local)) {
							Resource file = location.createRelative(local);
							if (file.exists() && file.isReadable() && PathUtils
									.checkResource(file, location, locationResources)) {
								return file;
							}
						}
					}
				}
			}
			catch (IOException e) {
				throw new NoSuchResourceException(
						"Error : " + path + ". (" + e.getMessage() + ")");
			}
		}
		throw new NoSuchResourceException("Not found: " + path);
	}