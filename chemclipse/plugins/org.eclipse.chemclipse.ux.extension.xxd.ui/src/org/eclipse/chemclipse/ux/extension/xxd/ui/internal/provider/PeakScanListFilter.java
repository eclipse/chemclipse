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
 * Christoph Läubrich - adjust to new API
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import java.util.Collection;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.support.LibraryInformationSupport;
import org.eclipse.chemclipse.model.targets.ITarget;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class PeakScanListFilter extends ViewerFilter {

	private String searchText;
	private boolean caseSensitive;
	private final LibraryInformationSupport libraryInformationSupport = new LibraryInformationSupport();

	public void setSearchText(String searchText, boolean caseSensitive) {

		this.searchText = searchText;
		this.caseSensitive = caseSensitive;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {

		if(searchText == null || searchText.equals("")) {
			return true;
		}
		//
		if(element instanceof IPeak || element instanceof IScan) {
			if(element instanceof IPeak peak) {
				return matchPeak(peak);
			}
			if(element instanceof IScan scan) {
				return matchScan(scan);
			}
		}
		//
		return false;
	}

	private boolean matchPeak(IPeak peak) {

		if(isMatch(peak, searchText, caseSensitive)) {
			return true;
		}
		for(ITarget target : peak.getTargets()) {
			if(target instanceof IIdentificationTarget identificationTarget) {
				if(libraryInformationSupport.containsSearchText(identificationTarget.getLibraryInformation(), searchText, caseSensitive)) {
					return true;
				}
			}
		}
		return false;
	}

	private boolean matchScan(IScan scan) {

		for(IIdentificationTarget target : scan.getTargets()) {
			if(libraryInformationSupport.containsSearchText(target.getLibraryInformation(), searchText, caseSensitive)) {
				return true;
			}
		}
		return false;
	}

	private boolean isMatch(IPeak peak, String searchText, boolean caseSensitive) {

		boolean isMatch = false;
		//
		Collection<String> classifiers = peak.getClassifier();
		String detectorDescription = peak.getDetectorDescription();
		String modelDescription = peak.getModelDescription();
		String quantifierDescription = peak.getQuantifierDescription();
		//
		if(!caseSensitive) {
			searchText = searchText.toLowerCase();
			detectorDescription = detectorDescription.toLowerCase();
			modelDescription = modelDescription.toLowerCase();
			quantifierDescription = quantifierDescription.toLowerCase();
		}
		//
		if(containsText(classifiers, searchText, caseSensitive)) {
			isMatch = true;
		}
		//
		if(!isMatch && detectorDescription.contains(searchText)) {
			isMatch = true;
		}
		//
		if(!isMatch && modelDescription.contains(searchText)) {
			isMatch = true;
		}
		//
		if(!isMatch && quantifierDescription.contains(searchText)) {
			isMatch = true;
		}
		//
		return isMatch;
	}

	private static boolean containsText(Collection<String> classifiers, String searchText, boolean caseSensitive) {

		for(String classifier : classifiers) {
			if(!caseSensitive) {
				classifier = classifier.toLowerCase();
			}
			if(containsText(classifier, searchText)) {
				return true;
			}
		}
		return false;
	}

	private static boolean containsText(String text, String searchText) {

		return text.contains(searchText);
	}
}
