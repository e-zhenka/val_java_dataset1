protected Document getDocument(URL url) throws StageException {
		try {
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			addAuthHeader(conn);
			conn.setRequestProperty("Accept", "application/xml");
			int status = conn.getResponseCode();
			if (status == HttpURLConnection.HTTP_OK) {
				DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
				Document doc = builder.parse(conn.getInputStream());
				conn.disconnect();
				return doc;
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
		catch (ParserConfigurationException ex) {
			throw new StageException(ex);
		}
		catch (SAXException ex) {
			throw new StageException(ex);
		}

	}