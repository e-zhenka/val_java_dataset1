@SuppressRestrictedWarnings(XMLUtils.class) // TODO remove when baseline > 2.179
	protected Document getDocument(URL url) throws StageException {
		try {
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			addAuthHeader(conn);
			conn.setRequestProperty("Accept", "application/xml");
			int status = conn.getResponseCode();
			if (status == HttpURLConnection.HTTP_OK) {
				try (InputStream is = conn.getInputStream(); InputStreamReader isr = new InputStreamReader(is, StandardCharsets.UTF_8)) {
					Document doc = XMLUtils.parse(isr);
					conn.disconnect();
					return doc;
				}
			}
			else {
				drainOutput(conn);
				if (status == HttpURLConnection.HTTP_UNAUTHORIZED) {
					throw new IOException("Incorrect username / password supplied.");
				}
				else if (status == HttpURLConnection.HTTP_NOT_FOUND) {
					throw new IOException("Document not found - is this a Nexus server?");
				}
				else {
					throw new IOException("Server returned error code " + status + " for " + url.toString());
				}
			}
		}
		catch (IOException ex) {
			throw createStageExceptionForIOException(nexusURL, ex);
		}
		catch (SAXException ex) {
			throw new StageException(ex);
		}

	}