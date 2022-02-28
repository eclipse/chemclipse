/*******************************************************************************
 * Copyright (c) 2013, 2022 Lablicate GmbH.
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

import org.eclipse.chemclipse.msd.converter.massspectrum.MassSpectrumConverter;
import org.eclipse.chemclipse.processing.converter.ISupplier;
import org.eclipse.chemclipse.support.util.FileUtil;

public class MassSpectrumIdentifier {

	private static List<ISupplier> suppliers;

	/**
	 * Returns true if the file is a mass spectrum
	 * Returns false if not.
	 * 
	 * @param file
	 * @return boolean
	 */
	public static boolean isMassSpectrum(File file) {

		/*
		 * Check if it is a directory.
		 */
		if(file.isDirectory()) {
			return false;
		}
		/*
		 * It is a file.
		 */
		if(suppliers == null) {
			suppliers = MassSpectrumConverter.getMassSpectrumConverterSupport().getSupplier();
		} else {
			String baseFileName = file.toString().toLowerCase();
			/*
			 * Check if the file has an extension.
			 */
			if(FileUtil.fileHasExtension(file)) {
				/*
				 * Test the extensions
				 */
				for(ISupplier supplier : suppliers) {
					String supplierExtension = supplier.getFileExtension().toLowerCase();
					if(!supplierExtension.isEmpty() && baseFileName.endsWith(supplierExtension)) {
						if(supplier.isImportable()) {
							return true;
						}
					}
				}
			} else {
				/*
				 * Test the file names.
				 */
				for(ISupplier supplier : suppliers) {
					String supplierFileName = supplier.getFileName().toLowerCase();
					if(!baseFileName.isEmpty() && baseFileName.endsWith(supplierFileName)) {
						if(supplier.isImportable()) {
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	/**
	 * Returns true if the file is a directory and a mass spectrum. Returns false
	 * if not.
	 * 
	 * @param file
	 * @return boolean
	 */
	public static boolean isMassSpectrumDirectory(File file) {

		String directory = file.toString().toUpperCase();
		String directoryExtension;
		/*
		 * All directories are stored in upper cases.
		 */
		if(!file.isDirectory()) {
			return false;
		}
		if(suppliers == null) {
			suppliers = MassSpectrumConverter.getMassSpectrumConverterSupport().getSupplier();
		} else {
			for(ISupplier supplier : suppliers) {
				directoryExtension = supplier.getDirectoryExtension().toUpperCase();
				if(!directoryExtension.isEmpty() && directory.endsWith(directoryExtension)) {
					if(supplier.isImportable()) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
