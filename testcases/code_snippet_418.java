public void execute(final FunctionContext context) {
    CliFunctionResult result = null;
    String memberId = context.getCache().getDistributedSystem().getDistributedMember().getId();
    try {
      LuceneDestroyIndexInfo indexInfo = (LuceneDestroyIndexInfo) context.getArguments();
      String indexName = indexInfo.getIndexName();
      String regionPath = indexInfo.getRegionPath();
      LuceneService service = LuceneServiceProvider.get(context.getCache());
      if (indexName == null) {
        if (indexInfo.isDefinedDestroyOnly()) {
          ((LuceneServiceImpl) service).destroyDefinedIndexes(regionPath);
          result = new CliFunctionResult(memberId);
        } else {
          service.destroyIndexes(regionPath);
          result = new CliFunctionResult(memberId, getXmlEntity(indexName, regionPath));
        }
      } else {
        if (indexInfo.isDefinedDestroyOnly()) {
          ((LuceneServiceImpl) service).destroyDefinedIndex(indexName, regionPath);
          result = new CliFunctionResult(memberId);
        } else {
          service.destroyIndex(indexName, regionPath);
          result = new CliFunctionResult(memberId, getXmlEntity(indexName, regionPath));
        }
      }
    } catch (Exception e) {
      result = new CliFunctionResult(memberId, e, e.getMessage());
    }
    context.getResultSender().lastResult(result);
  }