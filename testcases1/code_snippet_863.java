@Override
    public AbstractBuild<?,?> resolveChild(Child child) {
        MatrixBuild b = (MatrixBuild)owner;
        return b.getProject().getItem(Combination.fromString(child.name)).getBuildByNumber(child.build);
    }