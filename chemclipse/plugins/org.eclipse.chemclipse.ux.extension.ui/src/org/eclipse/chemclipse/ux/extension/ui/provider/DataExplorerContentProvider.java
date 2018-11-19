/*******************************************************************************
 * Copyright (c) 2013, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.provider;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.chemclipse.xxd.process.files.ISupplierFileIdentifier;

public class DataExplorerContentProvider extends FileExplorerContentProvider {

	private List<? extends ISupplierFileIdentifier> supplierFileIdentifierList;

	public DataExplorerContentProvider(ISupplierFileIdentifier supplierFileIdentifier) {
		this(ExplorerListSupport.getSupplierFileIdentifierList(supplierFileIdentifier));
	}

	public DataExplorerContentProvider(List<? extends ISupplierFileIdentifier> supplierFileIdentifierList) {
		this.supplierFileIdentifierList = supplierFileIdentifierList;
	}

	@Override
	public File[] getFiles(File parentFile) {

		List<File> files = new ArrayList<File>();
		if(parentFile.isDirectory() && parentFile.canRead()) {
			/*
			 * I have found no method to monitor file system changes outside the
			 * workbench triggered by the operating system or users.<br/> It's a
			 * small overhead to reload the file content of directories each
			 * time they get the focus.<br/> There will be hopefully a better
			 * solution in the future.
			 */
			File updatedParentFile = new File(parentFile.toString());
			File[] fileList = updatedParentFile.listFiles();
			if(fileList != null) {
				for(File file : fileList) {
					if(!file.isHidden()) {
						/*
						 * Check if the file is a chromatogram/database.
						 * Do not list other files than chromatograms/databases.
						 */
						if(file.isDirectory()) {
							files.add(file);
						} else {
							/*
							 * Add MSD, CSD, WSD ... files.
							 */
							for(ISupplierFileIdentifier supplierFileIdentifier : supplierFileIdentifierList) {
								if(supplierFileIdentifier.isSupplierFile(file)) {
									if(supplierFileIdentifier.isMatchMagicNumber(file)) {
										files.add(file);
										break;
									}
								}
							}
						}
					}
				}
				File[] allFiles = new File[files.size()];
				files.toArray(allFiles);
				Arrays.sort(allFiles); // Sort ascending
				return allFiles;
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	@Override
	public boolean hasChildren(File parentFile) {

		ArrayList<File> files = new ArrayList<File>();
		if(parentFile.isDirectory() && parentFile.canRead()) {
			/*
			 * Check if the parent file is a chromatogram.
			 * It's not needed anymore, cause a chromatogram folder
			 * may contain several chromatogram files.
			 * chromatogramIdentifier.isChromatogramDirectory(parentFile)
			 */
			File[] fileList = parentFile.listFiles();
			if(fileList != null) {
				for(File file : parentFile.listFiles()) {
					if(!file.isHidden()) {
						files.add(file);
					}
				}
				return (files.size() > 0) ? true : false;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}
