/*******************************************************************************
 * Copyright (c) 2013, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Christoph Läubrich - Fix invalid string check, support supplier without file extension
 *******************************************************************************/
package org.eclipse.chemclipse.processing.converter;

import java.io.File;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.support.text.ValueFormat;

public abstract class AbstractSupplierFileIdentifier implements ISupplierFileIdentifier {

	private static final Logger logger = Logger.getLogger(AbstractSupplierFileIdentifier.class);
	private final List<ISupplier> suppliers;
	private final NumberFormat timeFormat = ValueFormat.getDecimalFormatEnglish("0.000");

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

	@Override
	public Collection<ISupplier> getSupplier(File file) {

		List<ISupplier> list = new ArrayList<>();
		if(file.isFile()) {
			for(ISupplier supplier : getSupplier()) {
				if(isValidFileSupplier(file, supplier) && isMatchMagicNumber(file)) {
					list.add(supplier);
				}
			}
		} else if(file.isDirectory()) {
			for(ISupplier supplier : getSupplier()) {
				if(isValidDirectorySupplier(file, supplier) && isMatchMagicNumber(file)) {
					list.add(supplier);
				}
			}
		}
		return list;
	}

	protected static boolean isValidFileSupplier(File file, ISupplier supplier) {

		String extension = file.toString().toLowerCase();
		String supplierExtension = supplier.getFileExtension().toLowerCase();
		boolean hasExtension = supplierExtension != null && !supplierExtension.isEmpty();
		if(hasExtension) {
			if(supplierExtension.contains(ISupplier.WILDCARD_NUMBER)) {
				/*
				 * E.g. *.r## is a matcher for *.r01, *.r02 ...
				 */
				String extensionMatcher = ISupplierFileIdentifier.getExtensionMatcher(supplierExtension);
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
				if(!"".equals(directoryExtension) && directory.endsWith(directoryExtension)) {
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
			long start = System.currentTimeMillis();
			boolean matched = supplier.isMatchMagicNumber(file);
			long end = System.currentTimeMillis();
			long spent = end - start;
			if(spent > 10) {
				logger.info("Magic number check of " + file.getName() + " by " + supplier.getFilterName() + " took " + timeFormat.format(spent / 1000.0d) + " seconds.");
			}
			if(matched) {
				return true;
			}
		}
		//
		return false;
	}

	@Override
	public boolean isMatchContent(File file) {

		for(ISupplier supplier : getSupplier()) {
			long start = System.currentTimeMillis();
			boolean matched = supplier.isMatchContent(file);
			long end = System.currentTimeMillis();
			long spent = end - start;
			if(spent > 100) {
				logger.info("File content check of " + file.getName() + " by " + supplier.getFilterName() + " took " + timeFormat.format(spent / 1000.0d) + " seconds.");
			}
			if(supplier.isMatchMagicNumber(file) && supplier.isMatchContent(file)) {
				return true;
			}
		}
		//
		return false;
	}
}