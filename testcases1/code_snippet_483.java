private void introspectInterfaces(Class<?> beanClass, Class<?> currClass) throws IntrospectionException {
		for (Class<?> ifc : currClass.getInterfaces()) {
			if (!ClassUtils.isJavaLanguageInterface(ifc)) {
				for (PropertyDescriptor pd : getBeanInfo(ifc).getPropertyDescriptors()) {
					PropertyDescriptor existingPd = this.propertyDescriptors.get(pd.getName());
					if (existingPd == null ||
							(existingPd.getReadMethod() == null && pd.getReadMethod() != null)) {
						// GenericTypeAwarePropertyDescriptor leniently resolves a set* write method
						// against a declared read method, so we prefer read method descriptors here.
						pd = buildGenericTypeAwarePropertyDescriptor(beanClass, pd);
						if (pd.getPropertyType() != null && (ClassLoader.class.isAssignableFrom(pd.getPropertyType())
								|| ProtectionDomain.class.isAssignableFrom(pd.getPropertyType()))) {
							// Ignore ClassLoader and ProtectionDomain types - nobody needs to bind to those
							continue;
						}
						this.propertyDescriptors.put(pd.getName(), pd);
					}
				}
				introspectInterfaces(ifc, ifc);
			}
		}
	}