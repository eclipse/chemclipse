/*******************************************************************************
 * Copyright (c) 2008, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Christoph Läubrich - move code from AbstracConverterSupport
 *******************************************************************************/
package org.eclipse.chemclipse.converter.core;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.chemclipse.converter.exceptions.NoConverterAvailableException;
import org.eclipse.chemclipse.processing.converter.ISupplier;
import org.eclipse.chemclipse.processing.converter.ISupplierFileIdentifier;
import org.eclipse.chemclipse.support.util.FileUtil;

public class Converter {

	/*
	 * These are the attributes of the extension point elements.
	 */
	public static final String ID = "id"; //$NON-NLS-1$
	public static final String DESCRIPTION = "description"; //$NON-NLS-1$
	public static final String FILTER_NAME = "filterName"; //$NON-NLS-1$
	public static final String FILE_EXTENSION = "fileExtension"; //$NON-NLS-1$
	public static final String FILE_NAME = "fileName"; //$NON-NLS-1$
	public static final String DIRECTORY_EXTENSION = "directoryExtension"; //$NON-NLS-1$
	public static final String IS_EXPORTABLE = "isExportable"; //$NON-NLS-1$
	public static final String IS_IMPORTABLE = "isImportable"; //$NON-NLS-1$
	public static final String EXPORT_CONVERTER = "exportConverter"; //$NON-NLS-1$
	public static final String IMPORT_CONVERTER = "importConverter"; //$NON-NLS-1$
	public static final String IMPORT_MAGIC_NUMBER_MATCHER = "importMagicNumberMatcher"; //$NON-NLS-1$
	public static final String IMPORT_FILE_CONTENT_MATCHER = "importContentMatcher"; //$NON-NLS-1$

	/**
	 * This class has only static methods.
	 */
	private Converter() {

	}

	/**
	 * This method return true if the input string contains a not allowed
	 * character like \/:*?"<>| It returns true if the input string is a valid
	 * string and false if not.<br/>
	 * If the input string is null it returns false.
	 * 
	 * @return boolean
	 */
	public static boolean isValid(final String input) {

		if(input == null) {
			return false;
		}
		/*
		 * Use four times backslash to search after a normal backslash. See
		 * "Mastering Regular Expressions" from Jeffrey Friedl, ISBN:
		 * 0596528124.
		 */
		String regex = "[\\\\/:*?\"<>|]";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher(input);
		return !matcher.find();
	}

	public static List<ISupplier> getSupplierForFile(final File file, Iterable<? extends ISupplier> suppliers) throws NoConverterAvailableException {

		HashMap<String, String> regularExpressions = new HashMap<>();
		/*
		 * Test if the suppliers ArrayList is empty.
		 */
		List<ISupplier> availableConverters = new ArrayList<>();
		String fileName = file.getName();
		for(ISupplier supplier : suppliers) {
			if(file.isDirectory()) {
				/*
				 * Enable to read directories whether they end with lower or
				 * upper case letters.
				 */
				String directoryExtension = supplier.getDirectoryExtension();
				if(directoryExtension.isEmpty()) {
					continue;
				}
				if(fileName.endsWith(directoryExtension) || fileName.endsWith(directoryExtension.toLowerCase()) || fileName.endsWith(directoryExtension.toUpperCase())) {
					availableConverters.add(supplier);
				} else {
					if(directoryExtension.contains(ISupplier.WILDCARD_NUMBER)) {
						//
						if(directoryExtension.startsWith(".")) {
							directoryExtension = directoryExtension.substring(1, directoryExtension.length());
						}
						//
						String[] directoryParts = directoryExtension.split("#");
						if(directoryParts.length > 0) {
							if(file.getName().matches(directoryParts[0])) {
								availableConverters.add(supplier);
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
					String fileExtension = supplier.getFileExtension();
					if(fileExtension == null || fileExtension.equals("")) {
						continue;
					} else {
						if(fileExtension.contains(ISupplier.WILDCARD_NUMBER)) {
							/*
							 * E.g. *.r## is a matcher for *.r01, *.r02 ...
							 */
							String supplierExtension = fileExtension.toLowerCase();
							String extensionMatcher = regularExpressions.get(supplierExtension);
							if(extensionMatcher == null) {
								extensionMatcher = ISupplierFileIdentifier.getExtensionMatcher(supplierExtension);
								regularExpressions.put(supplierExtension, extensionMatcher);
							}
							//
							if(fileName.toLowerCase().matches(extensionMatcher)) {
								availableConverters.add(supplier);
							}
						} else if(fileName.endsWith(fileExtension) || fileName.endsWith(fileExtension.toLowerCase()) || fileName.endsWith(fileExtension.toUpperCase())) {
							/*
							 * Normal handling
							 */
							availableConverters.add(supplier);
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
							availableConverters.add(supplier);
						}
					}
				}
			}
		}
		//
		if(availableConverters.isEmpty()) {
			throw new NoConverterAvailableException("There is no converter available to process the file: " + file.toString());
		}
		//
		return availableConverters;
	}
}