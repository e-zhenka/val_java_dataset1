public User getUser() {
            return userId == null
                    ? User.getUnknown()
                    : User.getById(userId, true)
            ;
        }