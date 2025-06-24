@Override
	public void convertInput()
	{
		super.convertInput();

		final PolicyFactory policy = this.newPolicyFactory();
		final String input = this.getConvertedInput();

		this.setConvertedInput(policy.sanitize(input));
	}