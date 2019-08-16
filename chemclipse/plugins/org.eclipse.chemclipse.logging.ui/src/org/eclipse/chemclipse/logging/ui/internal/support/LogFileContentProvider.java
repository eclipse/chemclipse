/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.logging.ui.internal.support;

import java.io.File;
import java.io.FilenameFilter;

import org.eclipse.chemclipse.logging.support.PropertiesUtil;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class LogFileContentProvider implements IStructuredContentProvider {

	@Override
	public void dispose() {

	}

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}

	@Override
	public Object[] getElements(Object inputElement) {

		if(inputElement instanceof File) {
			File directory = (File)inputElement;
			if(directory.isDirectory()) {
				/*
				 * Return only *.log files.
				 */
				return directory.listFiles(new FilenameFilter() {

					@Override
					public boolean accept(File directory, String file) {

						if(file.contains(PropertiesUtil.LOG_EXTENSION)) {
							return true;
						}
						return false;
					}
				});
			}
		}
		return null;
	}
}
