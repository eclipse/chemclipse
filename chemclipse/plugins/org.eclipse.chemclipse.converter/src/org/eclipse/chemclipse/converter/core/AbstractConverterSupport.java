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
 * Christoph Läubrich - move implementation of getAvailableConverterIds to Converter for more general use cases
 *******************************************************************************/
package org.eclipse.chemclipse.converter.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.converter.support.FileExtensionCompiler;
import org.eclipse.chemclipse.processing.converter.ISupplier;
import org.eclipse.chemclipse.support.settings.OperatingSystemUtils;

public abstract class AbstractConverterSupport implements IConverterSupportSetter {

	private final List<ISupplier> suppliers;

	public AbstractConverterSupport() {

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
	public String getConverterId(final String name, boolean exportConverterOnly) throws NoConverterAvailableException {

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
				if(exportConverterOnly) {
					if(supplier.isExportable()) {
						id = supplier.getId();
						break breakloop;
					}
				} else {
					id = supplier.getId();
					break breakloop;
				}
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
		/*
		 * If the ArrayList is not empty, return the registered chromatogram
		 * converter extensions.<br/> Note, if a supported chromatogram is
		 * stored in a directory, the directory extension will be listed.<br/>
		 * Otherwise the fileExtension will be listed.
		 */
		ArrayList<String> extensions = new ArrayList<String>();
		for(ISupplier supplier : suppliers) {
			if(supplier.getDirectoryExtension().equals("")) {
				FileExtensionCompiler fileExtensionCompiler = new FileExtensionCompiler(supplier.getFileExtension(), true);
				extensions.add(fileExtensionCompiler.getCompiledFileExtension());
			} else if(OperatingSystemUtils.isWindows()) {
				/*
				 * DirectoryExtension: Directory extension will return "*."
				 * otherwise directory could not be identified under
				 * Windows.
				 */
				extensions.add("*.");
			}
		}
		return extensions.toArray(new String[extensions.size()]);
	}

	@Override
	public String[] getExportableFilterExtensions() throws NoConverterAvailableException {

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
		FileExtensionCompiler fileExtensionCompiler;
		ArrayList<String> extensions = new ArrayList<String>();
		for(ISupplier supplier : suppliers) {
			if(supplier.isExportable()) {
				/*
				 * Differentiate between directory and file extensions.
				 */
				if(supplier.getDirectoryExtension().equals("")) {
					/*
					 * FileExtension: See
					 * org.eclipse.swt.widgets.FileDialog.<br/> The chromatogram
					 * files shall be visible by their lower and upper case
					 * extensions (e.g. "*.cdf" and "*.CDF").<br/> In this case,
					 * "*.cdf;*.CDF" will be returned.<br/> Use the semicolon
					 * instead of adding lower and upper case extension
					 * separately, cause the FileDialog connects the filter
					 * extensions with the filter names (export converters).
					 * Mixed lower and upper case extension must match exact the
					 * given converter supplier extension point declaration,
					 * e.g. *.ionXML. <br/> Example:<br/>
					 * supplier.getFileExtension(): ".ionXML"<br/>
					 * fileExtensionCompiler.getCompiledFileExtension():
					 * "*.ionXML;*.ionxml;*.IonXML"
					 */
					fileExtensionCompiler = new FileExtensionCompiler(supplier.getFileExtension(), true);
					extensions.add(fileExtensionCompiler.getCompiledFileExtension());
				} else if(OperatingSystemUtils.isWindows()) {
					/*
					 * DirectoryExtension: Directory extension will return "*."
					 * otherwise directory could not be identified under
					 * Windows.
					 */
					extensions.add("*.");
				}
			}
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
	public String[] getExportableFilterNames() throws NoConverterAvailableException {

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
			if(supplier.isExportable()) {
				filterNames.add(supplier.getFilterName());
			}
		}
		return filterNames.toArray(new String[filterNames.size()]);
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

	@Override
	public List<ISupplier> getExportSupplier() {

		List<ISupplier> exportSupplier = new ArrayList<ISupplier>();
		/*
		 * See also: getExportableFilterNames
		 */
		for(ISupplier supplier : suppliers) {
			if(supplier.isExportable()) {
				exportSupplier.add(supplier);
			}
		}
		return exportSupplier;
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
