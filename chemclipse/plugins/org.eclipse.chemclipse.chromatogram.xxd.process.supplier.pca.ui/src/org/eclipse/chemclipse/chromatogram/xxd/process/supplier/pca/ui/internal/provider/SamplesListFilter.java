/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.internal.provider;

import org.eclipse.chemclipse.model.statistics.ISample;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class SamplesListFilter extends ViewerFilter {

	private String searchText;
	private boolean caseSensitive;

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
		if(element instanceof ISample) {
			ISample sample = (ISample)element;
			String name = sample.getName();
			String groupName = (sample.getGroupName() != null) ? sample.getGroupName() : "";
			String classification = (sample.getClassification() != null) ? sample.getClassification() : "";
			String description = (sample.getDescription() != null) ? sample.getDescription() : "";
			//
			if(!caseSensitive) {
				searchText = searchText.toLowerCase();
				name = name.toLowerCase();
				groupName = groupName.toLowerCase();
				classification = classification.toLowerCase();
				description = description.toLowerCase();
			}
			//
			if(name.contains(searchText)) {
				return true;
			}
			//
			if(groupName.contains(searchText)) {
				return true;
			}
			//
			if(classification.contains(searchText)) {
				return true;
			}
			//
			if(description.contains(searchText)) {
				return true;
			}
		}
		//
		return false;
	}
}
