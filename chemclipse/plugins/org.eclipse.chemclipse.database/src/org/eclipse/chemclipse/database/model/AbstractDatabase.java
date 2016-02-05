/*******************************************************************************
 * Copyright (c) 2013, 2016 Dr. Philip Wenig.
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

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.eclipse.chemclipse.database.documents.ILibraryDescriptionDocument;
import org.eclipse.chemclipse.database.documents.ISettingsDocument;
import org.eclipse.chemclipse.database.documents.LibraryDescriptionDocument;
import org.eclipse.chemclipse.database.documents.SettingsDocument;
import org.eclipse.chemclipse.database.exceptions.NoDatabaseAvailableException;
import org.eclipse.chemclipse.database.support.DatabasePathHelper;
import org.eclipse.chemclipse.support.settings.IOperatingSystemUtils;
import org.eclipse.chemclipse.support.settings.OperatingSystemUtils;

import com.orientechnologies.orient.core.db.OPartitionedDatabasePool;
import com.orientechnologies.orient.core.db.document.ODatabaseDocumentTx;
import com.orientechnologies.orient.core.intent.OIntent;
import com.orientechnologies.orient.core.intent.OIntentMassiveRead;
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
	private int maxNumberOfConnections = 512; // 640k is ought to be enough for everybody

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
	final protected ODocument queryDocumentById(String className, long id) {

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
	 * @param dbtx
	 * @return ODocument
	 */
	final protected ODocument queryDocumentById(String className, long id, ODatabaseDocumentTx dbtx) {

		if(countCluster(className.toLowerCase()) == 0) {
			return null;
		} else {
			/*
			 * Create the query
			 */
			int cluster = dbtx.getClusterIdByName(className);
			StringBuilder query = new StringBuilder();
			query.append("SELECT FROM #");
			query.append(cluster);
			query.append(":");
			query.append(id);
			/*
			 * Execute
			 */
			List<ODocument> results = null;
			results = dbtx.query(new OSQLSynchQuery<ODocument>(query.toString()));
			if(results.size() == 1) {
				return results.get(0);
			} else {
				return null;
			}
		}
	}

	/**
	 * Selects a Document in the current database by its class name and id.
	 * 
	 * @param className
	 * @param id
	 * @param dbtx
	 * @param queryStrings
	 * @return ODocument
	 */
	final protected ODocument queryDocumentById(String className, long id, ODatabaseDocumentTx dbtx, String... queryStrings) {

		if(countCluster(className.toLowerCase()) == 0) {
			return null;
		} else {
			/*
			 * Create the query
			 */
			int cluster = dbtx.getClusterIdByName(className);
			StringBuilder query = new StringBuilder();
			query.append("SELECT FROM #");
			query.append(cluster);
			query.append(":");
			query.append(id);
			for(String s : queryStrings) {
				query.append(s);
			}
			/*
			 * Execute
			 */
			List<ODocument> results = null;
			results = dbtx.query(new OSQLSynchQuery<ODocument>(query.toString()));
			if(results.size() == 1) {
				return results.get(0);
			} else {
				return null;
			}
		}
	}

	final protected List<ODocument> queryDocumentsByRecordIds(String className, Set<Long> ids) {

		return queryDocumentsByRecordIds(className, new OIntentMassiveRead(), ids);
	}

	final protected List<ODocument> queryDocumentsByRecordIds(String className, OIntent intent, Set<Long> ids) {

		// List<ODocument> result = null;
		// try (ODatabaseDocumentTx separatedb = ownerPool.acquire()) {
		// result = queryDocumentsByRecordIds(className, separatedb, intent, ids);
		// }
		List<ODocument> result = queryDocumentsByRecordIds(className, getDB(), intent, ids);
		return result;
	}

	final protected List<ODocument> queryDocumentsByRecordIds(String className, ODatabaseDocumentTx dbtx, OIntent intent, Set<Long> ids) {

		if(countCluster(className.toLowerCase()) > 0) {
			/*
			 * Converting to [#cluster:id1, #cluster:id2 ...]
			 */
			int cluster = dbtx.getClusterIdByName(className);
			StringBuilder queryBuilder = new StringBuilder();
			queryBuilder.append("SELECT FROM [");
			Iterator<Long> idIterator = ids.iterator();
			while(idIterator.hasNext()) {
				queryBuilder.append("#");
				queryBuilder.append(cluster);
				queryBuilder.append(":");
				queryBuilder.append(idIterator.next());
				if(idIterator.hasNext()) {
					queryBuilder.append(", ");
				}
			}
			queryBuilder.append("]");
			String queryString = queryBuilder.toString();
			/*
			 * Supporting intent
			 */
			if(intent != null) {
				dbtx.declareIntent(intent);
			}
			List<ODocument> documents = dbtx.query(new OSQLSynchQuery<ODocument>(queryString));
			/*
			 * WORKAROUND to bypass local cache
			 */
			List<ODocument> returnDocuments = new ArrayList<ODocument>();
			for(ODocument document : documents) {
				ODocument reloadedDocument = dbtx.load(document.getIdentity(), null, true);
				returnDocuments.add(reloadedDocument);
			}
			if(intent != null) {
				dbtx.declareIntent(null);
			}
			return returnDocuments;
		} else {
			return Collections.<ODocument> emptyList();
		}
	}

	final protected List<ODocument> queryDocumentsByClassName(String className, String... queryStrings) {

		/*
		 * Assume by default that we read a lot of stuff
		 */
		return queryDocumentsByClassName(className, new OIntentMassiveRead(), queryStrings);
	}

	final protected List<ODocument> queryDocumentsByClassName(String className, OIntent intent, String... queryStrings) {

		// List<ODocument> result = null;
		// try (ODatabaseDocumentTx separatedb = ownerPool.acquire()) {
		// result = queryDocumentsByClassName(className, separatedb, intent, queryStrings);
		// }
		List<ODocument> result = queryDocumentsByClassName(className, getDB(), intent, queryStrings);
		return result;
	}

	final protected List<ODocument> queryDocumentsByClassName(String className, ODatabaseDocumentTx dbtx, String... queryStrings) {

		return queryDocumentsByClassName(className, dbtx, new OIntentMassiveRead(), queryStrings);
	}

	final protected List<ODocument> queryDocumentsByClassName(String className, ODatabaseDocumentTx dbtx, OIntent intent, String... queryStrings) {

		if(countCluster(className.toLowerCase()) > 0) {
			StringBuilder queryBuilder = new StringBuilder();
			queryBuilder.append("SELECT FROM ");
			queryBuilder.append(className);
			for(String s : queryStrings) {
				queryBuilder.append(s);
			}
			String queryString = queryBuilder.toString();
			/*
			 * Supporting intent
			 */
			if(intent != null) {
				dbtx.declareIntent(intent);
			}
			List<ODocument> documents = dbtx.query(new OSQLSynchQuery<ODocument>(queryString));
			/*
			 * WORKAROUND to bypass local cache
			 */
			List<ODocument> returnDocuments = new ArrayList<ODocument>();
			for(ODocument document : documents) {
				ODocument reloadedDocument = dbtx.load(document.getIdentity(), null, true);
				returnDocuments.add(reloadedDocument);
			}
			if(intent != null) {
				dbtx.declareIntent(null);
			}
			return returnDocuments;
		} else {
			return Collections.<ODocument> emptyList();
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
	final protected ODocument queryDocumentByIdOnSeparateConnection(String className, long id) {

		ODocument result = null;
		try (ODatabaseDocumentTx separatedb = ownerPool.acquire()) {
			result = queryDocumentById(className, id, separatedb);
		}
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

		long result;
		try (ODatabaseDocumentTx separatedb = ownerPool.acquire()) {
			result = countCluster(clusterName, separatedb);
		}
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

	@Override
	public Optional<ISettingsDocument> getSettingsDocument() {

		ODocument document = queryDocumentById(ISettingsDocument.CLASS_NAME, 0);
		return document == null ? Optional.empty() : Optional.of(new SettingsDocument(document));
	}
}
