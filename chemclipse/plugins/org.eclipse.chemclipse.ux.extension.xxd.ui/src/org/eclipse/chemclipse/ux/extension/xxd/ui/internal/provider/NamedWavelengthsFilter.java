/*******************************************************************************
 * Copyright (c) 2020, 2022 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Matthias Mailänder - adapted for DAD
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import org.eclipse.chemclipse.model.wavelengths.NamedWavelength;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class NamedWavelengthsFilter extends ViewerFilter {

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
		if(element instanceof NamedWavelength) {
			NamedWavelength namedWavelength = (NamedWavelength)element;
			String identifier = namedWavelength.getIdentifier();
			String wavelengths = namedWavelength.getWavelengths();
			//
			if(!caseSensitive) {
				searchText = searchText.toLowerCase();
				identifier = identifier.toLowerCase();
				wavelengths = wavelengths.toLowerCase();
			}
			//
			if(identifier.contains(searchText)) {
				return true;
			}
			//
			if(wavelengths.contains(searchText)) {
				return true;
			}
		}
		//
		return false;
	}
}
