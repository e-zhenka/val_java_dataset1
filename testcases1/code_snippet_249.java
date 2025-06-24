@Exported(inline=true)
    public Map<String/*monitor name*/,Object> getMonitorData() {
        Map<String,Object> r = new HashMap<String, Object>();
        if (hasPermission(CONNECT)) {
            for (NodeMonitor monitor : NodeMonitor.getAll())
                r.put(monitor.getClass().getName(), monitor.data(this));
        }
        return r;
    }