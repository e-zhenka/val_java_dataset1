public Map<String, FileEntry> generatorCode(TableDetails tableDetails, String tablePrefix,
			Map<String, String> customProperties, List<TemplateFile> templateFiles) {

		Map<String, FileEntry> map = new HashMap<>(templateFiles.size());

		// 模板渲染
		Map<String, Object> context = GenUtils.getContext(tableDetails, tablePrefix, customProperties);

		for (TemplateFile templateFile : templateFiles) {
			FileEntry fileEntry = new FileEntry();
			fileEntry.setType(templateFile.getType());

			// 替换路径中的占位符
			String filename = StrUtil.format(templateFile.getFilename(), context);
			fileEntry.setFilename(filename);

			String parentFilePath = GenUtils.evaluateRealPath(templateFile.getParentFilePath(), context);
			fileEntry.setParentFilePath(parentFilePath);

			// 如果是文件
			if (TemplateEntryTypeEnum.FILE.getType().equals(fileEntry.getType())) {
				fileEntry.setFilePath(GenUtils.concatFilePath(parentFilePath, filename));
				// 文件内容渲染
				TemplateEngineTypeEnum engineTypeEnum = TemplateEngineTypeEnum.of(templateFile.getEngineType());
				String content = templateEngineDelegator.render(engineTypeEnum, templateFile.getContent(), context);
				fileEntry.setContent(content);
			}
			else {
				String currentPath = GenUtils.evaluateRealPath(templateFile.getFilename(), context);
				fileEntry.setFilePath(GenUtils.concatFilePath(parentFilePath, currentPath));
			}

			map.put(fileEntry.getFilePath(), fileEntry);
		}

		return map;
	}