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

import org.eclipse.chemclipse.csd.model.core.IScanCSD;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IIonTransition;
import org.eclipse.chemclipse.wsd.model.core.IScanSignalWSD;
import org.eclipse.chemclipse.xir.model.core.ISignalXIR;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class ScanSignalListFilter extends ViewerFilter {

	private String searchText;
	private boolean caseSensitive;

	public ScanSignalListFilter() {

	}

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
		/*
		 * Not needed at the moment.
		 */
		if(!caseSensitive) {
			searchText = searchText.toLowerCase();
		}
		//
		if(element instanceof IIon ion) {
			IIonTransition ionTransition = ion.getIonTransition();
			if(ionTransition != null) {
				if(Double.toString(ionTransition.getQ3Ion()).contains(searchText)) {
					return true;
				} else if(Integer.toString(ionTransition.getQ1Ion()).contains(searchText)) {
					return true;
				}
			} else if(Double.toString(ion.getIon()).contains(searchText)) {
				return true;
			}
		} else if(element instanceof IScanCSD scanCSD) {
			if(Float.toString(scanCSD.getTotalSignal()).contains(searchText)) {
				return true;
			}
		} else if(element instanceof IScanSignalWSD scanSignalWSD) {
			if(Double.toString(scanSignalWSD.getWavelength()).contains(searchText)) {
				return true;
			}
		} else if(element instanceof ISignalXIR scanSignalISD) {
			if(Double.toString(scanSignalISD.getWavenumber()).contains(searchText)) {
				return true;
			}
		}
		//
		return false;
	}
}