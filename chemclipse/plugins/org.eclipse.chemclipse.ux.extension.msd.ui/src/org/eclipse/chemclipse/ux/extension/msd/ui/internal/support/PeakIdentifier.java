/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.internal.support;

import java.io.File;
import java.util.List;

import org.eclipse.chemclipse.converter.core.ISupplier;
import org.eclipse.chemclipse.msd.converter.peak.PeakConverterMSD;

public class PeakIdentifier {

	private static List<ISupplier> suppliers;

	/**
	 * Returns true if the file is a chromatogram as stored in enum
	 * PeakSupplier. Returns false if not.
	 * 
	 * @param file
	 * @return boolean
	 */
	public static boolean isPeak(File file) {

		String extension = file.toString().toLowerCase();
		String supplierExtension;
		/*
		 * All directories are stored in upper cases.
		 */
		if(file.isDirectory()) {
			return false;
		}
		if(suppliers == null) {
			suppliers = PeakConverterMSD.getPeakConverterSupport().getSupplier();
		} else {
			for(ISupplier supplier : suppliers) {
				supplierExtension = supplier.getFileExtension().toLowerCase();
				if(supplierExtension != "" && extension.endsWith(supplierExtension)) {
					if(supplier.isImportable()) {
						return true;
					} else {
						return false;
					}
				}
			}
		}
		return false;
	}

	/**
	 * Returns true if the file is a directory and a peak list. Returns false if
	 * not.
	 * 
	 * @param file
	 * @return boolean
	 */
	public static boolean isPeakDirectory(File file) {

		String directory = file.toString().toUpperCase();
		String directoryExtension;
		/*
		 * All directories are stored in upper cases.
		 */
		if(!file.isDirectory()) {
			return false;
		}
		if(suppliers == null) {
			suppliers = PeakConverterMSD.getPeakConverterSupport().getSupplier();
		} else {
			for(ISupplier supplier : suppliers) {
				directoryExtension = supplier.getDirectoryExtension().toUpperCase();
				if(directoryExtension != "" && directory.endsWith(directoryExtension)) {
					if(supplier.isImportable()) {
						return true;
					} else {
						return false;
					}
				}
			}
		}
		return false;
	}
}
