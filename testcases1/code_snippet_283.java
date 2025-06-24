public Script compile() throws CompilationFailedException {
        Binding binding = new Binding();
        binding.setVariable("falsePositive", falsePositive);
        GroovyShell shell = new GroovyShell(WarningsDescriptor.class.getClassLoader(), binding);
        return shell.parse(script);
    }