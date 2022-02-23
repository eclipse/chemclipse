/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.core;

import java.io.File;
import java.util.Date;

/**
 * An interface for single MALDI-TOF MS spectra which contain additional metadata.
 * 
 * @author Matthias Mailänder
 */
public interface IVendorStandaloneMassSpectrum extends IVendorMassSpectrum {

	/**
	 * Returns the file, see setFile().
	 * May return null.
	 * 
	 * @return File
	 */
	File getFile();

	/**
	 * Set the file of the mass spectrum, e.g. if it is a MALDI-MS record.
	 * If it's a GC/MS run, file is not needed cause the chromatogram holds the scans.
	 * 
	 * @param file
	 */
	void setFile(File file);

	/**
	 * Returns the name of the mass spectrum, if it's e.g. a MALDI-MS record.
	 * 
	 * @return String
	 */
	String getName();

	//
	String getSampleName();

	void setSampleName(String name);

	String getDescription();

	void setDescription(String description);

	String getOperator();

	void setOperator(String operator);

	Date getDate();

	void setDate(Date date);

	String getInstrument();

	void setInstrument(String instrument);
}
