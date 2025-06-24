protected String getRemoteAddr(RequestCycle requestCycle)
	{
		ServletWebRequest request = (ServletWebRequest)requestCycle.getRequest();
		HttpServletRequest req = request.getContainerRequest();
		String remoteAddr = request.getHeader("X-Forwarded-For");

		if (remoteAddr != null)
		{
			if (remoteAddr.contains(","))
			{
				// sometimes the header is of form client ip,proxy 1 ip,proxy 2 ip,...,proxy n ip,
				// we just want the client
				remoteAddr = Strings.split(remoteAddr, ',')[0].trim();
			}
			try
			{
				// If ip4/6 address string handed over, simply does pattern validation.
				InetAddress.getByName(remoteAddr);
			}
			catch (UnknownHostException e)
			{
				remoteAddr = req.getRemoteAddr();
			}
		}
		else
		{
			remoteAddr = req.getRemoteAddr();
		}
		return remoteAddr;
	}