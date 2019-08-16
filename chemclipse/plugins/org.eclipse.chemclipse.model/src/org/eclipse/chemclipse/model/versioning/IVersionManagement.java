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

public interface IVersionManagement {

	// TODO JUnit
	/**
	 * Returns if the first revision has been set or not.<br/>
	 * If at least one do action was performed, the return value is false.
	 * 
	 * @return boolean
	 */
	boolean isBaseRevision();

	// TODO JUnit
	/**
	 * Returns the actual revision.
	 * 
	 * @return int
	 */
	int getRevision();

	/**
	 * Call this method to increase the actual revision number.
	 */
	public void doOperation();

	/**
	 * Call this method to decrease the actual revision number.
	 */
	public void undoOperation();

	/**
	 * Call this method to increase the actual revision number.
	 */
	public void redoOperation();

	/**
	 * Returns the valid file of the actual scan revision.<br/>
	 * Dump the scan array before calling one of the do operations.
	 * 
	 * @return File
	 */
	public File getActualScanRevision();

	/**
	 * Returns the valid file of the previous scan revision.<br/>
	 * 
	 * @return File
	 */
	public File getPreviousScanRevision();

	/**
	 * Returns the valid file of the next scan revision.<br/>
	 * 
	 * @return File
	 */
	public File getNextScanRevision();

	/**
	 * Returns the temporary storage directory of the actual chromatogram.
	 * 
	 * @return File
	 */
	public File getStorageDirectory();

	/**
	 * Returns the chromatogram identifier.
	 * 
	 * @return String
	 */
	public String getChromatogramIdentifier();
}
