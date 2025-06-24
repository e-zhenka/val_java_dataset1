public Map<String, FileEntry> generatorCode(TableDetails tableDetails, String tablePrefix,
			Map<String, String> customProperties, List<TemplateFile> templateFiles) {

		Map<String, FileEntry> map = new HashMap<>(templateFiles.size());

		// 模板渲染
		Map<String, Object> context = GenUtils.getContext(tableDetails, tablePrefix, customProperties);

		for (TemplateFile templateFile : templateFiles) {
			FileEntry fileEntry = new FileEntry();
			fileEntry.setType(templateFile.getType());

			// 替换路径中的占位符
			String templateFilename = templateFile.getFilename();
			String filename = StrUtil.format(templateFilename, context);
			fileEntry.setFilename(filename);

			String parentFilePath = GenUtils.evaluateRealPath(templateFile.getParentFilePath(), context);
			fileEntry.setParentFilePath(parentFilePath);

			// 如果是文件
			if (TemplateEntryTypeEnum.FILE.getType().equals(fileEntry.getType())) {
				String filePath = GenUtils.concatFilePath(parentFilePath, filename);
				fileEntry.setFilePath(filePath);
				// 文件内容渲染
				TemplateEngineTypeEnum engineTypeEnum = TemplateEngineTypeEnum.of(templateFile.getEngineType());

				try {
					String content = templateEngineDelegator.render(engineTypeEnum, templateFile.getContent(), context);
					fileEntry.setContent(content);
				}
				catch (TemplateRenderException ex) {
					String errorMessage = StrUtil.format("模板渲染异常，模板文件名：【{}】，错误详情：{}", templateFilename,
							ex.getMessage());
					throw new BusinessException(SystemResultCode.SERVER_ERROR.getCode(), errorMessage);
				}
			}
			else {
				String currentPath = GenUtils.evaluateRealPath(templateFilename, context);
				fileEntry.setFilePath(GenUtils.concatFilePath(parentFilePath, currentPath));
			}

			map.put(fileEntry.getFilePath(), fileEntry);
		}

		return map;
	}