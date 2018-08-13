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

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.support.LibraryInformationSupport;
import org.eclipse.chemclipse.model.targets.ITarget;
import org.eclipse.chemclipse.ux.extension.xxd.ui.internal.support.ScanDataSupport;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class PeakScanListFilter extends ViewerFilter {

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

		if(searchText == null || searchText.equals("")) {
			return true;
		}
		//
		if(element instanceof IPeak || element instanceof IScan) {
			if(element instanceof IPeak) {
				IPeak peak = (IPeak)element;
				matchPeak(peak);
			} else {
				IScan scan = (IScan)element;
				matchScan(scan);
			}
		}
		//
		return false;
	}

	private boolean matchPeak(IPeak peak) {

		if(isMatch(peak, searchText, caseSensitive)) {
			return true;
		} else {
			for(ITarget target : peak.getTargets()) {
				if(target instanceof IIdentificationTarget) {
					IIdentificationTarget identificationTarget = (IIdentificationTarget)target;
					if(libraryInformationSupport.matchSearchText(identificationTarget.getLibraryInformation(), searchText, caseSensitive)) {
						return true;
					}
				}
			}
		}
		return false;
	}

	private boolean matchScan(IScan scan) {

		List<? extends IIdentificationTarget> identificationTargets = scanDataSupport.getIdentificationTargets(scan);
		for(IIdentificationTarget target : identificationTargets) {
			IIdentificationTarget identificationTarget = (IIdentificationTarget)target;
			if(libraryInformationSupport.matchSearchText(identificationTarget.getLibraryInformation(), searchText, caseSensitive)) {
				return true;
			}
		}
		return false;
	}

	private boolean isMatch(IPeak peak, String searchText, boolean caseSensitive) {

		boolean isMatch = false;
		//
		String classifier = peak.getClassifier();
		String detectorDescription = peak.getDetectorDescription();
		String modelDescription = peak.getModelDescription();
		String quantifierDescription = peak.getQuantifierDescription();
		//
		if(!caseSensitive) {
			searchText = searchText.toLowerCase();
			classifier = classifier.toLowerCase();
			detectorDescription = detectorDescription.toLowerCase();
			modelDescription = modelDescription.toLowerCase();
			quantifierDescription = quantifierDescription.toLowerCase();
		}
		//
		if(!isMatch && matchText(classifier, searchText)) {
			isMatch = true;
		}
		//
		if(!isMatch && detectorDescription.matches(searchText)) {
			isMatch = true;
		}
		//
		if(!isMatch && modelDescription.matches(searchText)) {
			isMatch = true;
		}
		//
		if(!isMatch && quantifierDescription.matches(searchText)) {
			isMatch = true;
		}
		//
		return isMatch;
	}

	private boolean matchText(String text, String searchText) {

		return text.matches(searchText);
	}
}
