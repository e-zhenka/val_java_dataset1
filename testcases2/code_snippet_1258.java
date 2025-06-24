@Override
  public AccessControlList getAccessControlList(String mediaPackageId) throws NotFoundException,
  SearchServiceDatabaseException {
    EntityManager em = null;
    try {
      em = emf.createEntityManager();
      SearchEntity entity = getSearchEntity(mediaPackageId, em);
      if (entity == null) {
        throw new NotFoundException("Could not found media package with ID " + mediaPackageId);
      }
      if (entity.getAccessControl() == null) {
        return null;
      } else {
        return AccessControlParser.parseAcl(entity.getAccessControl());
      }
    } catch (NotFoundException e) {
      throw e;
    } catch (Exception e) {
      logger.error("Could not retrieve ACL {}: {}", mediaPackageId, e.getMessage());
      throw new SearchServiceDatabaseException(e);
    } finally {
      em.close();
    }
  }