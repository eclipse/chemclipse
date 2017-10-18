/*******************************************************************************
 * Copyright (c) 2013, 2017 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.provider;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.chemclipse.converter.core.AbstractConverterSupport;
import org.eclipse.chemclipse.converter.core.IConverterSupport;
import org.eclipse.chemclipse.converter.core.ISupplier;

public abstract class AbstractChromatogramIdentifier implements ISupplierFileIdentifier {

	private List<ISupplier> suppliers;
	private Map<String, String> regularExpressions;

	public AbstractChromatogramIdentifier(List<ISupplier> suppliers) {
		this.suppliers = suppliers;
		regularExpressions = new HashMap<String, String>();
	}

	@Override
	public boolean isSupplierFile(File file) {

		String extension = file.toString().toLowerCase();
		String supplierExtension;
		/*
		 * All directories are stored in upper cases.
		 */
		if(file.isDirectory()) {
			return false;
		}
		/*
		 * Check each supplier.
		 */
		for(ISupplier supplier : suppliers) {
			supplierExtension = supplier.getFileExtension().toLowerCase();
			if(supplierExtension != "") {
				if(supplierExtension.contains(IConverterSupport.WILDCARD_NUMBER)) {
					/*
					 * Get the matcher.
					 */
					String extensionMatcher = regularExpressions.get(supplierExtension);
					if(extensionMatcher == null) {
						extensionMatcher = AbstractConverterSupport.getExtensionMatcher(supplierExtension);
						regularExpressions.put(supplierExtension, extensionMatcher);
					}
					/*
					 * E.g. *.r## is a matcher for *.r01, *.r02 ...
					 */
					if(extension.matches(extensionMatcher)) {
						return supplier.isImportable();
					}
				} else if(extension.endsWith(supplierExtension)) {
					/*
					 * Test various implementations.
					 */
					if(supplier.isImportable()) {
						return true;
					} else {
						/*
						 * Try to find a supplier which is capable
						 * to read the data.
						 */
						for(ISupplier specificSupplier : suppliers) {
							if(extension.endsWith(specificSupplier.getFileExtension())) {
								if(specificSupplier.isImportable()) {
									return true;
								}
							}
						}
					}
				}
			}
		}
		/*
		 * If no converter was found, return false.
		 */
		return false;
	}

	@Override
	public boolean isSupplierFileDirectory(File file) {

		String directory = file.toString().toUpperCase();
		String directoryExtension;
		/*
		 * All directories are stored in upper cases.
		 */
		if(!file.isDirectory()) {
			return false;
		}
		/*
		 * Check each supplier.
		 */
		for(ISupplier supplier : suppliers) {
			directoryExtension = supplier.getDirectoryExtension().toUpperCase();
			if(directoryExtension != "" && directory.endsWith(directoryExtension)) {
				if(supplier.isImportable()) {
					return true;
				}
			}
		}
		/*
		 * If no converter was found, return false.
		 */
		return false;
	}

	@Override
	public boolean isMatchMagicNumber(File file) {

		for(ISupplier supplier : suppliers) {
			if(supplier.isMatchMagicNumber(file)) {
				return true;
			}
		}
		return false;
	}
}
