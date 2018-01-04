/*******************************************************************************
 * Copyright (c) 2014, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.core.massspectrum;

import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.filter.exceptions.NoMassSpectrumFilterSupplierAvailableException;

public interface IMassSpectrumFilterSupport {

	/**
	 * Returns the id of the selected filter name.<br/>
	 * The id of the selected filter is used to determine which filter should be used.
	 * Be aware of that the first index is 0. It is a 0-based index.
	 * 
	 * @param index
	 * @return String
	 * @throws NoMassSpectrumFilterSupplierAvailableException
	 */
	String getFilterId(int index) throws NoMassSpectrumFilterSupplierAvailableException;

	/**
	 * Returns an IMassSpectrumFilterSupplier object.<br/>
	 * The object stores some additional supplier information.
	 * 
	 * @param filterId
	 * @return {@link IMassSpectrumFilterSupplier}
	 * @throws NoMassSpectrumFilterSupplierAvailableException
	 */
	IMassSpectrumFilterSupplier getFilterSupplier(String filterId) throws NoMassSpectrumFilterSupplierAvailableException;

	/**
	 * Returns an ArrayList with all available mass spectrum filter supplier ids.<br/>
	 * 
	 * @return List<String>
	 * @throws NoMassSpectrumFilterSupplierAvailableException
	 */
	List<String> getAvailableFilterIds() throws NoMassSpectrumFilterSupplierAvailableException;

	/**
	 * Returns the list of available mass spectrum filter names.
	 * 
	 * @return String[]
	 * @throws NoMassSpectrumFilterSupplierAvailableException
	 */
	String[] getFilterNames() throws NoMassSpectrumFilterSupplierAvailableException;
}
