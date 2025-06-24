@BeforeClass
  public static void setupClass() {
    FunctionService.registerFunction(alterConnectionFunction);
    FunctionService.registerFunction(alterMappingFunction);
    FunctionService.registerFunction(createConnectionFunction);
    FunctionService.registerFunction(createMappingFunction);
    FunctionService.registerFunction(describeConnectionFunction);
    FunctionService.registerFunction(describeMappingFunction);
    FunctionService.registerFunction(destroyConnectionFunction);
    FunctionService.registerFunction(destroyMappingFunction);
    FunctionService.registerFunction(listConnectionFunction);
    FunctionService.registerFunction(listMappingFunction);
    FunctionService.registerFunction(inheritsDefaultPermissionsFunction);
  }