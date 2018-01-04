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
package org.eclipse.chemclipse.ux.extension.msd.ui.provider;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.chemclipse.ux.extension.msd.ui.internal.support.PeakIdentifier;
import org.eclipse.chemclipse.ux.extension.ui.provider.FileExplorerContentProvider;

public class PeakFileExplorerContentProvider extends FileExplorerContentProvider {

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
						 * Check if the file is a chromatogram.
						 * Do not list other files than chromatograms.
						 */
						if(file.isDirectory()) {
							files.add(file);
						} else {
							if(PeakIdentifier.isPeak(file)) {
								files.add(file);
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
			 */
			if(PeakIdentifier.isPeakDirectory(parentFile)) {
				return false;
			} else {
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
			}
		} else {
			return false;
		}
	}
}
