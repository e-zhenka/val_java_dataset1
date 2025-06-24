@Exported(inline=true)
    public synchronized Item[] getItems() {
        Item[] r = new Item[waitingList.size() + blockedProjects.size() + buildables.size() + pendings.size()];
        waitingList.toArray(r);
        int idx = waitingList.size();
        for (BlockedItem p : blockedProjects.values())
            r[idx++] = p;
        for (BuildableItem p : reverse(buildables.values()))
            r[idx++] = p;
        for (BuildableItem p : reverse(pendings.values()))
            r[idx++] = p;
        return r;
    }