private void introspectInterfaces(Class<?> beanClass, Class<?> currClass, Set<String> readMethodNames)
			throws IntrospectionException {

		for (Class<?> ifc : currClass.getInterfaces()) {
			if (!ClassUtils.isJavaLanguageInterface(ifc)) {
				for (PropertyDescriptor pd : getBeanInfo(ifc).getPropertyDescriptors()) {
					PropertyDescriptor existingPd = this.propertyDescriptors.get(pd.getName());
					if (existingPd == null ||
							(existingPd.getReadMethod() == null && pd.getReadMethod() != null)) {
						// GenericTypeAwarePropertyDescriptor leniently resolves a set* write method
						// against a declared read method, so we prefer read method descriptors here.
						pd = buildGenericTypeAwarePropertyDescriptor(beanClass, pd);
						this.propertyDescriptors.put(pd.getName(), pd);
						Method readMethod = pd.getReadMethod();
						if (readMethod != null) {
							readMethodNames.add(readMethod.getName());
						}
					}
				}
				introspectInterfaces(ifc, ifc, readMethodNames);
			}
		}
	}