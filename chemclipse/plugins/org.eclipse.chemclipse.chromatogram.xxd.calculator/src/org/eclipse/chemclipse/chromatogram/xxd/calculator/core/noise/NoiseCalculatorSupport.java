/*******************************************************************************
 * Copyright (c) 2014, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.calculator.core.noise;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.calculator.exceptions.NoNoiseCalculatorAvailableException;

public class NoiseCalculatorSupport implements INoiseCalculatorSupport {

	private final List<INoiseCalculatorSupplier> suppliers;

	public NoiseCalculatorSupport() {

		suppliers = new ArrayList<>();
	}

	/**
	 * Adds a {@link INoiseCalculatorSupplier} to the {@link NoiseCalculatorSupport}.
	 * 
	 * @param supplier
	 */
	protected void add(INoiseCalculatorSupplier supplier) {

		suppliers.add(supplier);
	}

	@Override
	public List<String> getAvailableCalculatorIds() throws NoNoiseCalculatorAvailableException {

		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		areCalculatorsStored();
		List<String> availableDetectors = new ArrayList<>();
		for(INoiseCalculatorSupplier supplier : suppliers) {
			availableDetectors.add(supplier.getId());
		}
		return availableDetectors;
	}

	@Override
	public INoiseCalculatorSupplier getCalculatorSupplier(String calculatorId) throws NoNoiseCalculatorAvailableException {

		INoiseCalculatorSupplier detectorSupplier = null;
		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		areCalculatorsStored();
		if(calculatorId == null || calculatorId.equals("")) {
			throw new NoNoiseCalculatorAvailableException("There is no noise calculator available with the following id: " + calculatorId + ".");
		}
		endsearch:
		for(INoiseCalculatorSupplier supplier : suppliers) {
			if(supplier.getId().equals(calculatorId)) {
				detectorSupplier = supplier;
				break endsearch;
			}
		}
		if(detectorSupplier == null) {
			throw new NoNoiseCalculatorAvailableException("There is no noise calculator available with the following id: " + calculatorId + ".");
		} else {
			return detectorSupplier;
		}
	}

	@Override
	public String getCalculatorId(int index) throws NoNoiseCalculatorAvailableException {

		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		areCalculatorsStored();
		/*
		 * Test if the index is out of range.
		 */
		if(index < 0 || index > suppliers.size() - 1) {
			throw new NoNoiseCalculatorAvailableException("There is no noise calculator available with the following id: " + index + ".");
		}
		INoiseCalculatorSupplier supplier = suppliers.get(index);
		return supplier.getId();
	}

	@Override
	public String[] getCalculatorNames() throws NoNoiseCalculatorAvailableException {

		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		areCalculatorsStored();
		/*
		 * If the ArrayList is not empty, return the registered chromatogram
		 * converter filter names.<br/>
		 */
		ArrayList<String> detectorNames = new ArrayList<>();
		for(INoiseCalculatorSupplier supplier : suppliers) {
			detectorNames.add(supplier.getCalculatorName());
		}
		return detectorNames.toArray(new String[detectorNames.size()]);
	}

	// -------------------------------------private methods
	private void areCalculatorsStored() throws NoNoiseCalculatorAvailableException {

		if(suppliers.isEmpty()) {
			throw new NoNoiseCalculatorAvailableException();
		}
	}
	// -------------------------------------private methods

	@Override
	public Collection<INoiseCalculatorSupplier> getCalculatorSupplier() {

		return Collections.unmodifiableCollection(suppliers);
	}
}
