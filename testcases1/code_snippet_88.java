@Override
		public void setPropertyValue(String propertyName, Object value) throws BeansException {

			if (!isWritableProperty(propertyName)) {
				throw new NotWritablePropertyException(type, propertyName);
			}

			StandardEvaluationContext context = new StandardEvaluationContext();
			context.addPropertyAccessor(new PropertyTraversingMapAccessor(type, conversionService));
			context.setTypeConverter(new StandardTypeConverter(conversionService));
			context.setTypeLocator(REJECTING_LOCATOR);
			context.setRootObject(map);

			Expression expression = PARSER.parseExpression(propertyName);

			PropertyPath leafProperty = getPropertyPath(propertyName).getLeafProperty();
			TypeInformation<?> owningType = leafProperty.getOwningType();
			TypeInformation<?> propertyType = owningType.getProperty(leafProperty.getSegment());

			propertyType = propertyName.endsWith("]") ? propertyType.getActualType() : propertyType;

			if (conversionRequired(value, propertyType.getType())) {

				PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(owningType.getType(),
						leafProperty.getSegment());
				MethodParameter methodParameter = new MethodParameter(descriptor.getReadMethod(), -1);
				TypeDescriptor typeDescriptor = TypeDescriptor.nested(methodParameter, 0);

				value = conversionService.convert(value, TypeDescriptor.forObject(value), typeDescriptor);
			}

			try {
				expression.setValue(context, value);
			} catch (SpelEvaluationException o_O) {
				throw new NotWritablePropertyException(type, propertyName, "Could not write property!", o_O);
			}
		}