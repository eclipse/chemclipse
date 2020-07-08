/*******************************************************************************
 * Copyright (c) 2018, 2020 Lablicate GmbH.
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

import org.eclipse.chemclipse.model.identifier.template.TargetTemplate;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class TargetTemplateFilter extends ViewerFilter {

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
		if(element instanceof TargetTemplate) {
			TargetTemplate targetTemplate = (TargetTemplate)element;
			String name = targetTemplate.getName();
			String casNumber = targetTemplate.getCasNumber();
			String comment = targetTemplate.getComments();
			String contributor = targetTemplate.getContributor();
			String referenceId = targetTemplate.getReferenceId();
			//
			if(!caseSensitive) {
				searchText = searchText.toLowerCase();
				name = name.toLowerCase();
				casNumber = casNumber.toLowerCase();
				comment = comment.toLowerCase();
				contributor = contributor.toLowerCase();
				referenceId = referenceId.toLowerCase();
			}
			//
			if(name.contains(searchText)) {
				return true;
			}
			//
			if(casNumber.contains(searchText)) {
				return true;
			}
			//
			if(comment.contains(searchText)) {
				return true;
			}
			//
			if(contributor.contains(searchText)) {
				return true;
			}
			//
			if(referenceId.contains(searchText)) {
				return true;
			}
		}
		//
		return false;
	}
}
