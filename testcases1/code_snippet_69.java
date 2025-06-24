private Page<TaskExecution> queryForPageableResults(Pageable pageable,
			String selectClause, String fromClause, String whereClause,
			MapSqlParameterSource queryParameters, long totalCount) {
		SqlPagingQueryProviderFactoryBean factoryBean = new SqlPagingQueryProviderFactoryBean();
		factoryBean.setSelectClause(selectClause);
		factoryBean.setFromClause(fromClause);
		if (StringUtils.hasText(whereClause)) {
			factoryBean.setWhereClause(whereClause);
		}
		final Sort sort = pageable.getSort();
		final LinkedHashMap<String, Order> sortOrderMap = new LinkedHashMap<>();

		if (sort != null) {
			for (Sort.Order sortOrder : sort) {
				sortOrderMap.put(sortOrder.getProperty(),
						sortOrder.isAscending() ? Order.ASCENDING : Order.DESCENDING);
			}
		}

		if (!CollectionUtils.isEmpty(sortOrderMap)) {
			factoryBean.setSortKeys(sortOrderMap);
		}
		else {
			factoryBean.setSortKeys(this.orderMap);
		}

		factoryBean.setDataSource(this.dataSource);
		PagingQueryProvider pagingQueryProvider;
		try {
			pagingQueryProvider = factoryBean.getObject();
			pagingQueryProvider.init(this.dataSource);
		}
		catch (Exception e) {
			throw new IllegalStateException(e);
		}
		String query = pagingQueryProvider.getPageQuery(pageable);
		List<TaskExecution> resultList = this.jdbcTemplate.query(getQuery(query),
				queryParameters, new TaskExecutionRowMapper());
		return new PageImpl<>(resultList, pageable, totalCount);
	}