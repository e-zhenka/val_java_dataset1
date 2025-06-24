public Script compile() throws CompilationFailedException {
        Binding binding = new Binding();
        binding.setVariable("falsePositive", falsePositive);
        GroovyShell shell = new GroovyShell(GroovySandbox.createSecureClassLoader(WarningsDescriptor.class.getClassLoader()),
                binding, GroovySandbox.createSecureCompilerConfiguration());
        return shell.parse(script);
    }