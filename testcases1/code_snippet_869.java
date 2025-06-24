protected String getRemoteAddr(RequestCycle requestCycle)
	{
		ServletWebRequest request = (ServletWebRequest)requestCycle.getRequest();
		return request.getContainerRequest().getRemoteAddr();
	}