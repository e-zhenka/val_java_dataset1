private void checkPrivileges(HiveOperationType hiveOpType, List<HivePrivilegeObject> hiveObjects,
                               String userName, Operation2Privilege.IOType ioType, List<String> deniedMessages) {

    if (hiveObjects == null) {
      return;
    }
    if (admins != null && Arrays.stream(admins).parallel().anyMatch(n -> n.equals(userName))) {
      return; // Skip rest of checks if user is admin
    }

    // Special-casing for ADMIN-level operations that do not require object checking.
    if (Operation2Privilege.isAdminPrivOperation(hiveOpType)) {
      // Require ADMIN privilege
      deniedMessages.add(SQLPrivTypeGrant.ADMIN_PRIV.toString() + " on " + ioType);
      return; // Ignore object, fail if not admin, succeed if admin.
    }

    boolean needAdmin = false;
    for (HivePrivilegeObject hiveObj : hiveObjects) {
      // If involving local file system
      if (hiveObj.getType() == HivePrivilegeObject.HivePrivilegeObjectType.LOCAL_URI) {
        needAdmin = true;
        break;
      }
    }
    if (!needAdmin) {
      switch (hiveOpType) {
        case ADD:
        case DFS:
        case COMPILE:
          needAdmin = true;
          break;
        default:
          break;
      }
    }
    if (needAdmin) {
      deniedMessages.add("ADMIN");
    }
  }