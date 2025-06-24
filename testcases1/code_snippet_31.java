@Override
    public void aroundWriteTo(WriterInterceptorContext context) throws IOException, WebApplicationException {
        String function = uri.getQueryParameters().getFirst(callbackQueryParameter);
        if (enabled && function != null && !function.trim().isEmpty() && !jsonpCompatibleMediaTypes.getPossible(context.getMediaType()).isEmpty()){

            OutputStreamWriter writer = new OutputStreamWriter(context.getOutputStream());

            if (wrapInTryCatch) writer.write("try{");
            writer.write(function + "(");
            writer.flush();

            // Disable the close method before calling context.proceed()
            OutputStream old = context.getOutputStream();
            DoNotCloseDelegateOutputStream wrappedOutputStream = new DoNotCloseDelegateOutputStream(old);
            context.setOutputStream(wrappedOutputStream);

            try {
                context.proceed();
                wrappedOutputStream.flush();
                writer.write(")");
                if (wrapInTryCatch) writer.write("}catch(e){}");
                writer.flush();
            } finally {
                context.setOutputStream(old);
            }
        } else {
            context.proceed();
        }
    }