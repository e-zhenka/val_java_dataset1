@BeforeClass
  public static void setupClass() {
    FunctionService.registerFunction(luceneCreateIndexFunction);
    FunctionService.registerFunction(luceneDescribeIndexFunction);
    FunctionService.registerFunction(luceneDestroyIndexFunction);
    FunctionService.registerFunction(luceneListIndexFunction);
    FunctionService.registerFunction(luceneSearchIndexFunction);
    FunctionService.registerFunction(dumpDirectoryFiles);
    FunctionService.registerFunction(luceneQueryFunction);
    FunctionService.registerFunction(waitUntilFlushedFunction);
    FunctionService.registerFunction(luceneGetPageFunction);
  }