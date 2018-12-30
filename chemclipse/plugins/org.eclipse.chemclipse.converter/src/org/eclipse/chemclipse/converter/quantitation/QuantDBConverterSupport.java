/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.converter.quantitation;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.converter.core.ISupplier;
import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.support.util.FileUtil;

public class QuantDBConverterSupport implements IQuantDBConverterSupport {

	private List<ISupplier> suppliers;

	public QuantDBConverterSupport() {
		suppliers = new ArrayList<ISupplier>();
	}

	@Override
	public void add(final ISupplier supplier) {

		suppliers.add(supplier);
	}

	@Override
	public String getConverterId(final int index) throws NoConverterAvailableException {

		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		areConvertersStored();
		/*
		 * Test if the index is out of range.
		 */
		if(index < 0 || index > suppliers.size() - 1) {
			throw new NoConverterAvailableException("There is no converter available.");
		}
		ISupplier supplier = suppliers.get(index);
		return supplier.getId();
	}

	@Override
	public String getConverterId(final String name) throws NoConverterAvailableException {

		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		areConvertersStored();
		/*
		 * Get the supplier by name.
		 */
		String id = "";
		breakloop:
		for(ISupplier supplier : suppliers) {
			if(supplier.getFilterName().equals(name)) {
				id = supplier.getId();
				break breakloop;
			}
		}
		/*
		 * If id is empty.
		 */
		if(id.equals("")) {
			throw new NoConverterAvailableException("There is no converter available.");
		}
		return id;
	}

	@Override
	public String[] getFilterExtensions() throws NoConverterAvailableException {

		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		areConvertersStored();
		ArrayList<String> extensions = new ArrayList<String>();
		for(ISupplier supplier : suppliers) {
			extensions.add(supplier.getFileExtension());
		}
		return extensions.toArray(new String[extensions.size()]);
	}

	@Override
	public String[] getFilterNames() throws NoConverterAvailableException {

		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		areConvertersStored();
		/*
		 * If the ArrayList is not empty, return the registered chromatogram
		 * converter filter names.<br/>
		 */
		ArrayList<String> filterNames = new ArrayList<String>();
		for(ISupplier supplier : suppliers) {
			filterNames.add(supplier.getFilterName());
		}
		return filterNames.toArray(new String[filterNames.size()]);
	}

	@Override
	public List<String> getAvailableConverterIds(final File file) throws NoConverterAvailableException {

		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		areConvertersStored();
		List<String> availableConverters = new ArrayList<String>();
		String fileName = file.getName();
		for(ISupplier supplier : suppliers) {
			/*
			 * Check if the file has an extension or
			 * if the bare file name shall be used.
			 */
			if(FileUtil.fileHasExtension(file)) {
				String fileExtension = supplier.getFileExtension();
				if(fileExtension == null || fileExtension.equals("")) {
					continue;
				} else {
					if(fileName.endsWith(fileExtension) || fileName.endsWith(fileExtension.toLowerCase()) || fileName.endsWith(fileExtension.toUpperCase())) {
						/*
						 * Normal handling
						 */
						availableConverters.add(supplier.getId());
					}
				}
			}
		}
		if(availableConverters.isEmpty()) {
			throw new NoConverterAvailableException("There is no converter available to process the file: " + file.toString());
		}
		return availableConverters;
	}

	@Override
	public List<ISupplier> getSupplier() {

		return suppliers;
	}

	@Override
	public ISupplier getSupplier(String id) throws NoConverterAvailableException {

		ISupplier instance = null;
		exitloop:
		for(ISupplier supplier : suppliers) {
			if(supplier.getId().equals(id)) {
				instance = supplier;
				break exitloop;
			}
		}
		/*
		 * Throw an exception if the requested converter is not available.
		 */
		if(instance == null) {
			throw new NoConverterAvailableException("There is no converter available with the given id: " + id + ".");
		}
		return instance;
	}

	/**
	 * Check if there are converters stored in the
	 * ArrayList<IChromatogramSupplier>.
	 * 
	 * @throws NoConverterAvailableException
	 */
	private void areConvertersStored() throws NoConverterAvailableException {

		if(suppliers.size() < 1) {
			throw new NoConverterAvailableException();
		}
	}
}
