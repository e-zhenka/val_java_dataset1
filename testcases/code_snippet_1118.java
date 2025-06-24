@Override
	@Exported(visibility = 2)
	public String getName() {
		return hudson.Util.escape(pageCounts.page);
	}