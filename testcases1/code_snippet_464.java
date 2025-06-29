@Override
    public boolean checkObjectExecutePermission(Class clazz, String methodName)
    {
        if (Class.class.isAssignableFrom(clazz) && methodName != null && this.secureClassMethods.contains(methodName)) {
            return true;
        } else {
            return super.checkObjectExecutePermission(clazz, methodName);
        }
    }