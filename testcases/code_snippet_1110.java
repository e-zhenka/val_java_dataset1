@Override
	protected File getFile(HandlerRequest<EmptyRequestBody, FileMessageParameters> handlerRequest) {
		if (logDir == null) {
			return null;
		}
		String filename = handlerRequest.getPathParameter(LogFileNamePathParameter.class);
		return new File(logDir, filename);
	}