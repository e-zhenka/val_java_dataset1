public QueryTargetPolicy getQueryTargetPolicyInstance() {
        if (queryTargetPolicyPlugin.get() == null) {
            queryTargetPolicyPlugin.instantiate(ReplicationPolicy.class,
                    this, true);
        }
        return (QueryTargetPolicy) queryTargetPolicyPlugin.get();
    }