@Override
	protected void convertInput()
	{
		final PolicyFactory policy = this.newPolicyFactory();
		final String html = this.textarea.getConvertedInput();

		this.setConvertedInput(policy.sanitize(html));
	}