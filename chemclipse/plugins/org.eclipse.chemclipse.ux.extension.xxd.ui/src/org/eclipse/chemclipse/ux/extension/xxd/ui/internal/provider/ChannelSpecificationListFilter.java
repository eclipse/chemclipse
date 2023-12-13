/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
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

import java.util.Map;

import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class ChannelSpecificationListFilter extends ViewerFilter {

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
		if(element instanceof Map.Entry<?, ?> entry) {
			String search = (caseSensitive) ? searchTextExtended : searchTextExtended.toLowerCase();
			String key = (caseSensitive) ? (String)entry.getKey() : ((String)entry.getKey()).toLowerCase();
			String value = (caseSensitive) ? (String)entry.getValue() : ((String)entry.getValue()).toLowerCase();
			//
			if(key.matches(search)) {
				return true;
			}
			//
			if(value.matches(search)) {
				return true;
			}
		}
		//
		return false;
	}
}
