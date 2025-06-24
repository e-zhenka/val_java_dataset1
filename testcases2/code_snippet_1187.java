@Override
	public void convertInput()
	{
		final PolicyFactory policy = this.newPolicyFactory();
		final String input = this.textarea.getConvertedInput();

		this.setConvertedInput(policy.sanitize(input));
	}