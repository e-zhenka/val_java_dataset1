@Exported(name="jobs")
    public List<TopLevelItem> getItems() {
        List<TopLevelItem> viewableItems = new ArrayList<TopLevelItem>();
        for (TopLevelItem item : items.values()) {
            if (item.hasPermission(Item.READ))
                viewableItems.add(item);
        }

        return viewableItems;
    }