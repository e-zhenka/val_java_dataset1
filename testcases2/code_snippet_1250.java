@Override
	protected File getFile(HandlerRequest<EmptyRequestBody, FileMessageParameters> handlerRequest) {
		if (logDir == null) {
			return null;
		}
		// wrapping around another File instantiation is a simple way to remove any path information - we're
		// solely interested in the filename
		String filename = new File(handlerRequest.getPathParameter(LogFileNamePathParameter.class)).getName();
		return new File(logDir, filename);
	}