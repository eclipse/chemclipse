/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.isd.filter.ui.internal.provider;

import org.eclipse.chemclipse.chromatogram.isd.filter.model.WavenumberSignal;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class WavenumberSignalsFilter extends ViewerFilter {

	private String searchText;

	public void setSearchText(String searchText, boolean caseSensitive) {

		this.searchText = searchText;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {

		if(searchText == null || searchText.equals("")) {
			return true;
		}
		//
		if(element instanceof WavenumberSignal signal) {
			/*
			 * Only Wavenumber
			 */
			if(Double.toString(signal.getWavenumber()).contains(searchText)) {
				return true;
			}
		}
		//
		return false;
	}
}