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
package org.eclipse.chemclipse.chromatogram.msd.filter.core.chromatogram;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.filter.exceptions.NoChromatogramFilterSupplierAvailableException;

public class ChromatogramFilterSupportMSD implements IChromatogramFilterSupportMSD {

	private List<IChromatogramFilterSupplierMSD> suppliers;

	/**
	 * Creates a new suppliers list.
	 */
	public ChromatogramFilterSupportMSD() {
		suppliers = new ArrayList<IChromatogramFilterSupplierMSD>();
	}

	/**
	 * Adds a {@link IChromatogramFilterSupplierMSD} to the {@link IChromatogramFilterSupportMSD}.
	 * 
	 * @param supplier
	 */
	protected void add(IChromatogramFilterSupplierMSD supplier) {

		suppliers.add(supplier);
	}

	@Override
	public List<String> getAvailableFilterIds() throws NoChromatogramFilterSupplierAvailableException {

		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		areChromatogramFiltersStored();
		List<String> availableFilters = new ArrayList<String>();
		for(IChromatogramFilterSupplierMSD supplier : suppliers) {
			availableFilters.add(supplier.getId());
		}
		return availableFilters;
	}

	@Override
	public String getFilterId(int index) throws NoChromatogramFilterSupplierAvailableException {

		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		areChromatogramFiltersStored();
		/*
		 * Test if the index is out of range.
		 */
		if(index < 0 || index > suppliers.size() - 1) {
			throw new NoChromatogramFilterSupplierAvailableException("There is no chromatogram filter available with the following id: " + index + ".");
		}
		IChromatogramFilterSupplierMSD supplier = suppliers.get(index);
		return supplier.getId();
	}

	@Override
	public String[] getFilterNames() throws NoChromatogramFilterSupplierAvailableException {

		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		areChromatogramFiltersStored();
		/*
		 * If the ArrayList is not empty, return the registered chromatogram
		 * converter filter names.<br/>
		 */
		ArrayList<String> filterNames = new ArrayList<String>();
		for(IChromatogramFilterSupplierMSD supplier : suppliers) {
			filterNames.add(supplier.getFilterName());
		}
		return filterNames.toArray(new String[filterNames.size()]);
	}

	@Override
	public IChromatogramFilterSupplierMSD getFilterSupplier(String filterId) throws NoChromatogramFilterSupplierAvailableException {

		IChromatogramFilterSupplierMSD filterSupplier = null;
		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		areChromatogramFiltersStored();
		if(filterId == null || filterId.equals("")) {
			throw new NoChromatogramFilterSupplierAvailableException("There is no chromatogram filter supplier available with the following id: " + filterId + ".");
		}
		endsearch:
		for(IChromatogramFilterSupplierMSD supplier : suppliers) {
			if(supplier.getId().equals(filterId)) {
				filterSupplier = supplier;
				break endsearch;
			}
		}
		if(filterSupplier == null) {
			throw new NoChromatogramFilterSupplierAvailableException("There is no chromatogram filter supplier available with the following id: " + filterId + ".");
		} else {
			return filterSupplier;
		}
	}

	// -------------------------------------private methods
	private void areChromatogramFiltersStored() throws NoChromatogramFilterSupplierAvailableException {

		if(suppliers.size() < 1) {
			throw new NoChromatogramFilterSupplierAvailableException();
		}
	}
	// -------------------------------------private methods
}
