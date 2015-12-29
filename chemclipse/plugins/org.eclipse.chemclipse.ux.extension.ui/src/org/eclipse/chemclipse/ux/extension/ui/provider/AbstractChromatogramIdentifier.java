/*******************************************************************************
 * Copyright (c) 2013, 2015 Dr. Philip Wenig.
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
import java.util.List;

import org.eclipse.chemclipse.converter.core.ISupplier;

public abstract class AbstractChromatogramIdentifier implements IChromatogramIdentifier {

	private List<ISupplier> suppliers;

	public AbstractChromatogramIdentifier(List<ISupplier> suppliers) {
		this.suppliers = suppliers;
	}

	@Override
	public boolean isChromatogram(File file) {

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
			if(supplierExtension != "" && extension.endsWith(supplierExtension)) {
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
	public boolean isChromatogramDirectory(File file) {

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
}
