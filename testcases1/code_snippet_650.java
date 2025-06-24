public T newInstance(@Nullable StaplerRequest req, @Nonnull JSONObject formData) throws FormException {
        try {
            Method m = getClass().getMethod("newInstance", StaplerRequest.class);

            if(!Modifier.isAbstract(m.getDeclaringClass().getModifiers())) {
                // this class overrides newInstance(StaplerRequest).
                // maintain the backward compatible behavior
                return verifyNewInstance(newInstance(req));
            } else {
                if (req==null) {
                    // yes, req is supposed to be always non-null, but see the note above
                    return verifyNewInstance(clazz.newInstance());
                }

                // new behavior as of 1.206
                BindInterceptor oldInterceptor = req.getBindInterceptor();
                try {
                    NewInstanceBindInterceptor interceptor;
                    if (oldInterceptor instanceof NewInstanceBindInterceptor) {
                        interceptor = (NewInstanceBindInterceptor) oldInterceptor;
                    } else {
                        interceptor = new NewInstanceBindInterceptor(oldInterceptor);
                        req.setBindInterceptor(interceptor);
                    }
                    interceptor.processed.put(formData, true);
                    return verifyNewInstance(req.bindJSON(clazz, formData));
                } finally {
                    req.setBindInterceptor(oldInterceptor);
                }
            }
        } catch (NoSuchMethodException e) {
            throw new AssertionError(e); // impossible
        } catch (InstantiationException | IllegalAccessException | RuntimeException e) {
            throw new Error("Failed to instantiate "+clazz+" from "+RedactSecretJsonInErrorMessageSanitizer.INSTANCE.sanitize(formData),e);
        }
    }