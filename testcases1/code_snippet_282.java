private static CloseableHttpClient getAllTrustClient(HttpHost proxy) {
			return HttpClients.custom()
					.setProxy(proxy)
					.setSslcontext(getSSLContext())
					.setHostnameVerifier(SSLConnectionSocketFactory.ALLOW_ALL_HOSTNAME_VERIFIER)
					.build();
		}