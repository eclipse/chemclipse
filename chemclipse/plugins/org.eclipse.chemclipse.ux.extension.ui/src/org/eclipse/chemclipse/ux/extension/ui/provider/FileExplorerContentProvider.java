/*******************************************************************************
 * Copyright (c) 2008, 2022 Lablicate GmbH.
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

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class FileExplorerContentProvider implements ITreeContentProvider {

	@Override
	public Object[] getChildren(Object parentElement) {

		if(parentElement instanceof File file) {
			return getFiles(file);
		}
		return File.listRoots();
	}

	@Override
	public Object getParent(Object element) {

		if(element instanceof File file) {
			File parentFile = file.getParentFile();
			if(parentFile != null && parentFile.canRead() && !parentFile.isHidden()) {
				return parentFile;
			}
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {

		if(element instanceof File file) {
			return hasChildren(file);
		}
		return false;
	}

	@Override
	public Object[] getElements(Object inputElement) {

		return getChildren(inputElement);
	}

	@Override
	public void dispose() {

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}

	/**
	 * Gets the children of the parent file.
	 * 
	 * @param parentFile
	 * @return File[]
	 */
	public File[] getFiles(File parentFile) {

		List<File> files = new ArrayList<>();
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
						files.add(file);
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

	/**
	 * Checks if the parent file has children.
	 * 
	 * @param parentFile
	 * @return boolean
	 */
	public boolean hasChildren(File parentFile) {

		ArrayList<File> files = new ArrayList<>();
		if(parentFile.isDirectory() && parentFile.canRead()) {
			/*
			 * Check if the parent file is a chromatogram.
			 */
			File[] fileList = parentFile.listFiles();
			if(fileList != null) {
				for(File file : parentFile.listFiles()) {
					if(!file.isHidden()) {
						files.add(file);
					}
				}
				return (!files.isEmpty());
			} else {
				return false;
			}
		} else {
			return false;
		}
	}
}
