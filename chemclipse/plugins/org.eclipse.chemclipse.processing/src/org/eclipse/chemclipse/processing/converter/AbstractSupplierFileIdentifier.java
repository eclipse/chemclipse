/*******************************************************************************
 * Copyright (c) 2013, 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - Fix invalid string check, support supplier without file extension
 *******************************************************************************/
package org.eclipse.chemclipse.processing.converter;

import java.io.File;
import java.util.Collection;
import java.util.List;

public abstract class AbstractSupplierFileIdentifier implements ISupplierFileIdentifier {

	private final List<ISupplier> suppliers;

	public AbstractSupplierFileIdentifier(List<ISupplier> suppliers) {
		this.suppliers = suppliers;
	}

	@Override
	public boolean isSupplierFile(File file) {

		if(file.isFile()) {
			for(ISupplier supplier : getSupplier()) {
				if(isValidFileSupplier(file, supplier)) {
					return true;
				}
			}
		} else if(file.isDirectory()) {
			for(ISupplier supplier : getSupplier()) {
				if(isValidDirectorySupplier(file, supplier)) {
					return true;
				}
			}
		}
		return false;
	}

	protected static boolean isValidFileSupplier(File file, ISupplier supplier) {

		// FIXME what is the difference to org.eclipse.chemclipse.converter.core.Converter.getSupplierForFile(File, Iterable<? extends ISupplier>) method, can we join the codes?
		String extension = file.toString().toLowerCase();
		String supplierExtension = supplier.getFileExtension().toLowerCase();
		boolean hasExtension = supplierExtension != null && !supplierExtension.isEmpty();
		if(hasExtension) {
			if(supplierExtension.contains(ISupplier.WILDCARD_NUMBER)) {
				/*
				 * Get the matcher.
				 */
				String extensionMatcher = getExtensionMatcher(supplierExtension);
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
				}
			}
		} else {
			return supplier.isImportable();
		}
		return false;
	}

	protected static boolean isValidDirectorySupplier(File file, ISupplier supplier) {

		String directory = file.toString().toUpperCase();
		String directoryExtension = supplier.getDirectoryExtension();
		if(!"".equals(directoryExtension)) {
			if(directoryExtension.contains(ISupplier.WILDCARD_NUMBER)) {
				/*
				 * (0_[a-zA-Z][0-9]+)#([1-9]+)#1SLin
				 */
				if(directoryExtension.startsWith(".")) {
					directoryExtension = directoryExtension.substring(1, directoryExtension.length());
				}
				String[] directoryParts = directoryExtension.split("#");
				return isDirectoryPatternMatch(file, directoryParts, 0);
			} else {
				directoryExtension = directoryExtension.toUpperCase();
				if(directoryExtension != "" && directory.endsWith(directoryExtension)) {
					if(supplier.isImportable()) {
						return true;
					}
				}
			}
		}
		return false;
	}

	@Override
	public Collection<ISupplier> getSupplier() {

		return suppliers;
	}

	public boolean isSupplierFileDirectory(File file) {

		return isSupplierFileDirectory(file);
	}

	private static boolean isDirectoryPatternMatch(File file, String[] directoryParts, int index) {

		if(file.isDirectory()) {
			if(index < directoryParts.length) {
				if(file.getName().matches(directoryParts[index])) {
					index++;
					if(index == directoryParts.length) {
						return true;
					} else {
						File[] listFiles = file.listFiles();
						if(listFiles != null) {
							for(File subFile : listFiles) {
								if(isDirectoryPatternMatch(subFile, directoryParts, index)) {
									return true;
								}
							}
						}
					}
				}
			}
		}
		return false;
	}

	@Override
	public boolean isMatchMagicNumber(File file) {

		for(ISupplier supplier : getSupplier()) {
			if(supplier.isMatchMagicNumber(file)) {
				return true;
			}
		}
		return false;
	}

	private static String getExtensionMatcher(String supplierExtension) {

		String extensionMatcher = supplierExtension.replaceAll(ISupplier.WILDCARD_NUMBER, "[0-9]");
		return extensionMatcher.replace(".", ".*\\.");
	}
}
