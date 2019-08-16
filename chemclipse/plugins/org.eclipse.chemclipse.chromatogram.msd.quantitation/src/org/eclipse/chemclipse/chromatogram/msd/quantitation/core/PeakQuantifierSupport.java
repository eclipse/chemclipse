/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.quantitation.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.quantitation.exceptions.NoPeakQuantifierAvailableException;

public class PeakQuantifierSupport implements IPeakQuantifierSupport {

	private List<IPeakQuantifierSupplier> suppliers;

	public PeakQuantifierSupport() {
		suppliers = new ArrayList<IPeakQuantifierSupplier>();
	}

	/**
	 * Adds a {@link IPeakQuantifierSupplier} to the {@link PeakQuantifierSupport}.
	 * 
	 * @param supplier
	 */
	protected void add(IPeakQuantifierSupplier supplier) {

		suppliers.add(supplier);
	}

	@Override
	public List<String> getAvailablePeakQuantifierIds() throws NoPeakQuantifierAvailableException {

		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		arePeakQuantifierStored();
		List<String> availableDetectors = new ArrayList<String>();
		for(IPeakQuantifierSupplier supplier : suppliers) {
			availableDetectors.add(supplier.getId());
		}
		return availableDetectors;
	}

	@Override
	public IPeakQuantifierSupplier getPeakQuantifierSupplier(String quantifierId) throws NoPeakQuantifierAvailableException {

		IPeakQuantifierSupplier detectorSupplier = null;
		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		arePeakQuantifierStored();
		if(quantifierId == null || quantifierId.equals("")) {
			throw new NoPeakQuantifierAvailableException("There is no peak quantifier available with the following id: " + quantifierId + ".");
		}
		endsearch:
		for(IPeakQuantifierSupplier supplier : suppliers) {
			if(supplier.getId().equals(quantifierId)) {
				detectorSupplier = supplier;
				break endsearch;
			}
		}
		if(detectorSupplier == null) {
			throw new NoPeakQuantifierAvailableException("There is no peak quantifier available with the following id: " + quantifierId + ".");
		} else {
			return detectorSupplier;
		}
	}

	@Override
	public String getPeakQuantifierId(int index) throws NoPeakQuantifierAvailableException {

		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		arePeakQuantifierStored();
		/*
		 * Test if the index is out of range.
		 */
		if(index < 0 || index > suppliers.size() - 1) {
			throw new NoPeakQuantifierAvailableException("There is no peak quantifier available with the following id: " + index + ".");
		}
		IPeakQuantifierSupplier supplier = suppliers.get(index);
		return supplier.getId();
	}

	@Override
	public String[] getPeakQuantifierNames() throws NoPeakQuantifierAvailableException {

		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		arePeakQuantifierStored();
		/*
		 * If the ArrayList is not empty, return the registered chromatogram
		 * converter filter names.<br/>
		 */
		ArrayList<String> detectorNames = new ArrayList<String>();
		for(IPeakQuantifierSupplier supplier : suppliers) {
			detectorNames.add(supplier.getPeakQuantifierName());
		}
		return detectorNames.toArray(new String[detectorNames.size()]);
	}

	// -------------------------------------private methods
	private void arePeakQuantifierStored() throws NoPeakQuantifierAvailableException {

		if(suppliers.size() < 1) {
			throw new NoPeakQuantifierAvailableException();
		}
	}
	// -------------------------------------private methods
}
