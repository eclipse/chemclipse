/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.wsd.filter.core.chromatogram;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.filter.exceptions.NoChromatogramFilterSupplierAvailableException;

public interface IChromatogramFilterSupportWSD {

	/**
	 * Returns the id of the selected filter name.<br/>
	 * The id of the selected filter is used to determine which filter should be
	 * used to calculate the integration results of the chromatogram.<br/>
	 * Be aware of that the first index is 0. It is a 0-based index.
	 * 
	 * @param index
	 * @return String
	 * @throws NoChromatogramFilterSupplierAvailableException
	 */
	String getFilterId(int index) throws NoChromatogramFilterSupplierAvailableException;

	/**
	 * Returns an IChromatogramFilterSupplier object.<br/>
	 * The object stores some additional supplier information.
	 * 
	 * @param filterId
	 * @return {@link IChromatogramFilterSupplierWSD}
	 * @throws NoChromatogramFilterSupplierAvailableException
	 */
	IChromatogramFilterSupplierWSD getFilterSupplier(String filterId) throws NoChromatogramFilterSupplierAvailableException;

	/**
	 * Returns an ArrayList with all available chromatogram filter supplier ids.<br/>
	 * 
	 * @return List<String>
	 * @throws NoChromatogramFilterSupplierAvailableException
	 */
	List<String> getAvailableFilterIds() throws NoChromatogramFilterSupplierAvailableException;

	/**
	 * Returns the list of available chromatogram filter names.
	 * 
	 * @return String[]
	 * @throws NoChromatogramFilterSupplierAvailableException
	 */
	String[] getFilterNames() throws NoChromatogramFilterSupplierAvailableException;
}
