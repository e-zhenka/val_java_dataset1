@BeforeClass
  public static void setupClass() {
    functionStringMap.put(new AlterConnectionFunction(), "*");
    functionStringMap.put(new AlterMappingFunction(), "*");
    functionStringMap.put(new CreateConnectionFunction(), "*");
    functionStringMap.put(new CreateMappingFunction(), "*");
    functionStringMap.put(new DescribeConnectionFunction(), "*");
    functionStringMap.put(new DescribeMappingFunction(), "*");
    functionStringMap.put(new DestroyConnectionFunction(), "*");
    functionStringMap.put(new DestroyMappingFunction(), "*");
    functionStringMap.put(new ListConnectionFunction(), "*");
    functionStringMap.put(new ListMappingFunction(), "*");
    functionStringMap.put(new InheritsDefaultPermissionsJDBCFunction(), "*");
    functionStringMap.keySet().forEach(FunctionService::registerFunction);
  }