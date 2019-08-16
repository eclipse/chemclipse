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
package org.eclipse.chemclipse.chromatogram.msd.comparison.massspectrum;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.msd.comparison.exceptions.NoMassSpectrumComparatorAvailableException;

/**
 * This class gives you the information about the registered mass spectra
 * comparators.<br/>
 * A mass spectrum comparator can be used to determine the match quality of two
 * different mass spectra.
 * 
 * @author eselmeister
 */
public class MassSpectrumComparatorSupport implements IMassSpectrumComparatorSupport {

	private List<IMassSpectrumComparisonSupplier> suppliers;

	public MassSpectrumComparatorSupport() {
		suppliers = new ArrayList<IMassSpectrumComparisonSupplier>();
	}

	/**
	 * Adds a ({@link IMassSpectrumComparator}) to the {@link MassSpectrumComparatorSupport}.
	 * 
	 * @param supplier
	 */
	protected void add(final IMassSpectrumComparisonSupplier supplier) {

		suppliers.add(supplier);
	}

	@Override
	public String getComparatorId(int index) throws NoMassSpectrumComparatorAvailableException {

		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		areComparatorsStored();
		/*
		 * Test if the index is out of range.
		 */
		if(index < 0 || index > suppliers.size() - 1) {
			throw new NoMassSpectrumComparatorAvailableException("There is no mass spectrum comparator available with the following id: " + index + ".");
		}
		IMassSpectrumComparisonSupplier supplier = suppliers.get(index);
		return supplier.getId();
	}

	@Override
	public IMassSpectrumComparisonSupplier getMassSpectrumComparisonSupplier(String comparatorId) throws NoMassSpectrumComparatorAvailableException {

		IMassSpectrumComparisonSupplier comparisonSupplier = null;
		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		areComparatorsStored();
		/*
		 * Test if the index is out of range.
		 */
		if(comparatorId == null || comparatorId.equals("")) {
			throw new NoMassSpectrumComparatorAvailableException("There is no mass spectrum comparator available with the following id: " + comparatorId + ".");
		}
		endsearch:
		for(IMassSpectrumComparisonSupplier supplier : suppliers) {
			if(supplier.getId().equals(comparatorId)) {
				comparisonSupplier = supplier;
				break endsearch;
			}
		}
		if(comparisonSupplier == null) {
			throw new NoMassSpectrumComparatorAvailableException("There is no mass spectrum comparator available with the following id: " + comparatorId + ".");
		} else {
			return comparisonSupplier;
		}
	}

	@Override
	public List<String> getAvailableComparatorIds() throws NoMassSpectrumComparatorAvailableException {

		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		areComparatorsStored();
		List<String> availableComparators = new ArrayList<String>();
		for(IMassSpectrumComparisonSupplier supplier : suppliers) {
			availableComparators.add(supplier.getId());
		}
		return availableComparators;
	}

	public String[] getComparatorNames() throws NoMassSpectrumComparatorAvailableException {

		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		areComparatorsStored();
		/*
		 * If the ArrayList is not empty, return the registered chromatogram
		 * comparator filter names.<br/>
		 */
		ArrayList<String> comparatorNames = new ArrayList<String>();
		for(IMassSpectrumComparisonSupplier supplier : suppliers) {
			comparatorNames.add(supplier.getComparatorName());
		}
		return comparatorNames.toArray(new String[comparatorNames.size()]);
	}

	// -------------------------------------private methods
	/**
	 * Check if there are comparators stored in the
	 * ArrayList<IMassSpectrumComparisonSupplier>.
	 * 
	 * @throws NoMassSpectrumComparatorAvailableException
	 */
	private void areComparatorsStored() throws NoMassSpectrumComparatorAvailableException {

		if(suppliers.size() < 1) {
			throw new NoMassSpectrumComparatorAvailableException();
		}
	}
	// -------------------------------------private methods
}
