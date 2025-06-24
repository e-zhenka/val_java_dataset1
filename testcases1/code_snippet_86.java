@Override
	protected void onInitialize()
	{
		super.onInitialize();

		this.textarea = new TextArea<String>("textarea", this.getModel());
		this.textarea.setEscapeModelStrings(false);
		this.add(this.textarea.setOutputMarkupId(true));

		this.add(JQueryWidget.newWidgetBehavior(this, this.container));
	}