default boolean contains(String name) {
        return get(name, Argument.OBJECT_ARGUMENT).isPresent();
    }