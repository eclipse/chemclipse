/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import java.io.File;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class SequenceFilesFilter extends ViewerFilter {

	private String searchText;
	private boolean caseSensitive;

	public void setSearchText(String searchText, boolean caseSensitive) {

		this.searchText = ".*" + searchText + ".*";
		this.caseSensitive = caseSensitive;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {

		/*
		 * Pre-Condition
		 */
		if(searchText == null || searchText.equals("")) {
			return true;
		}
		//
		if(element instanceof File) {
			//
			File file = (File)element;
			String name = file.getName();
			//
			if(!caseSensitive) {
				searchText = searchText.toLowerCase();
				name = name.toLowerCase();
			}
			//
			if(name.matches(searchText)) {
				return true;
			}
		}
		//
		return false;
	}
}
