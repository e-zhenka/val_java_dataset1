@Override
    public AbstractBuild<?,?> resolveChild(Child child) {
        MatrixBuild b = (MatrixBuild)owner;
        return b.getRun(Combination.fromString(child.name));
    }