@BeforeClass
  public static void setupClass() {
    functionStringMap.put(new LuceneCreateIndexFunction(), "CLUSTER:MANAGE:LUCENE");
    functionStringMap.put(new LuceneDescribeIndexFunction(), "CLUSTER:READ:LUCENE");
    functionStringMap.put(new LuceneDestroyIndexFunction(), "CLUSTER:MANAGE:LUCENE");
    functionStringMap.put(new LuceneListIndexFunction(), "CLUSTER:READ:LUCENE");
    functionStringMap.put(new LuceneSearchIndexFunction(), "DATA:READ:testRegion");
    functionStringMap.put(new LuceneQueryFunction(), "DATA:READ:testRegion");
    functionStringMap.put(new WaitUntilFlushedFunction(), "DATA:READ:testRegion");
    functionStringMap.put(new LuceneGetPageFunction(), "DATA:READ:testRegion");

    functionStringMap.keySet().forEach(FunctionService::registerFunction);
    FunctionService.registerFunction(new DumpDirectoryFiles());
  }