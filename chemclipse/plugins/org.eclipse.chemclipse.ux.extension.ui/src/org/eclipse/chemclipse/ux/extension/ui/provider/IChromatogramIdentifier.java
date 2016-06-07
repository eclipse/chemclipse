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
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.provider;

import java.io.File;

public interface IChromatogramIdentifier {

	String TYPE_MSD = "MSD";
	String TYPE_CSD = "CSD";
	String TYPE_WSD = "WSD";

	/**
	 * Returns the identifier type.
	 * 
	 * @return String
	 */
	String getType();

	/**
	 * Check whether the file is a supplied chromatogram or not.
	 * 
	 * @param file
	 * @return boolean
	 */
	boolean isChromatogram(File file);

	/**
	 * Check whether the file is a supplied chromatogram directory or not.
	 * 
	 * @param file
	 * @return boolean
	 */
	boolean isChromatogramDirectory(File file);

	/**
	 * Try to match the magic number of the file format.
	 * If true, it's pretty likely that the format can be imported.
	 * 
	 * @param file
	 * @return true
	 */
	boolean isMatchMagicNumber(File file);
}
