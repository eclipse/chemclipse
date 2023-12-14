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
package org.eclipse.chemclipse.chromatogram.xxd.integrator.core.peaks;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.exceptions.NoIntegratorAvailableException;

/**
 * @author eselmeister
 */
public class PeakIntegratorSupport implements IPeakIntegratorSupport {

	List<IPeakIntegratorSupplier> suppliers;

	/**
	 * Creates a new suppliers list.
	 */
	public PeakIntegratorSupport() {

		suppliers = new ArrayList<>();
	}

	/**
	 * Adds a {@link IPeakIntegratorSupplier} to the {@link PeakIntegratorSupport}.
	 * 
	 * @param supplier
	 */
	protected void add(IPeakIntegratorSupplier supplier) {

		suppliers.add(supplier);
	}

	@Override
	public List<String> getAvailableIntegratorIds() throws NoIntegratorAvailableException {

		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		arePeakIntegratorsStored();
		List<String> availableIntegrators = new ArrayList<>();
		for(IPeakIntegratorSupplier supplier : suppliers) {
			availableIntegrators.add(supplier.getId());
		}
		return availableIntegrators;
	}

	@Override
	public IPeakIntegratorSupplier getIntegratorSupplier(String integratorId) throws NoIntegratorAvailableException {

		IPeakIntegratorSupplier integratorSupplier = null;
		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		arePeakIntegratorsStored();
		if(integratorId == null || integratorId.equals("")) {
			throw new NoIntegratorAvailableException("There is no integrator available with the following id: " + integratorId + ".");
		}
		endsearch:
		for(IPeakIntegratorSupplier supplier : suppliers) {
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
		IPeakIntegratorSupplier supplier = suppliers.get(index);
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
		for(IPeakIntegratorSupplier supplier : suppliers) {
			integratorNames.add(supplier.getIntegratorName());
		}
		return integratorNames.toArray(new String[integratorNames.size()]);
	}

	// -------------------------------------private methods
	private void arePeakIntegratorsStored() throws NoIntegratorAvailableException {

		if(suppliers.isEmpty()) {
			throw new NoIntegratorAvailableException();
		}
	}
	// -------------------------------------private methods
}
