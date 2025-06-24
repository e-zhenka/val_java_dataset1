@Path("{id}")
    @PUT
    @Consumes(MediaType.APPLICATION_JSON)
    public Response updateUser(final @PathParam("id") String id, final UserRepresentation rep) {
        auth.requireManage();

        try {
            UserModel user = session.users().getUserById(id, realm);
            if (user == null) {
                throw new NotFoundException("User not found");
            }

             Set<String> attrsToRemove;
            if (rep.getAttributes() != null) {
                attrsToRemove = new HashSet<>(user.getAttributes().keySet());
                attrsToRemove.removeAll(rep.getAttributes().keySet());
            } else {
                attrsToRemove = Collections.emptySet();
            }

            if (rep.isEnabled() != null && rep.isEnabled()) {
                UserLoginFailureModel failureModel = session.sessions().getUserLoginFailure(realm, id);
                if (failureModel != null) {
                    failureModel.clearFailures();
                }
            }

            updateUserFromRep(user, rep, attrsToRemove, realm, session, true);
            adminEvent.operation(OperationType.UPDATE).resourcePath(uriInfo).representation(rep).success();

            if (session.getTransactionManager().isActive()) {
                session.getTransactionManager().commit();
            }
            return Response.noContent().build();
        } catch (ModelDuplicateException e) {
            return ErrorResponse.exists("User exists with same username or email");
        } catch (ModelReadOnlyException re) {
            return ErrorResponse.exists("User is read only!");
        } catch (ModelException me) {
            return ErrorResponse.exists("Could not update user!");
        } catch (Exception me) { // JPA may be committed by JTA which can't 
            return ErrorResponse.exists("Could not update user!");
        }
    }