public Object extractValue(String strVal)
   {
      if (strVal == null)
      {
         if (defaultValue == null)
         {
            //System.out.println("NO DEFAULT VALUE");
            if (!StringToPrimitive.isPrimitive(baseType)) return null;
            else
               return StringToPrimitive.stringToPrimitiveBoxType(baseType, strVal);
         }
         else
         {
            strVal = defaultValue;
            //System.out.println("DEFAULT VAULUE: " + strVal);
         }
      }
      if (paramConverter != null)
      {
         try {
            return paramConverter.fromString(strVal);
         } catch (Exception pce) {
            throwProcessingException(Messages.MESSAGES.unableToExtractParameter(
                    getParamSignature(), strVal, target), pce);
         }
      }
      if (unmarshaller != null)
      {
         try {
         return unmarshaller.fromString(strVal);
         } catch (Exception ue) {
            throwProcessingException(Messages.MESSAGES.unableToExtractParameter(
                    getParamSignature(), strVal, target), ue);
         }
      }
      else if (delegate != null)
      {
         try {
            return delegate.fromString(strVal);
         } catch (Exception pce) {
            throwProcessingException(Messages.MESSAGES.unableToExtractParameter(
                    getParamSignature(), strVal, target), pce);
         }
      }
      else if (constructor != null)
      {
         try
         {
            return constructor.newInstance(strVal);
         }
         catch (InstantiationException e)
         {
            throwProcessingException(Messages.MESSAGES.unableToExtractParameter(getParamSignature(), _encode(strVal), target), e);
         }
         catch (IllegalAccessException e)
         {
            throwProcessingException(Messages.MESSAGES.unableToExtractParameter(getParamSignature(), _encode(strVal), target), e);
         }
         catch (InvocationTargetException e)
         {
            Throwable targetException = e.getTargetException();
            if (targetException instanceof WebApplicationException)
            {
               throw ((WebApplicationException)targetException);
            }
            throwProcessingException(Messages.MESSAGES.unableToExtractParameter(getParamSignature(), _encode(strVal), target), targetException);
         }
      }
      else if (valueOf != null)
      {
         try
         {
            return valueOf.invoke(null, strVal);
         }
         catch (IllegalAccessException e)
         {
            throwProcessingException(Messages.MESSAGES.unableToExtractParameter(getParamSignature(), _encode(strVal), target), e);
         }
         catch (InvocationTargetException e)
         {
            Throwable targetException = e.getTargetException();
            if (targetException instanceof WebApplicationException)
            {
               throw ((WebApplicationException)targetException);
            }
            throwProcessingException(Messages.MESSAGES.unableToExtractParameter(getParamSignature(), _encode(strVal), target), targetException);
         }
      }
      try
      {
         if (StringToPrimitive.isPrimitive(baseType)) return StringToPrimitive.stringToPrimitiveBoxType(baseType, strVal);
      }
      catch (Exception e)
      {
         throwProcessingException(Messages.MESSAGES.unableToExtractParameter(getParamSignature(), _encode(strVal), target), e);
      }
      return null;
   }