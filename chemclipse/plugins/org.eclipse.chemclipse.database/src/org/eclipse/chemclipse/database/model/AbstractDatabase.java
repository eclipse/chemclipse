/*******************************************************************************
 * Copyright (c) 2013, 2015 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Dr. Janos Binder - more implementation
 *******************************************************************************/
package org.eclipse.chemclipse.database.model;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.database.documents.ILibraryDescriptionDocument;
import org.eclipse.chemclipse.database.documents.LibraryDescriptionDocument;
import org.eclipse.chemclipse.database.exceptions.NoDatabaseAvailableException;
import org.eclipse.chemclipse.database.support.DatabasePathHelper;
import org.eclipse.chemclipse.support.settings.IOperatingSystemUtils;
import org.eclipse.chemclipse.support.settings.OperatingSystemUtils;

import com.orientechnologies.orient.core.db.OPartitionedDatabasePool;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.record.impl.ODocument;
import com.orientechnologies.orient.core.sql.query.OSQLSynchQuery;

public abstract class AbstractDatabase implements IDatabase {

	//
	public static final String DEFAULT_USER_NAME = "admin";
	public static final String DEFAULT_USER_PASSWORD = "admin";
	//
	/*
	 * This map stores the requested database instances.
	 * It prevents that too many connections will be opened.
	 */
	private static Map<String, OPartitionedDatabasePool> databases;
	//
	private ODatabaseDocumentTx db;
	private OPartitionedDatabasePool ownerPool;
	private int maxNumberOfConnections = 64; // 640k is ought to be enough for everybody

	public AbstractDatabase(String database, String user, String password) throws NoDatabaseAvailableException {

		/*
		 * Initialize the database map if neccessary.
		 */
		if(databases == null) {
			databases = new HashMap<String, OPartitionedDatabasePool>();
		}
		/*
		 * local: or remote:
		 */
		String key = user + "@" + database; // database is the url plocal://... or remote://...
		ownerPool = databases.get(key);
		if(ownerPool == null) {
			if(DatabasePathHelper.isRemoteDatabasePath(database)) {
				/*
				 * Remote
				 */
				ownerPool = new OPartitionedDatabasePool(database, user, password, maxNumberOfConnections);
				/*
				 * If no error occurred, add the database to the hash map.
				 */
				databases.put(key, ownerPool);
			} else if(DatabasePathHelper.isLocalDatabasePath(database)) {
				/*
				 * Local (Optimized DB path)
				 */
				ownerPool = new OPartitionedDatabasePool(getOptimizedLocalDatabasePath(database), user, password, maxNumberOfConnections);
				/*
				 * If no error occurred, add the database to the hash map.
				 */
				databases.put(key, ownerPool);
			} else {
				throw new NoDatabaseAvailableException("The database location is malformed: " + database);
			}
		}
		/*
		 * If null, create a new database connection.
		 */
		if(db == null) {
			/*
			 * Remote and local are handled differently.
			 */
			try {
				db = ownerPool.acquire();
			} catch(Exception e) {
				throw new NoDatabaseAvailableException("The database connection couldn't be established: " + database);
			}
		}
	}

	/**
	 * The char "/" is replaced by "$". Otherwise, local databases with the equal
	 * names but different locations can't be separated. E.g.:
	 * 
	 * converts
	 * local:/home/user/.chemclipse/0.8.0/org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse/DefaultDB
	 * to
	 * local:$home$user$.chemclipse$0.8.0$org.eclipse.chemclipse.chromatogram.msd.quantitation.supplier.chemclipse$DefaultDB
	 * 
	 * @param database
	 * @return String
	 */
	public static String getOptimizedLocalDatabasePath(String database) {

		IOperatingSystemUtils operatingSystemUtils = new OperatingSystemUtils();
		/*
		 * Windows and others are treated separately.
		 */
		if(operatingSystemUtils.isWindows()) {
			return database.replaceAll("\\\\", "\\$");
		} else {
			return database.replaceAll("/", "\\$");
		}
	}

	@Override
	protected void finalize() throws Throwable {

		if(db != null) {
			db.close();
		}
		super.finalize();
	}

	@Override
	public ODatabaseDocumentTx getDB() {

		/*
		 * If the connection has been closed, reopen it.
		 */
		if(db != null && db.isClosed()) {
			db = ownerPool.acquire();
		}
		return db;
	}

	@Override
	public void close() {

		if(db != null && !db.isClosed()) {
			// databases.remove(db);
			db.close();
		}
	}

	// ----------------------------------------------------------------------------------
	/**
	 * Selects a Document in the current database by its class name and id.
	 * 
	 * @param className
	 * @param id
	 * @return ODocument
	 */
	protected ODocument queryDocumentById(String className, long id) {

		return queryDocumentByIdOnSeparateConnection(className, id);
		/*
		 * If you uncomment it "Current database instance (com.orientechnologies.orient.core.db.OPartitionedDatabasePool is not active on current thread" exceptions occur.
		 * It happens since OrientDB 2.1.2 when the ConcurrentLinkedHashMap was replaced by Guava.
		 */
		// return queryDocumentById(className, id, getDB());
	}

	/**
	 * Selects a Document in the current database by its class name and id.
	 * 
	 * @param className
	 * @param id
	 * @return ODocument
	 */
	protected ODocument queryDocumentById(String className, long id, ODatabaseDocumentTx db) {

		if(countCluster(className.toLowerCase()) == 0) {
			return null;
		} else {
			/*
			 * Create the query
			 */
			int cluster = db.getClusterIdByName(className);
			StringBuilder query = new StringBuilder();
			query.append("SELECT FROM #");
			query.append(cluster);
			query.append(":");
			query.append(id);
			/*
			 * Execute
			 */
			List<ODocument> results = null;
			results = getDB().query(new OSQLSynchQuery<ODocument>(query.toString()));
			if(results.size() == 1) {
				return results.get(0);
			} else {
				return null;
			}
		}
	}

	// ----------------------------------------------------------------------------------
	/**
	 * Selects a Document in the current database on a separate connection by its class name and id.
	 * Really handy in multithreaded/multicore environments
	 * 
	 * @param className
	 * @param id
	 * @return ODocument
	 */
	protected ODocument queryDocumentByIdOnSeparateConnection(String className, long id) {

		ODatabaseDocumentTx separatedb = ownerPool.acquire();
		ODocument result = queryDocumentById(className, id, separatedb);
		separatedb.close();
		return result;
	}

	@Override
	public long countCluster(String clusterName) {

		return countClusterOnSeparateConnection(clusterName);
		/*
		 * If you uncomment it "Current database instance (com.orientechnologies.orient.core.db.OPartitionedDatabasePool is not active on current thread" exceptions occur.
		 * It happens since OrientDB 2.1.2 when the ConcurrentLinkedHashMap was replaced by Guava.
		 */
		// return countCluster(clusterName, getDB());
	}

	@Override
	public long countClusterOnSeparateConnection(String clusterName) {

		ODatabaseDocumentTx separatedb = ownerPool.acquire();
		long result = countCluster(clusterName, separatedb);
		separatedb.close();
		return result;
	}

	private long countCluster(String clusterName, ODatabaseDocumentTx dbtx) {

		long result = 0;
		try {
			result = dbtx.countClusterElements(clusterName);
		} catch(IllegalArgumentException e) {
			// Should we make it nice when the cluster does not exist? Rethrow the exception?
			// logger.warn(e);
		}
		return result;
	}

	@Override
	public ILibraryDescriptionDocument getLibraryDescriptionDocument() {

		ODocument document = queryDocumentById(ILibraryDescriptionDocument.CLASS_NAME, 0);
		if(document == null) {
			return null;
		} else {
			return new LibraryDescriptionDocument(document);
		}
	}

	@Override
	public ILibraryDescriptionDocument getLibraryDescriptionDocumentOnSeparateConnection() {

		ODocument document = queryDocumentByIdOnSeparateConnection(ILibraryDescriptionDocument.CLASS_NAME, 0);
		if(document == null) {
			return null;
		} else {
			return new LibraryDescriptionDocument(document);
		}
	}
}
