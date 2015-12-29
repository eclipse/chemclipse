package org.eclipse.chemclipse.database.ui.runnables;

/*******************************************************************************
 * Copyright (c) 2012, 2015 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 *******************************************************************************/
import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;

import org.eclipse.chemclipse.database.exceptions.NoDatabaseAvailableException;
import org.eclipse.chemclipse.database.model.IDatabases;
import org.eclipse.chemclipse.logging.core.Logger;

public class CreateNewDatabaseRunnable implements IRunnableWithProgress {

	private static final Logger logger = Logger.getLogger(CreateNewDatabaseRunnable.class);
	private static final String description = "Creating new database";
	private IDatabases databases;
	private String dbName;
	private String dbDescription;

	public CreateNewDatabaseRunnable(IDatabases databases, String dbName, String dbDescription) {
		this.databases = databases;
		this.dbName = dbName;
		this.dbDescription = dbDescription;
	}

	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

		try {
			monitor.beginTask(description, IProgressMonitor.UNKNOWN);
			/*
			 * Add the chromatogram to the database chromatogram.
			 */
			try {
				databases.createDatabase(dbName, dbDescription);
			} catch(NoDatabaseAvailableException e) {
				logger.warn(e);
			}
		} finally {
			monitor.done();
		}
	}
}
