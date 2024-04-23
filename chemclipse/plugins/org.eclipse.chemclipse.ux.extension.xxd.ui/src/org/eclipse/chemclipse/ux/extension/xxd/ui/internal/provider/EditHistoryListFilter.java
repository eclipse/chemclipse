/*******************************************************************************
 * Copyright (c) 2020, 2024 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import org.eclipse.chemclipse.support.history.IEditInformation;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class EditHistoryListFilter extends ViewerFilter {

	private String searchText;
	private boolean caseSensitive;

	public void setSearchText(String searchText, boolean caseSensitive) {

		this.searchText = searchText;
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
		if(element instanceof IEditInformation editInformation) {
			String search = caseSensitive ? searchText : searchText.toLowerCase();
			//
			if(matches(search, editInformation.getDescription())) {
				return true;
			}
			//
			if(matches(search, editInformation.getEditor())) {
				return true;
			}
			//
			if(matches(search, editInformation.getDate().toString())) {
				return true;
			}
		}
		//
		return false;
	}

	private boolean matches(String search, String target) {

		return (caseSensitive ? target : target.toLowerCase()).contains(search);
	}
}