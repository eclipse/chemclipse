/*******************************************************************************
 * Copyright (c) 2010, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - remove nist application settings
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.nist.settings;

import java.io.File;

public interface ISearchSettings {

	/**
	 * Return the MSSEARCH folder of the NIST installation.
	 * 
	 * @return File
	 */
	File getNistFolder();

	/**
	 * Retrieve the given number of targets from the database.
	 * 
	 * @return {@link Integer}
	 */
	int getNumberOfTargets();

	/**
	 * Use the optimized mass spectrum if available.
	 * Otherwise, the mass spectrum itself is used.
	 * 
	 * @return boolean
	 */
	boolean isUseOptimizedMassSpectrum();

	/**
	 * Return minimum match factor.
	 * 
	 * @return float
	 */
	float getMinMatchFactor();

	/**
	 * Return minimum reverse match factor.
	 * 
	 * @return float
	 */
	float getMinReverseMatchFactor();

	/**
	 * Run the query in batch modus.
	 * Otherwise the GUI is used.
	 * 
	 * @return boolean
	 */
	boolean isBatchModus();

	/**
	 * The background modus could fail.
	 * The process will be skipped after the given amount of time.
	 * 
	 * @param int
	 */
	int getTimeoutInMinutes();

	/**
	 * Wait the time, before starting the NIST GUI.
	 * Otherwise, the NIST GUI is started too early and mass spectra can't be processed.
	 * 
	 * @return int
	 */
	int getWaitInSeconds();
}
