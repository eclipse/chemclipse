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
package org.eclipse.chemclipse.chromatogram.msd.comparison.massspectrum;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.comparison.exceptions.NoMassSpectrumComparatorAvailableException;

public interface IMassSpectrumComparatorSupport {

	/**
	 * Returns the id of the selected filter name.<br/>
	 * The id of the selected filter is used to determine which comparer should
	 * be used to check the similarity of the two mass spectra.<br/>
	 * Be aware of that the first index is 0. It is a 0-based index.
	 * 
	 * @param index
	 * @return String
	 * @throws NoMassSpectrumComparatorAvailableException
	 */
	String getComparatorId(int index) throws NoMassSpectrumComparatorAvailableException;

	/**
	 * Returns an IMassSpectrumComparisonSupplier object.<br/>
	 * The object stores some additional supplier information.
	 * 
	 * @param converterId
	 * @return IMassSpectrumComparisonSupplier
	 * @throws NoMassSpectrumComparatorAvailableException
	 */
	IMassSpectrumComparisonSupplier getMassSpectrumComparisonSupplier(String converterId) throws NoMassSpectrumComparatorAvailableException;

	/**
	 * Returns an ArrayList with all available comparator ids.<br/>
	 * 
	 * @return List<String>
	 * @throws NoMassSpectrumComparatorAvailableException
	 */
	List<String> getAvailableComparatorIds() throws NoMassSpectrumComparatorAvailableException;

	/**
	 * Returns the list of available comparator names.
	 * 
	 * @return String[]
	 * @throws NoMassSpectrumComparatorAvailableException
	 */
	String[] getComparatorNames() throws NoMassSpectrumComparatorAvailableException;
}
