/*******************************************************************************
 * Copyright (c) 2011, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.classifier.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.classifier.exceptions.NoChromatogramClassifierSupplierAvailableException;

public class ChromatogramClassifierSupport implements IChromatogramClassifierSupport {

	private List<IChromatogramClassifierSupplier> suppliers;

	/**
	 * Creates a new suppliers list.
	 */
	public ChromatogramClassifierSupport() {

		suppliers = new ArrayList<>();
	}

	/**
	 * Adds a {@link IChromatogramClassifierSupplier} to the {@link IChromatogramClassifierSupport}.
	 * 
	 * @param supplier
	 */
	protected void add(IChromatogramClassifierSupplier supplier) {

		suppliers.add(supplier);
	}

	@Override
	public List<String> getAvailableClassifierIds() throws NoChromatogramClassifierSupplierAvailableException {

		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		areChromatogramClassifiersStored();
		List<String> availableClassifiers = new ArrayList<>();
		for(IChromatogramClassifierSupplier supplier : suppliers) {
			availableClassifiers.add(supplier.getId());
		}
		return availableClassifiers;
	}

	@Override
	public String getClassifierId(int index) throws NoChromatogramClassifierSupplierAvailableException {

		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		areChromatogramClassifiersStored();
		/*
		 * Test if the index is out of range.
		 */
		if(index < 0 || index > suppliers.size() - 1) {
			throw new NoChromatogramClassifierSupplierAvailableException("There is no chromatogram classifier available with the following id: " + index + ".");
		}
		IChromatogramClassifierSupplier supplier = suppliers.get(index);
		return supplier.getId();
	}

	@Override
	public String[] getClassifierNames() throws NoChromatogramClassifierSupplierAvailableException {

		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		areChromatogramClassifiersStored();
		/*
		 * If the ArrayList is not empty, return the registered chromatogram
		 * converter classifier names.<br/>
		 */
		ArrayList<String> classifierNames = new ArrayList<>();
		for(IChromatogramClassifierSupplier supplier : suppliers) {
			classifierNames.add(supplier.getClassifierName());
		}
		return classifierNames.toArray(new String[classifierNames.size()]);
	}

	@Override
	public IChromatogramClassifierSupplier getClassifierSupplier(String classifierId) throws NoChromatogramClassifierSupplierAvailableException {

		IChromatogramClassifierSupplier classifierSupplier = null;
		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		areChromatogramClassifiersStored();
		if(classifierId == null || classifierId.equals("")) {
			throw new NoChromatogramClassifierSupplierAvailableException("There is no chromatogram classifier supplier available with the following id: " + classifierId + ".");
		}
		endsearch:
		for(IChromatogramClassifierSupplier supplier : suppliers) {
			if(supplier.getId().equals(classifierId)) {
				classifierSupplier = supplier;
				break endsearch;
			}
		}
		if(classifierSupplier == null) {
			throw new NoChromatogramClassifierSupplierAvailableException("There is no chromatogram classifier supplier available with the following id: " + classifierId + ".");
		}
		return classifierSupplier;
	}

	// -------------------------------------private methods
	private void areChromatogramClassifiersStored() throws NoChromatogramClassifierSupplierAvailableException {

		if(suppliers.isEmpty()) {
			throw new NoChromatogramClassifierSupplierAvailableException();
		}
	}
	// -------------------------------------private methods
}
