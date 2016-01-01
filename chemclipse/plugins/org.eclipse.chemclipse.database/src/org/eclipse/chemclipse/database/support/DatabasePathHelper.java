/*******************************************************************************
 * Copyright (c) 2012, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 *******************************************************************************/
package org.eclipse.chemclipse.database.support;

import java.io.File;
import java.util.Random;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.settings.ApplicationSettings;
import org.eclipse.chemclipse.support.settings.IOperatingSystemUtils;
import org.eclipse.chemclipse.support.settings.OperatingSystemUtils;

public class DatabasePathHelper {

	private static final Logger logger = Logger.getLogger(DatabasePathHelper.class);
	public static final String LOCAL_DB_PREFIX = "plocal:";
	public static final String REMOTE_DB_PREFIX = "remote:";
	public static final String[][] DATABASE_CONNECTIONS = new String[][]{{"Local", LOCAL_DB_PREFIX}, {"Remote", REMOTE_DB_PREFIX}};
	public static final String DEFAULT_IDENTIFIER = "org.eclipse.chemclipse.database";
	private String identifier = DEFAULT_IDENTIFIER;

	/**
	 * Uses e.g. "org.eclipse.chemclipse.chromatogram.msd.identifier.supplier.chromident" as an identifier.
	 * 
	 * @param identifier
	 */
	public DatabasePathHelper(String identifier) {
		if(identifier == null) {
			this.identifier = DEFAULT_IDENTIFIER;
		} else {
			this.identifier = identifier;
		}
	}

	public static boolean isLocalDatabasePath(String database) {

		return database.startsWith(DatabasePathHelper.LOCAL_DB_PREFIX);
	}

	public static boolean isRemoteDatabasePath(String database) {

		return database.startsWith(DatabasePathHelper.REMOTE_DB_PREFIX);
	}

	public static boolean isValidDatabasePath(String database) {

		return isLocalDatabasePath(database) || isRemoteDatabasePath(database);
	}

	/**
	 * Returns the file object (directory) where the chromatogram instances can
	 * temporarily be stored.
	 * 
	 * @return File
	 */
	public File getStoragePath() {

		File file = new File(ApplicationSettings.getSettingsDirectory().getAbsolutePath() + File.separator + identifier);
		/*
		 * Create the directory if it not exists.
		 */
		if(!file.exists()) {
			if(!file.mkdirs()) {
				logger.warn("The temporarily ident directory could not be created: " + file.getAbsolutePath());
			}
		}
		return file;
	}

	/**
	 * Returns a temporary file. Use a file name, e.g. "82398983240.ocb".
	 * 
	 * @param fileExtension
	 * @return
	 */
	public File getTempFile(String fileName) {

		StringBuilder builder = new StringBuilder();
		builder.append(ApplicationSettings.getSettingsDirectory().getAbsolutePath());
		builder.append(File.separator);
		builder.append(identifier);
		builder.append(File.separator);
		builder.append("tmp");
		File dir = new File(builder.toString());
		/*
		 * Create the directory if it not exists.
		 */
		if(!dir.exists()) {
			if(!dir.mkdirs()) {
				logger.warn("The temporarily ident directory could not be created: " + dir.getAbsolutePath());
			}
		}
		/*
		 * Create the file
		 */
		builder.append(File.separator);
		builder.append(fileName);
		File file = new File(builder.toString());
		return file;
	}

	/**
	 * Creates a random identifier with a length of 15 numerical value, e.g.:
	 * 830472894971928
	 * 
	 * @return
	 */
	public String getRandomIdentifier() {

		StringBuilder builder = new StringBuilder();
		/*
		 * Create the random identifier.
		 */
		Random random = new Random();
		for(int i = 0; i <= 15; i++) {
			builder.append(random.nextInt(10));
		}
		return builder.toString();
	}

	/**
	 * DO NOT CALL THIS METHOD IF YOU NOT REALLY KNOW WHAT YOU ARE DOING.<br/>
	 * Cleans a database in the storage directory.<br/>
	 * 
	 */
	public void cleanDatabase(String fullDatabasePath) {

		// String databasePath = getDatabasePath(name);
		File directory = new File(fullDatabasePath);
		deleteFiles(directory);
	}

	public String getDatabasePath(String name) {

		StringBuilder builder = new StringBuilder();
		builder.append(getStoragePath().getAbsolutePath());
		builder.append(File.separator);
		builder.append(name);
		return builder.toString();
	}

	/**
	 * Returns the full database path, if the full local database path has not been provided.
	 * Returns null is not a local database ha been provided.
	 * 
	 * @param name
	 * @return String
	 */
	public String getLocalDatabasePath(String name) {

		if(isLocalDatabasePath(name)) {
			IOperatingSystemUtils operatingSystemUtils = new OperatingSystemUtils();
			String[] parts;
			/*
			 * Checking whether it has been a full local database path. If yes it will split.
			 */
			if(operatingSystemUtils.isWindows()) {
				parts = name.split("\\\\");
			} else {
				parts = name.split("/");
			}
			/*
			 * Get the name.
			 */
			if(parts != null && parts.length > 1) {
				// databaseName = LOCAL_DB_PREFIX + parts[parts.length - 1];
				/*
				 * Full database path has been provided, return it
				 */
				return name;
			} else if(parts != null && parts.length == 1) {
				/*
				 * Could not be splitted, probably database path has been not formatted.
				 */
				return LOCAL_DB_PREFIX + getDatabasePath(name.replace(LOCAL_DB_PREFIX, ""));
			}
		} else if(isRemoteDatabasePath(name)) {
			return name;
		}
		return LOCAL_DB_PREFIX + getDatabasePath(name);
	}

	/**
	 * Returns the trimmed database path for local paths, if the full local database path has been provided.
	 * 
	 * @param name
	 * @return String
	 */
	public String trimLocalDatabasePath(String name) {

		if(isLocalDatabasePath(name)) {
			IOperatingSystemUtils operatingSystemUtils = new OperatingSystemUtils();
			String[] parts;
			/*
			 * Checking whether it has been a full local database path. If yes it will split.
			 */
			if(operatingSystemUtils.isWindows()) {
				parts = name.split("\\\\");
			} else {
				parts = name.split("/");
			}
			/*
			 * Get the name.
			 */
			if(parts != null && parts.length > 1) {
				/*
				 * Return trimmed name
				 */
				return LOCAL_DB_PREFIX + parts[parts.length - 1];
			} else {
				return name;
			}
		} else {
			return name;
		}
	}

	/**
	 * Deletes the given directory recursively.
	 * 
	 * @param directory
	 */
	private void deleteFiles(File directory) {

		/*
		 * Delete all files in all directories.
		 */
		if(directory != null && directory.exists()) {
			for(File file : directory.listFiles()) {
				if(file.isDirectory()) {
					deleteFiles(file);
				} else {
					if(!file.delete()) {
						logger.warn("The file " + file + "could not be deleted.");
					}
				}
			}
			/*
			 * Delete the directory if all files have been removed.
			 */
			if(!directory.delete()) {
				logger.warn("The directory " + directory + "could not be deleted.");
			}
		}
	}
}
