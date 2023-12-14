/*******************************************************************************
 * Copyright (c) 2008, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.core.combined;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.exceptions.NoIntegratorAvailableException;

public class CombinedIntegratorSupport implements ICombinedIntegratorSupport {

	List<ICombinedIntegratorSupplier> suppliers;

	/**
	 * Creates a new suppliers list.
	 */
	public CombinedIntegratorSupport() {

		suppliers = new ArrayList<>();
	}

	/**
	 * Adds a {@link ICombinedIntegratorSupplier} to the {@link CombinedIntegratorSupport}.
	 * 
	 * @param supplier
	 */
	protected void add(ICombinedIntegratorSupplier supplier) {

		suppliers.add(supplier);
	}

	@Override
	public List<String> getAvailableIntegratorIds() throws NoIntegratorAvailableException {

		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		arePeakIntegratorsStored();
		List<String> availableIntegrators = new ArrayList<>();
		for(ICombinedIntegratorSupplier supplier : suppliers) {
			availableIntegrators.add(supplier.getId());
		}
		return availableIntegrators;
	}

	@Override
	public ICombinedIntegratorSupplier getIntegratorSupplier(String integratorId) throws NoIntegratorAvailableException {

		ICombinedIntegratorSupplier integratorSupplier = null;
		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		arePeakIntegratorsStored();
		if(integratorId == null || integratorId.equals("")) {
			throw new NoIntegratorAvailableException("There is no integrator available with the following id: " + integratorId + ".");
		}
		endsearch:
		for(ICombinedIntegratorSupplier supplier : suppliers) {
			if(supplier.getId().equals(integratorId)) {
				integratorSupplier = supplier;
				break endsearch;
			}
		}
		if(integratorSupplier == null) {
			throw new NoIntegratorAvailableException("There is no integrator available with the following id: " + integratorId + ".");
		} else {
			return integratorSupplier;
		}
	}

	@Override
	public String getIntegratorId(int index) throws NoIntegratorAvailableException {

		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		arePeakIntegratorsStored();
		/*
		 * Test if the index is out of range.
		 */
		if(index < 0 || index > suppliers.size() - 1) {
			throw new NoIntegratorAvailableException("There is no integrator available with the following id: " + index + ".");
		}
		ICombinedIntegratorSupplier supplier = suppliers.get(index);
		return supplier.getId();
	}

	@Override
	public String[] getIntegratorNames() throws NoIntegratorAvailableException {

		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		arePeakIntegratorsStored();
		/*
		 * If the ArrayList is not empty, return the registered chromatogram
		 * converter filter names.<br/>
		 */
		ArrayList<String> integratorNames = new ArrayList<>();
		for(ICombinedIntegratorSupplier supplier : suppliers) {
			integratorNames.add(supplier.getIntegratorName());
		}
		return integratorNames.toArray(new String[integratorNames.size()]);
	}

	private void arePeakIntegratorsStored() throws NoIntegratorAvailableException {

		if(suppliers.isEmpty()) {
			throw new NoIntegratorAvailableException();
		}
	}
}
