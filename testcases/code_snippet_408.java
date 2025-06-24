protected UserModel validateCache(RealmModel realm, CachedUser cached) {
        if (!realm.getId().equals(cached.getRealm())) {
            return null;
        }

        StorageId storageId = new StorageId(cached.getId());
        if (!storageId.isLocal()) {
            ComponentModel component = realm.getComponent(storageId.getProviderId());
            UserStorageProviderModel model = new UserStorageProviderModel(component);
            UserStorageProviderModel.CachePolicy policy = model.getCachePolicy();
            // although we do set a timeout, Infinispan has no guarantees when the user will be evicted
            // its also hard to test stuff
            boolean invalidate = false;
            if (policy != null) {
                String currentTime = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL).format(new Date(Time.currentTimeMillis()));
                if (policy == UserStorageProviderModel.CachePolicy.NO_CACHE) {
                    invalidate = true;
                } else if (cached.getCacheTimestamp() < model.getCacheInvalidBefore()) {
                    invalidate = true;
                } else if (policy == UserStorageProviderModel.CachePolicy.EVICT_DAILY) {
                    long dailyTimeout = dailyTimeout(model.getEvictionHour(), model.getEvictionMinute());
                    dailyTimeout = dailyTimeout - (24 * 60 * 60 * 1000);
                    //String timeout = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL).format(new Date(dailyTimeout));
                    //String stamp = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL).format(new Date(cached.getCacheTimestamp()));
                    if (cached.getCacheTimestamp() <= dailyTimeout) {
                        invalidate = true;
                    }
                } else if (policy == UserStorageProviderModel.CachePolicy.EVICT_WEEKLY) {
                    int oneWeek = 7 * 24 * 60 * 60 * 1000;
                    long weeklyTimeout = weeklyTimeout(model.getEvictionDay(), model.getEvictionHour(), model.getEvictionMinute());
                    long lastTimeout = weeklyTimeout - oneWeek;
                    String timeout = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL).format(new Date(weeklyTimeout));
                    String stamp = DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL).format(new Date(cached.getCacheTimestamp()));
                    if (cached.getCacheTimestamp() <= lastTimeout) {
                        invalidate = true;
                    }
                }
            }
            if (invalidate) {
                registerUserInvalidation(realm, cached);
                return getDelegate().getUserById(cached.getId(), realm);
            }
        }
        return new UserAdapter(cached, this, session, realm);
    }