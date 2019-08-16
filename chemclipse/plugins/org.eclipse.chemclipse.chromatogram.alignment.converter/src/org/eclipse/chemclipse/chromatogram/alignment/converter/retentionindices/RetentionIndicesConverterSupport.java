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
package org.eclipse.chemclipse.chromatogram.alignment.converter.retentionindices;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.alignment.converter.exceptions.NoRetentionIndicesConverterAvailableException;

/**
 * This class encapsulated information about the registered retention indices
 * converters.<br/>
 * It offers some convenient methods for getting information about the
 * registered converters.<br/>
 * SWT FileDialog may use it to set the correct dialog information.
 * 
 * @author eselmeister
 */
public class RetentionIndicesConverterSupport implements IRetentionIndicesConverterSupport {

	private List<IRetentionIndicesSupplier> suppliers;

	public RetentionIndicesConverterSupport() {
		suppliers = new ArrayList<IRetentionIndicesSupplier>();
	}

	/**
	 * Adds a ({@link IRetentionIndicesSupplier}) to the
	 * RetentionIndicesConverterSupport.
	 * 
	 * @param supplier
	 */
	protected void add(final IRetentionIndicesSupplier supplier) {

		suppliers.add(supplier);
	}

	@Override
	public String getConverterId(final int index) throws NoRetentionIndicesConverterAvailableException {

		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		areConvertersStored();
		/*
		 * Test if the index is out of range.
		 */
		if(index < 0 || index > suppliers.size() - 1) {
			throw new NoRetentionIndicesConverterAvailableException("The index: " + index + " is out of range.");
		}
		IRetentionIndicesSupplier supplier = suppliers.get(index);
		return supplier.getId();
	}

	@Override
	public String[] getFilterExtensions() throws NoRetentionIndicesConverterAvailableException {

		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		areConvertersStored();
		/*
		 * If the ArrayList is not empty, return the registered chromatogram
		 * converter extensions.<br/> Note, if a supported chromatogram is
		 * stored in a directory, the directory extension will be listed.<br/>
		 * Otherwise the fileExtension will be listed.
		 */
		String extension;
		ArrayList<String> extensions = new ArrayList<String>();
		for(IRetentionIndicesSupplier supplier : suppliers) {
			extension = supplier.getFileExtension();
			extensions.add(extension);
		}
		return extensions.toArray(new String[extensions.size()]);
	}

	@Override
	public String[] getFilterNames() throws NoRetentionIndicesConverterAvailableException {

		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		areConvertersStored();
		/*
		 * If the ArrayList is not empty, return the registered chromatogram
		 * converter filter names.<br/>
		 */
		ArrayList<String> filterNames = new ArrayList<String>();
		for(IRetentionIndicesSupplier supplier : suppliers) {
			filterNames.add(supplier.getFilterName());
		}
		return filterNames.toArray(new String[filterNames.size()]);
	}

	@Override
	public List<String> getAvailableConverterIds(final File retentionIndices) throws NoRetentionIndicesConverterAvailableException {

		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		areConvertersStored();
		List<String> availableConverters = new ArrayList<String>();
		for(IRetentionIndicesSupplier supplier : suppliers) {
			if(retentionIndices.getName().endsWith(supplier.getFileExtension())) {
				availableConverters.add(supplier.getId());
			}
		}
		if(availableConverters.isEmpty()) {
			throw new NoRetentionIndicesConverterAvailableException("There is no converter for the chromatogram file " + retentionIndices + " available.");
		}
		return availableConverters;
	}

	/**
	 * Check if there are converters stored in the
	 * ArrayList<IRetentionIndicesSupplier>.
	 * 
	 * @throws NoRetentionIndicesConverterAvailableException
	 */
	private void areConvertersStored() throws NoRetentionIndicesConverterAvailableException {

		if(suppliers.size() < 1) {
			throw new NoRetentionIndicesConverterAvailableException();
		}
	}
}
