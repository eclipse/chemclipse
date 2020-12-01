/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
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
	private String searchTextExtended;
	private boolean caseSensitive;

	public void setSearchText(String searchText, boolean caseSensitive) {

		this.searchText = searchText;
		this.searchTextExtended = ".*" + searchText + ".*";
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
		if(element instanceof IEditInformation) {
			IEditInformation editInformation = (IEditInformation)element;
			String search = (caseSensitive) ? searchTextExtended : searchTextExtended.toLowerCase();
			//
			String description = (caseSensitive) ? editInformation.getDescription() : editInformation.getDescription().toLowerCase();
			if(description.matches(search)) {
				return true;
			}
			//
			String editor = (caseSensitive) ? editInformation.getEditor() : editInformation.getEditor().toLowerCase();
			if(editor.matches(search)) {
				return true;
			}
			//
			String date = (caseSensitive) ? editInformation.getDate().toString() : editInformation.getDate().toString();
			if(date.matches(search)) {
				return true;
			}
		}
		//
		return false;
	}
}
