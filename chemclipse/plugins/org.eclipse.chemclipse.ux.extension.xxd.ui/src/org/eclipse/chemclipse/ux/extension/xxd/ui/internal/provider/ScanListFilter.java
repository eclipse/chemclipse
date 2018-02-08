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

import java.util.List;

import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.support.LibraryInformationSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.ScanDataSupport;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class ScanListFilter extends ViewerFilter {

	private String searchText;
	private boolean caseSensitive;
	private LibraryInformationSupport libraryInformationSupport = new LibraryInformationSupport();
	private ScanDataSupport scanDataSupport = new ScanDataSupport();

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
		if(element instanceof IScan) {
			IScan scan = (IScan)element;
			List<? extends IIdentificationTarget> identificationTargets = scanDataSupport.getIdentificationTargets(scan);
			for(IIdentificationTarget target : identificationTargets) {
				IIdentificationTarget identificationTarget = (IIdentificationTarget)target;
				if(libraryInformationSupport.matchSearchText(identificationTarget.getLibraryInformation(), searchText, caseSensitive)) {
					return true;
				}
			}
		}
		//
		return false;
	}
}
