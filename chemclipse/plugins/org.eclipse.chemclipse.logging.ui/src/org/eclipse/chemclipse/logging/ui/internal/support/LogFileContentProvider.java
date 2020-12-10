/*******************************************************************************
 * Copyright (c) 2011, 2020 Lablicate GmbH.
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

import org.eclipse.chemclipse.logging.ui.support.FilesSupport;
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
			return FilesSupport.getSortedLogFiles((File)inputElement).toArray();
		}
		return null;
	}
}
