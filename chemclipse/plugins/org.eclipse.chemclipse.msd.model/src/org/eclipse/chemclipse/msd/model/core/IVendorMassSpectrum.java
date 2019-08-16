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
package org.eclipse.chemclipse.msd.model.core;

import java.io.File;

/**
 * More informations about the class structure of mass spectra are stored in {@link IScanMSD}.
 * 
 * @author eselmeister
 */
public interface IVendorMassSpectrum extends IRegularMassSpectrum {

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

	/**
	 * Returns the maximal possible amount of ion values.<br/>
	 * It could be that the range of the ion values differs from manufacturer to
	 * manufacturer.<br/>
	 * One manufacturer for example stores maximal 2000 ion values, another 4000
	 * ion.<br/>
	 * Be aware of it!
	 * 
	 * @return int
	 */
	int getMaxPossibleIons();

	/**
	 * Returns the minimal possible retention time.<br/>
	 * It could be that the range of the retention time differs from
	 * manufacturer to manufacturer.<br/>
	 * Be aware of it!
	 * 
	 * @return int
	 */
	int getMinPossibleRetentionTime();

	/**
	 * Returns the maximal possible retention time.<br/>
	 * It could be that the range of the retention time differs from
	 * manufacturer to manufacturer.<br/>
	 * Be aware of it!
	 * 
	 * @return int
	 */
	int getMaxPossibleRetentionTime();
}
