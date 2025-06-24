public String createPackagesTable(final String serverUrl, final String authenticationToken, final String project) {
		List<String> packageNames = new ArrayList<String>();
		try {
			packageNames = RapidDeployConnector.invokeRapidDeployListPackages(authenticationToken, serverUrl, project);
		} catch (final Exception e) {
			logger.warn(e.getMessage());
		}
		if (!packageNames.isEmpty()) {
			final StringBuffer sb = new StringBuffer();
			sb.append("<table>");
			int index = 0;
			final int limit = 10;
			for (final String packageName : packageNames) {
				if (!"null".equals(packageName) && !packageName.startsWith("Deployment")) {
					sb.append("<tr><td class=\"setting-main\">");
					sb.append(Util.escape(packageName));
					sb.append("</td></tr>");
					index++;
					if (index >= limit) {
						break;
					}
				}
			}
			sb.append("</table>");
			return sb.toString();
		}
		return null;
	}