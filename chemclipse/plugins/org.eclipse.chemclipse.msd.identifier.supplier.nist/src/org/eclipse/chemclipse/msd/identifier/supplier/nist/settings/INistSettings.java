/*******************************************************************************
 * Copyright (c) 2010, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.nist.settings;

/**
 * @author Dr. Philip Wenig
 * 
 */
public interface INistSettings {

	/**
	 * Returns the NIST path: C:\Programs\NIST\MSSEARCH\NISTMS$.EXE (Windows)
	 * /home/eselmeister/.wine/dosdevices/c:/Programme/NIST/MSSEARCH/nistms$.exe
	 * (Linux)
	 * /home/eselmeister/.wine/drive_c/Programme/NIST/MSSEARCH/nistms$.exe
	 * (Linux)
	 * 
	 * @return
	 */
	String getNistApplication();

	/**
	 * Set the NIST application path.
	 * 
	 * @param nistPath
	 */
	void setNistApplication(String nistPath);

	/**
	 * Set number of targets.
	 * 
	 * @return {@link Integer}
	 */
	int getNumberOfTargets();

	/**
	 * Set the number of targets that shall be reported.
	 * 
	 */
	void setNumberOfTargets(int numberOfTargets);

	/**
	 * If true, the targets will be stored in the peaks / mass spectra.
	 * 
	 * @return boolean
	 */
	boolean getStoreTargets();

	/**
	 * Set if the targets will be stored in the peaks / mass spectra.
	 * 
	 * @return boolean
	 */
	void setStoreTargets(boolean storeTargets);

	/**
	 * Returns the timeout in minutes.
	 * 
	 * @return int
	 */
	int getTimeoutInMinutes();

	/**
	 * Sets the timeout in minutes.
	 * 
	 * @param timeoutInMinutes
	 */
	void setTimeoutInMinutes(int timeoutInMinutes);
}
