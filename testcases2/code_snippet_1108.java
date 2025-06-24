public List<Map<String, Object>> read(int headerRowIndex, int startRowIndex, int endRowIndex) {
		checkNotClosed();
		// 边界判断
		final int firstRowNum = sheet.getFirstRowNum();
		final int lastRowNum = sheet.getLastRowNum();
		if (headerRowIndex < firstRowNum) {
			throw new IndexOutOfBoundsException(StrUtil.format("Header row index {} is lower than first row index {}.", headerRowIndex, firstRowNum));
		} else if (headerRowIndex > lastRowNum) {
			throw new IndexOutOfBoundsException(StrUtil.format("Header row index {} is greater than last row index {}.", headerRowIndex, firstRowNum));
		}
		startRowIndex = Math.max(startRowIndex, firstRowNum);// 读取起始行（包含）
		endRowIndex = Math.min(endRowIndex, lastRowNum);// 读取结束行（包含）

		// 读取header
		List<Object> headerList = readRow(sheet.getRow(headerRowIndex));

		final List<Map<String, Object>> result = new ArrayList<>(endRowIndex - startRowIndex + 1);
		List<Object> rowList;
		for (int i = startRowIndex; i <= endRowIndex; i++) {
			if (i != headerRowIndex) {
				// 跳过标题行
				rowList = readRow(sheet.getRow(i));
				if (CollUtil.isNotEmpty(rowList) || false == ignoreEmptyRow) {
					if (null == rowList) {
						rowList = new ArrayList<>(0);
					}
					result.add(IterUtil.toMap(aliasHeader(headerList), rowList));
				}
			}
		}
		return result;
	}