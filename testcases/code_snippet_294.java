public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Map<String, Object> map = new HashMap<String, Object>(model);
		String path = ServletUriComponentsBuilder.fromContextPath(request).build()
				.getPath();
		map.put("path", (Object) path==null ? "" : path);
		context.setRootObject(map);
		String result = helper.replacePlaceholders(template, resolver);
		response.setContentType(getContentType());
		response.getWriter().append(result);
	}