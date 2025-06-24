@BeforeClass
  public static void setupClass() {
    functionStringMap.put(new AlterConnectionFunction(), "CLUSTER:MANAGE");
    functionStringMap.put(new AlterMappingFunction(), "CLUSTER:MANAGE");
    functionStringMap.put(new CreateConnectionFunction(), "CLUSTER:MANAGE");
    functionStringMap.put(new CreateMappingFunction(), "CLUSTER:MANAGE");
    functionStringMap.put(new DescribeConnectionFunction(), "CLUSTER:READ");
    functionStringMap.put(new DescribeMappingFunction(), "CLUSTER:READ");
    functionStringMap.put(new DestroyConnectionFunction(), "CLUSTER:MANAGE");
    functionStringMap.put(new DestroyMappingFunction(), "CLUSTER:MANAGE");
    functionStringMap.put(new ListConnectionFunction(), "CLUSTER:READ");
    functionStringMap.put(new ListMappingFunction(), "CLUSTER:READ");
    functionStringMap.put(new InheritsDefaultPermissionsJDBCFunction(), "CLUSTER:READ");
    functionStringMap.keySet().forEach(FunctionService::registerFunction);
  }