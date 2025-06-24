@Exported(inline=true)
    public synchronized Item[] getItems() {
        List<Item> r = new ArrayList<Item>();

        for(WaitingItem p : waitingList) {
            r = filterItemListBasedOnPermissions(r, p);
        }
        for (BlockedItem p : blockedProjects.values()){
            r = filterItemListBasedOnPermissions(r, p);
        }
        for (BuildableItem p : reverse(buildables.values())) {
            r = filterItemListBasedOnPermissions(r, p);
        }
        for (BuildableItem p : reverse(pendings.values())) {
            r= filterItemListBasedOnPermissions(r, p);
        }
        Item[] items = new Item[r.size()];
        r.toArray(items);
        return items;
    }