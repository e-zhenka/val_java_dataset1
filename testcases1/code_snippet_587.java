public UnixUser authenticate(String username, String password) throws PAMException {
        this.password = password;
        try {
            check(libpam.pam_set_item(pht,PAM_USER,username),"pam_set_item failed");
            check(libpam.pam_authenticate(pht,0),"pam_authenticate failed");
            check(libpam.pam_setcred(pht,0),"pam_setcred failed");
            // several different error code seem to be used to represent authentication failures
//            check(libpam.pam_acct_mgmt(pht,0),"pam_acct_mgmt failed");

            PointerByReference r = new PointerByReference();
            check(libpam.pam_get_item(pht,PAM_USER,r),"pam_get_item failed");
            String userName = r.getValue().getString(0);
            passwd pwd = libc.getpwnam(userName);
            if(pwd==null)
                throw new PAMException("Authentication succeeded but no user information is available");
            return new UnixUser(userName,pwd);
        } finally {
            this.password = null;
        }
    }