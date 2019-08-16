/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.versioning;

import java.io.File;

import org.eclipse.chemclipse.logging.core.Logger;

public class VersionManagement implements IVersionManagement {

	private static Logger logger = Logger.getLogger(VersionManagement.class);
	private String identifier;
	/*
	 * Directories for the scans and the extracted ion chromatograms (xic)
	 * respectively the total ion chromatogram (tic).
	 */
	private static final String SCAN_DIRECTORY = "scans";
	private static final String SCAN_HIBERNATE_FILE = "dumpScans";
	private static final String XIC_DIRECTORY = "xics";
	private File storageDirectory; // This is the chromatogram instance storage
									// directory.
	private File storageDirectoryScans;
	private File storageDirectoryXics;
	private static final String MARKER = "_r";
	private int revision = 0;
	private int operationLimit = 25; // Use 25 as a default value;
	// TODO baseRevision
	private boolean baseRevision;

	public VersionManagement() {
		initializeStorageDirectories();
		// TODO baseRevision
		baseRevision = true;
	}

	/**
	 * This method intializes the chromatogram instance directories.
	 */
	private void initializeStorageDirectories() {

		createIdentifier();
		/*
		 * This is the main directory to store informations about the
		 * chromatogram.
		 */
		storageDirectory = new File(PathHelper.getStoragePath() + File.separator + identifier);
		if(!storageDirectory.exists()) {
			if(!storageDirectory.mkdir()) {
				logger.warn("The temporarily chromatogram file directory could not be created: " + storageDirectory.getAbsolutePath());
			}
		}
		/*
		 * This is the storage directory for the scans.
		 */
		storageDirectoryScans = new File(storageDirectory.getAbsolutePath() + File.separator + SCAN_DIRECTORY);
		if(!storageDirectoryScans.exists()) {
			if(!storageDirectoryScans.mkdir()) {
				logger.warn("The temporarily chromatogram scan file directory could not be created: " + storageDirectoryScans.getAbsolutePath());
			}
		}
		/*
		 * This is the storage directory for the extracted ion chromatograms and
		 * the total ion chromatogram.
		 */
		storageDirectoryXics = new File(storageDirectory.getAbsolutePath() + File.separator + XIC_DIRECTORY);
		if(!storageDirectoryXics.exists()) {
			if(!storageDirectoryXics.mkdir()) {
				logger.warn("The temporarily chromatogram tic file directory could not be created: " + storageDirectoryXics.getAbsolutePath());
			}
		}
	}

	private void createIdentifier() {

		identifier = "chromatogram@" + Integer.toHexString(hashCode()) + TimeHelper.getTimeStampId();
	}

	// -----------------------------------------------IVersionManagement
	// TODO JUnit
	@Override
	public boolean isBaseRevision() {

		return baseRevision;
	}

	// TODO JUnit
	@Override
	public int getRevision() {

		return revision;
	}

	@Override
	public void doOperation() {

		revision = validateRevision(++revision);
		// TODO base revision
		baseRevision = false;
	}

	@Override
	public void undoOperation() {

		revision = validateRevision(--revision);
	}

	@Override
	public void redoOperation() {

		revision = validateRevision(++revision);
	}

	@Override
	public File getActualScanRevision() {

		String file = storageDirectoryScans.getAbsolutePath() + File.separator + SCAN_HIBERNATE_FILE + getActualRevisionMarker();
		return new File(file);
	}

	@Override
	public File getPreviousScanRevision() {

		String file = storageDirectoryScans.getAbsolutePath() + File.separator + SCAN_HIBERNATE_FILE + getPreviousRevisionMarker();
		return new File(file);
	}

	@Override
	public File getNextScanRevision() {

		String file = storageDirectoryScans.getAbsolutePath() + File.separator + SCAN_HIBERNATE_FILE + getNextRevisionMarker();
		return new File(file);
	}

	@Override
	public File getStorageDirectory() {

		return storageDirectory;
	}

	@Override
	public String getChromatogramIdentifier() {

		return identifier;
	}

	// -----------------------------------------------IVersionManagement
	/**
	 * Validates the given revision and gives back a valid revision.
	 * 
	 * @param int
	 */
	private int validateRevision(int revision) {

		if(revision < 0 || revision > operationLimit) {
			revision = 0;
		}
		return revision;
	}

	/**
	 * Returns the actual available revision marker.<br/>
	 * For example, if operations has been done 3 times, the actual revision is
	 * 3.<br/>
	 * The marker "_r3" will be returned.
	 * 
	 * @return String
	 */
	private String getActualRevisionMarker() {

		return MARKER + validateRevision(revision);
	}

	private String getPreviousRevisionMarker() {

		return MARKER + validateRevision(revision - 1);
	}

	private String getNextRevisionMarker() {

		return MARKER + validateRevision(revision + 1);
	}
}
