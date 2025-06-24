@Override
		protected void doGet(HttpServletRequest request, HttpServletResponse response)
				throws ServletException, IOException {
			String origin = request.getParameter("origin");
			if (origin == null) {
				response.setStatus(500);
				response.getWriter().println(
						"Required parameter 'origin' missing. Example: 107.20.175.135:7001");
				return;
			}
			origin = origin.trim();

			HttpGet httpget = null;
			InputStream is = null;
			boolean hasFirstParameter = false;
			StringBuilder url = new StringBuilder();
			if (!origin.startsWith("http")) {
				url.append("http://");
			}
			url.append(origin);
			if (origin.contains("?")) {
				hasFirstParameter = true;
			}
			Map<String, String[]> params = request.getParameterMap();
			for (String key : params.keySet()) {
				if (!key.equals("origin")) {
					String[] values = params.get(key);
					String value = values[0].trim();
					if (hasFirstParameter) {
						url.append("&");
					}
					else {
						url.append("?");
						hasFirstParameter = true;
					}
					url.append(key).append("=").append(value);
				}
			}
			String proxyUrl = url.toString();
			log.info("\n\nProxy opening connection to: " + proxyUrl + "\n\n");
			try {
				httpget = new HttpGet(proxyUrl);
				HttpClient client = ProxyConnectionManager.httpClient;
				HttpResponse httpResponse = client.execute(httpget);
				int statusCode = httpResponse.getStatusLine().getStatusCode();
				if (statusCode == HttpStatus.SC_OK) {
					// writeTo swallows exceptions and never quits even if outputstream is
					// throwing IOExceptions (such as broken pipe) ... since the
					// inputstream is infinite
					// httpResponse.getEntity().writeTo(new
					// OutputStreamWrapper(response.getOutputStream()));
					// so I copy it manually ...
					is = httpResponse.getEntity().getContent();

					// set headers
					copyHeadersToServletResponse(httpResponse.getAllHeaders(), response);

					// copy data from source to response
					OutputStream os = response.getOutputStream();
					int b = -1;
					while ((b = is.read()) != -1) {
						try {
							os.write(b);
							if (b == 10 /** flush buffer on line feed */
							) {
								os.flush();
							}
						}
						catch (Exception ex) {
							if (ex.getClass().getSimpleName()
									.equalsIgnoreCase("ClientAbortException")) {
								// don't throw an exception as this means the user closed
								// the connection
								log.debug(
										"Connection closed by client. Will stop proxying ...");
								// break out of the while loop
								break;
							}
							else {
								// received unknown error while writing so throw an
								// exception
								throw new RuntimeException(ex);
							}
						}
					}
				}
				else {
					log.warn("Failed opening connection to " + proxyUrl + " : "
							+ statusCode + " : " + httpResponse.getStatusLine());
				}
			}
			catch (Exception ex) {
				log.error("Error proxying request: " + url, ex);
			}
			finally {
				if (httpget != null) {
					try {
						httpget.abort();
					}
					catch (Exception ex) {
						log.error("failed aborting proxy connection.", ex);
					}
				}

				// httpget.abort() MUST be called first otherwise is.close() hangs
				// (because data is still streaming?)
				if (is != null) {
					// this should already be closed by httpget.abort() above
					try {
						is.close();
					}
					catch (Exception ex) {
						// ignore errors on close
					}
				}
			}

		}