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
package org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.exceptions.NoBaselineDetectorAvailableException;

public class BaselineDetectorSupport implements IBaselineDetectorSupport {

	private List<IBaselineDetectorSupplier> suppliers;

	public BaselineDetectorSupport() {

		suppliers = new ArrayList<>();
	}

	/**
	 * Adds a {@link IBaselineDetectorSupplier} to the {@link BaselineDetectorSupport}.
	 * 
	 * @param supplier
	 */
	protected void add(IBaselineDetectorSupplier supplier) {

		suppliers.add(supplier);
	}

	@Override
	public List<String> getAvailableDetectorIds() throws NoBaselineDetectorAvailableException {

		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		areDetectorsStored();
		List<String> availableDetectors = new ArrayList<>();
		for(IBaselineDetectorSupplier supplier : suppliers) {
			availableDetectors.add(supplier.getId());
		}
		return availableDetectors;
	}

	@Override
	public IBaselineDetectorSupplier getBaselineDetectorSupplier(String detectorId) throws NoBaselineDetectorAvailableException {

		IBaselineDetectorSupplier detectorSupplier = null;
		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		areDetectorsStored();
		if(detectorId == null || detectorId.equals("")) {
			throw new NoBaselineDetectorAvailableException("There is no baseline detector available with the following id: " + detectorId + ".");
		}
		endsearch:
		for(IBaselineDetectorSupplier supplier : suppliers) {
			if(supplier.getId().equals(detectorId)) {
				detectorSupplier = supplier;
				break endsearch;
			}
		}
		if(detectorSupplier == null) {
			throw new NoBaselineDetectorAvailableException("There is no baseline detector available with the following id: " + detectorId + ".");
		} else {
			return detectorSupplier;
		}
	}

	@Override
	public String getDetectorId(int index) throws NoBaselineDetectorAvailableException {

		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		areDetectorsStored();
		/*
		 * Test if the index is out of range.
		 */
		if(index < 0 || index > suppliers.size() - 1) {
			throw new NoBaselineDetectorAvailableException("There is no baseline detector available with the following id: " + index + ".");
		}
		IBaselineDetectorSupplier supplier = suppliers.get(index);
		return supplier.getId();
	}

	@Override
	public String[] getDetectorNames() throws NoBaselineDetectorAvailableException {

		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		areDetectorsStored();
		/*
		 * If the ArrayList is not empty, return the registered chromatogram
		 * converter filter names.<br/>
		 */
		ArrayList<String> detectorNames = new ArrayList<>();
		for(IBaselineDetectorSupplier supplier : suppliers) {
			detectorNames.add(supplier.getDetectorName());
		}
		return detectorNames.toArray(new String[detectorNames.size()]);
	}

	// -------------------------------------private methods
	private void areDetectorsStored() throws NoBaselineDetectorAvailableException {

		if(suppliers.isEmpty()) {
			throw new NoBaselineDetectorAvailableException();
		}
	}
	// -------------------------------------private methods
}
