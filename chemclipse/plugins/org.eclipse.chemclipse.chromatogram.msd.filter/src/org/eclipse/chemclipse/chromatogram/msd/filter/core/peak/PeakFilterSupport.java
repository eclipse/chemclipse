/*******************************************************************************
 * Copyright (c) 2013, 2022 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.core.peak;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.filter.exceptions.NoPeakFilterSupplierAvailableException;

public class PeakFilterSupport implements IPeakFilterSupport {

	private List<IPeakFilterSupplier> suppliers;

	/**
	 * Creates a new suppliers list.
	 */
	public PeakFilterSupport() {

		suppliers = new ArrayList<>();
	}

	/**
	 * Adds a {@link IPeakFilterSupplier} to the {@link IPeakFilterSupport}.
	 * 
	 * @param supplier
	 */
	protected void add(IPeakFilterSupplier supplier) {

		suppliers.add(supplier);
	}

	@Override
	public List<String> getAvailableFilterIds() throws NoPeakFilterSupplierAvailableException {

		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		arePeakFiltersStored();
		List<String> availableFilters = new ArrayList<>();
		for(IPeakFilterSupplier supplier : suppliers) {
			availableFilters.add(supplier.getId());
		}
		return availableFilters;
	}

	@Override
	public String getFilterId(int index) throws NoPeakFilterSupplierAvailableException {

		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		arePeakFiltersStored();
		/*
		 * Test if the index is out of range.
		 */
		if(index < 0 || index > suppliers.size() - 1) {
			throw new NoPeakFilterSupplierAvailableException("There is no peak filter available with the following id: " + index + ".");
		}
		IPeakFilterSupplier supplier = suppliers.get(index);
		return supplier.getId();
	}

	@Override
	public String[] getFilterNames() throws NoPeakFilterSupplierAvailableException {

		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		arePeakFiltersStored();
		/*
		 * If the ArrayList is not empty, return the registered chromatogram
		 * converter filter names.<br/>
		 */
		ArrayList<String> filterNames = new ArrayList<>();
		for(IPeakFilterSupplier supplier : suppliers) {
			filterNames.add(supplier.getFilterName());
		}
		return filterNames.toArray(new String[filterNames.size()]);
	}

	@Override
	public IPeakFilterSupplier getFilterSupplier(String filterId) throws NoPeakFilterSupplierAvailableException {

		IPeakFilterSupplier filterSupplier = null;
		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		arePeakFiltersStored();
		if(filterId == null || filterId.equals("")) {
			throw new NoPeakFilterSupplierAvailableException("There is no peak filter supplier available with the following id: " + filterId + ".");
		}
		endsearch:
		for(IPeakFilterSupplier supplier : suppliers) {
			if(supplier.getId().equals(filterId)) {
				filterSupplier = supplier;
				break endsearch;
			}
		}
		if(filterSupplier == null) {
			throw new NoPeakFilterSupplierAvailableException("There is no peak filter supplier available with the following id: " + filterId + ".");
		} else {
			return filterSupplier;
		}
	}

	private void arePeakFiltersStored() throws NoPeakFilterSupplierAvailableException {

		if(suppliers.isEmpty()) {
			throw new NoPeakFilterSupplierAvailableException();
		}
	}
}
