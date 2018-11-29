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
package org.eclipse.chemclipse.converter.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.converter.support.FileExtensionCompiler;
import org.eclipse.chemclipse.support.util.FileUtil;

public abstract class AbstractConverterSupport implements IConverterSupportSetter {

	private List<ISupplier> suppliers;
	private Map<String, String> regularExpressions;

	public AbstractConverterSupport() {
		suppliers = new ArrayList<ISupplier>();
		regularExpressions = new HashMap<String, String>();
	}

	/**
	 * Gets e.g.
	 * .r##
	 * and returns
	 * .*\\.r[0-9][0-9]
	 */
	public static String getExtensionMatcher(String supplierExtension) {

		String extensionMatcher = supplierExtension.replaceAll(WILDCARD_NUMBER, "[0-9]");
		return extensionMatcher.replace(".", ".*\\.");
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
			} else {
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
				} else {
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
	public List<String> getAvailableConverterIds(final File file) throws NoConverterAvailableException {

		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		areConvertersStored();
		List<String> availableConverters = new ArrayList<String>();
		String fileName = file.getName();
		for(ISupplier supplier : suppliers) {
			if(file.isDirectory()) {
				/*
				 * Enable to read directories whether they end with lower or
				 * upper case letters.
				 */
				String directoryExtension = supplier.getDirectoryExtension();
				if(fileName.endsWith(directoryExtension) || fileName.endsWith(directoryExtension.toLowerCase()) || fileName.endsWith(directoryExtension.toUpperCase())) {
					availableConverters.add(supplier.getId());
				} else {
					if(directoryExtension.contains(IConverterSupport.WILDCARD_NUMBER)) {
						//
						if(directoryExtension.startsWith(".")) {
							directoryExtension = directoryExtension.substring(1, directoryExtension.length());
						}
						//
						String[] directoryParts = directoryExtension.split("#");
						if(directoryParts.length > 0) {
							if(file.getName().matches(directoryParts[0])) {
								availableConverters.add(supplier.getId());
							}
						}
					}
				}
			} else {
				/*
				 * Check if the file has an extension or
				 * if the bare file name shall be used.
				 */
				if(FileUtil.fileHasExtension(file)) {
					/*
					 * Enable to read files whether they end with lower or upper
					 * case letters. Take care, files like *.cdf would cause no
					 * problem, as they could be *.cdf (lower case) or *.CDF (upper
					 * case).<br/> There are problems using files, e.g. *.ionXML. The
					 * name must exactly fit the file extension or must be all in
					 * lower case (*.ionxml) or in upper case (*.IonXML) notation.
					 */
					String fileExtension = supplier.getFileExtension();
					if(fileExtension == null || fileExtension.equals("")) {
						continue;
					} else {
						if(fileExtension.contains(WILDCARD_NUMBER)) {
							/*
							 * Get the matcher.
							 */
							String supplierExtension = fileExtension.toLowerCase();
							String extensionMatcher = regularExpressions.get(supplierExtension);
							if(extensionMatcher == null) {
								extensionMatcher = getExtensionMatcher(supplierExtension);
								regularExpressions.put(supplierExtension, extensionMatcher);
							}
							/*
							 * E.g. *.r## is a matcher for *.r01, *.r02 ...
							 */
							if(fileName.toLowerCase().matches(extensionMatcher)) {
								availableConverters.add(supplier.getId());
							}
						} else if(fileName.endsWith(fileExtension) || fileName.endsWith(fileExtension.toLowerCase()) || fileName.endsWith(fileExtension.toUpperCase())) {
							/*
							 * Normal handling
							 */
							availableConverters.add(supplier.getId());
						}
					}
				} else {
					/*
					 * Some files do not have an extension, e.g. Bruker Flex (fid).
					 */
					String supplierFileName = supplier.getFileName().toLowerCase();
					if(supplierFileName == null || supplierFileName.equals("")) {
						continue;
					} else {
						if(fileName.endsWith(supplierFileName) || fileName.endsWith(supplierFileName.toLowerCase()) || fileName.endsWith(supplierFileName.toUpperCase())) {
							availableConverters.add(supplier.getId());
						}
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
