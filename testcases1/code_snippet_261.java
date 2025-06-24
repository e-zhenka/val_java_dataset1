@Override
		public void setPropertyValue(String propertyName, @Nullable Object value) throws BeansException {

			if (!isWritableProperty(propertyName)) {
				throw new NotWritablePropertyException(type, propertyName);
			}

			StandardEvaluationContext context = new StandardEvaluationContext();
			context.addPropertyAccessor(new PropertyTraversingMapAccessor(type, conversionService));
			context.setTypeConverter(new StandardTypeConverter(conversionService));
			context.setTypeLocator(typeName -> {
				throw new SpelEvaluationException(SpelMessage.TYPE_NOT_FOUND, typeName);
			});
			context.setRootObject(map);

			Expression expression = PARSER.parseExpression(propertyName);

			PropertyPath leafProperty = getPropertyPath(propertyName).getLeafProperty();
			TypeInformation<?> owningType = leafProperty.getOwningType();
			TypeInformation<?> propertyType = leafProperty.getTypeInformation();

			propertyType = propertyName.endsWith("]") ? propertyType.getActualType() : propertyType;

			if (propertyType != null && conversionRequired(value, propertyType.getType())) {

				PropertyDescriptor descriptor = BeanUtils.getPropertyDescriptor(owningType.getType(),
						leafProperty.getSegment());

				if (descriptor == null) {
					throw new IllegalStateException(String.format("Couldn't find PropertyDescriptor for %s on %s!",
							leafProperty.getSegment(), owningType.getType()));
				}

				MethodParameter methodParameter = new MethodParameter(descriptor.getReadMethod(), -1);
				TypeDescriptor typeDescriptor = TypeDescriptor.nested(methodParameter, 0);

				if (typeDescriptor == null) {
					throw new IllegalStateException(
							String.format("Couldn't obtain type descriptor for method parameter %s!", methodParameter));
				}

				value = conversionService.convert(value, TypeDescriptor.forObject(value), typeDescriptor);
			}

			try {
				expression.setValue(context, value);
			} catch (SpelEvaluationException o_O) {
				throw new NotWritablePropertyException(type, propertyName, "Could not write property!", o_O);
			}
		}