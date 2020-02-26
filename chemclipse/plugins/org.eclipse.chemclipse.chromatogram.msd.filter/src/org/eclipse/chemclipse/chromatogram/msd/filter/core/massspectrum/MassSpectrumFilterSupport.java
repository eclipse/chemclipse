/*******************************************************************************
 * Copyright (c) 2014, 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.filter.core.massspectrum;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.filter.exceptions.NoMassSpectrumFilterSupplierAvailableException;

public class MassSpectrumFilterSupport implements IMassSpectrumFilterSupport {

	private List<IMassSpectrumFilterSupplier> suppliers = new ArrayList<IMassSpectrumFilterSupplier>();

	/**
	 * Adds a {@link IMassSpectrumFilterSupplier} to the {@link IMassSpectrumFilterSupport}.
	 * 
	 * @param supplier
	 */
	protected void add(IMassSpectrumFilterSupplier supplier) {

		suppliers.add(supplier);
	}

	@Override
	public List<String> getAvailableFilterIds() throws NoMassSpectrumFilterSupplierAvailableException {

		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		areMassSpectrumFiltersStored();
		List<String> availableFilters = new ArrayList<String>();
		for(IMassSpectrumFilterSupplier supplier : suppliers) {
			availableFilters.add(supplier.getId());
		}
		return availableFilters;
	}

	@Override
	public String getFilterId(int index) throws NoMassSpectrumFilterSupplierAvailableException {

		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		areMassSpectrumFiltersStored();
		/*
		 * Test if the index is out of range.
		 */
		if(index < 0 || index > suppliers.size() - 1) {
			throw new NoMassSpectrumFilterSupplierAvailableException("There is no mass spectrum filter available with the following id: " + index + ".");
		}
		IMassSpectrumFilterSupplier supplier = suppliers.get(index);
		return supplier.getId();
	}

	@Override
	public String[] getFilterNames() throws NoMassSpectrumFilterSupplierAvailableException {

		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		areMassSpectrumFiltersStored();
		/*
		 * If the ArrayList is not empty, return the registered mass spectrum
		 * filter names.<br/>
		 */
		ArrayList<String> filterNames = new ArrayList<String>();
		for(IMassSpectrumFilterSupplier supplier : suppliers) {
			filterNames.add(supplier.getFilterName());
		}
		return filterNames.toArray(new String[filterNames.size()]);
	}

	@Override
	public IMassSpectrumFilterSupplier getFilterSupplier(String filterId) throws NoMassSpectrumFilterSupplierAvailableException {

		IMassSpectrumFilterSupplier filterSupplier = null;
		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		areMassSpectrumFiltersStored();
		if(filterId == null || filterId.equals("")) {
			throw new NoMassSpectrumFilterSupplierAvailableException("There is no mass spectrum filter supplier available with the following id: " + filterId + ".");
		}
		endsearch:
		for(IMassSpectrumFilterSupplier supplier : suppliers) {
			if(supplier.getId().equals(filterId)) {
				filterSupplier = supplier;
				break endsearch;
			}
		}
		if(filterSupplier == null) {
			throw new NoMassSpectrumFilterSupplierAvailableException("There is no mass spectrum filter supplier available with the following id: " + filterId + ".");
		} else {
			return filterSupplier;
		}
	}

	@Override
	public Collection<IMassSpectrumFilterSupplier> getSuppliers() {

		return Collections.unmodifiableList(suppliers);
	}

	private void areMassSpectrumFiltersStored() throws NoMassSpectrumFilterSupplierAvailableException {

		if(suppliers.size() < 1) {
			throw new NoMassSpectrumFilterSupplierAvailableException();
		}
	}
}
