private static void languages( Connection c, Statement s)
	throws SQLException
	{
		Savepoint p = null;
		try
		{
			p = c.setSavepoint();
			s.execute(
				"CREATE TRUSTED LANGUAGE java HANDLER sqlj.java_call_handler");
			s.execute(
				"COMMENT ON LANGUAGE java IS '" +
				"Trusted/sandboxed language for routines and types in " +
				"Java; http://tada.github.io/pljava/'");
			c.releaseSavepoint(p);
		}
		catch ( SQLException sqle )
		{
			c.rollback(p);
			if ( ! "42710".equals(sqle.getSQLState()) )
				throw sqle;
		}

		try
		{
			p = c.setSavepoint();
			s.execute(
				"CREATE LANGUAGE javaU HANDLER sqlj.javau_call_handler");
			s.execute(
				"COMMENT ON LANGUAGE javau IS '" +
				"Untrusted/unsandboxed language for routines and types in " +
				"Java; http://tada.github.io/pljava/'");
			c.releaseSavepoint(p);
		}
		catch ( SQLException sqle )
		{
			c.rollback(p);
			if ( ! "42710".equals(sqle.getSQLState()) )
				throw sqle;
		}
	}