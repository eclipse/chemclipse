/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.identifier.IFlavorMarker;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class FlavorMarkerListFilter extends ViewerFilter {

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
		if(element instanceof IFlavorMarker marker) {
			/*
			 * Search Text
			 */
			searchText = caseSensitive ? searchText : searchText.toLowerCase();
			/*
			 * Content
			 */
			if(contains(searchText, marker.getOdor())) {
				return true;
			}
			//
			if(contains(searchText, marker.getMatrix())) {
				return true;
			}
			//
			if(contains(searchText, marker.getSolvent())) {
				return true;
			}
		}
		//
		return false;
	}

	private boolean contains(String searchText, String value) {

		value = caseSensitive ? value : value.toLowerCase();
		return value.contains(searchText);
	}
}