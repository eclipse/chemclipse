/*******************************************************************************
 * Copyright (c) 2008, 2022 Lablicate GmbH.
 *
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Jan Holy - implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.peak.detector.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.peak.detector.exceptions.NoPeakDetectorAvailableException;

/**
 * @author eselmeister
 */
public abstract class AbstractPeakDetectorSupport<S extends IPeakDetectorSupplier> implements IPeakDetectorSupport {

	List<S> suppliers;

	public AbstractPeakDetectorSupport() {

		suppliers = new ArrayList<>();
	}

	public void add(S supplier) {

		suppliers.add(supplier);
	}

	@Override
	public List<String> getAvailablePeakDetectorIds() throws NoPeakDetectorAvailableException {

		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		arePeakDetectorsStored();
		List<String> availablePeakDetectors = new ArrayList<>();
		for(IPeakDetectorSupplier supplier : suppliers) {
			availablePeakDetectors.add(supplier.getId());
		}
		return availablePeakDetectors;
	}

	protected S getSupplier(String peakDetectorId) throws NoPeakDetectorAvailableException {

		S peakDetectorSupplier = null;
		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		arePeakDetectorsStored();
		if(peakDetectorId == null || peakDetectorId.equals("")) {
			throw new NoPeakDetectorAvailableException("There is no peak detector available with the following id: " + peakDetectorId + ".");
		}
		endsearch:
		for(S supplier : suppliers) {
			if(supplier.getId().equals(peakDetectorId)) {
				peakDetectorSupplier = supplier;
				break endsearch;
			}
		}
		if(peakDetectorSupplier == null) {
			throw new NoPeakDetectorAvailableException("There is no peak detector available with the following id: " + peakDetectorId + ".");
		} else {
			return peakDetectorSupplier;
		}
	}

	@Override
	public String getPeakDetectorId(int index) throws NoPeakDetectorAvailableException {

		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		arePeakDetectorsStored();
		/*
		 * Test if the index is out of range.
		 */
		if(index < 0 || index > suppliers.size() - 1) {
			throw new NoPeakDetectorAvailableException("There is no peak detector available with the following id: " + index + ".");
		}
		IPeakDetectorSupplier supplier = suppliers.get(index);
		return supplier.getId();
	}

	@Override
	public String[] getPeakDetectorNames() throws NoPeakDetectorAvailableException {

		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		arePeakDetectorsStored();
		/*
		 * If the ArrayList is not empty, return the registered chromatogram
		 * converter filter names.<br/>
		 */
		ArrayList<String> integratorNames = new ArrayList<>();
		for(IPeakDetectorSupplier supplier : suppliers) {
			integratorNames.add(supplier.getPeakDetectorName());
		}
		return integratorNames.toArray(new String[integratorNames.size()]);
	}

	// -------------------------------------private methods
	private void arePeakDetectorsStored() throws NoPeakDetectorAvailableException {

		if(suppliers.isEmpty()) {
			throw new NoPeakDetectorAvailableException();
		}
	}
}
