/*******************************************************************************
 * Copyright (c) 2020, 2022 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.ui.internal.provider;

import org.eclipse.chemclipse.model.statistics.IVariable;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.Feature;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class FeatureListFilter extends ViewerFilter {

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
		if(element instanceof Feature) {
			Feature feature = (Feature)element;
			IVariable variable = feature.getVariable();
			//
			String value = variable.getValue();
			String classification = variable.getClassification() == null ? "" : variable.getClassification();
			String description = variable.getDescription() == null ? "" : variable.getDescription();
			//
			if(!caseSensitive) {
				searchText = searchText.toLowerCase();
				value = value.toLowerCase();
				classification = classification.toLowerCase();
				description = description.toLowerCase();
			}
			//
			if(value.contains(searchText)) {
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
