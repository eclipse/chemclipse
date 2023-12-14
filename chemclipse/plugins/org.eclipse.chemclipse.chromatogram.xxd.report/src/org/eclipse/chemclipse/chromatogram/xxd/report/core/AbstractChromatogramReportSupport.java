/*******************************************************************************
 * Copyright (c) 2012, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.report.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.report.exceptions.NoReportSupplierAvailableException;

public abstract class AbstractChromatogramReportSupport implements IChromatogramReportSupportSetter {

	private List<IChromatogramReportSupplier> suppliers;

	public AbstractChromatogramReportSupport() {

		suppliers = new ArrayList<>();
	}

	@Override
	public void add(final IChromatogramReportSupplier supplier) {

		suppliers.add(supplier);
	}

	@Override
	public String[] getReportExtensions() throws NoReportSupplierAvailableException {

		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		areReportSuppliersStored();
		/*
		 * If the ArrayList is not empty, return the registered chromatogram
		 * report extensions.
		 */
		String extension;
		ArrayList<String> extensions = new ArrayList<String>();
		for(IChromatogramReportSupplier supplier : suppliers) {
			extension = supplier.getFileExtension();
			extensions.add(extension);
		}
		return extensions.toArray(new String[extensions.size()]);
	}

	@Override
	public String[] getFilterNames() throws NoReportSupplierAvailableException {

		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		areReportSuppliersStored();
		/*
		 * If the ArrayList is not empty, return the registered chromatogram
		 * report names.<br/>
		 */
		ArrayList<String> filterNames = new ArrayList<>();
		for(IChromatogramReportSupplier supplier : suppliers) {
			filterNames.add(supplier.getReportName());
		}
		return filterNames.toArray(new String[filterNames.size()]);
	}

	@Override
	public String getReportSupplierId(int index) throws NoReportSupplierAvailableException {

		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		areReportSuppliersStored();
		/*
		 * Test if the index is out of range.
		 */
		if(index < 0 || index > suppliers.size() - 1) {
			throw new NoReportSupplierAvailableException("The index is out of range.");
		}
		IChromatogramReportSupplier supplier = suppliers.get(index);
		return supplier.getId();
	}

	@Override
	public String getReportSupplierId(String name) throws NoReportSupplierAvailableException {

		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		areReportSuppliersStored();
		/*
		 * Get the supplier by name.
		 */
		String id = "";
		breakloop:
		for(IChromatogramReportSupplier supplier : suppliers) {
			if(supplier.getReportName().equals(name)) {
				id = supplier.getId();
				break breakloop;
			}
		}
		/*
		 * If id is empty.
		 */
		if(id.equals("")) {
			throw new NoReportSupplierAvailableException("There is no chromatogram report generator available.");
		}
		return id;
	}

	@Override
	public List<IChromatogramReportSupplier> getReportSupplier() {

		return suppliers;
	}

	@Override
	public IChromatogramReportSupplier getReportSupplier(String id) throws NoReportSupplierAvailableException {

		IChromatogramReportSupplier instance = null;
		exitloop:
		for(IChromatogramReportSupplier supplier : suppliers) {
			if(supplier.getId().equals(id)) {
				instance = supplier;
				break exitloop;
			}
		}
		/*
		 * Throw an exception if the requested converter is not available.
		 */
		if(instance == null) {
			throw new NoReportSupplierAvailableException("There is no chromatogram report generator available with the given id: " + id + ".");
		}
		return instance;
	}

	@Override
	public List<String> getAvailableProcessorIds() throws NoReportSupplierAvailableException {

		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		areReportSuppliersStored();
		List<String> availableProcessors = new ArrayList<>();
		for(IChromatogramReportSupplier supplier : suppliers) {
			availableProcessors.add(supplier.getId());
		}
		return availableProcessors;
	}

	/**
	 * Check if there are report generators stored in the
	 * ArrayList<IChromatogramReportSupplier>.
	 * 
	 * @throws NoReportSupplierAvailableException
	 */
	private void areReportSuppliersStored() throws NoReportSupplierAvailableException {

		if(suppliers.isEmpty()) {
			throw new NoReportSupplierAvailableException();
		}
	}
}
