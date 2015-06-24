/*******************************************************************************
 * Copyright (c) 2012, 2015 Dr. Philip Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.rcp.app.ui.provider;

import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class SelectViewFilter extends ViewerFilter {

	private String searchPattern;
	private boolean caseInsensitive;

	public void setCaseInsensitive(boolean caseInsensitive) {

		this.caseInsensitive = caseInsensitive;
	}

	public boolean isCaseInsensitive() {

		return caseInsensitive;
	}

	public void setSearchPattern(String searchPattern) {

		this.searchPattern = (caseInsensitive) ? searchPattern.toLowerCase() : searchPattern;
	}

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {

		if(searchPattern == null || searchPattern.equals("")) {
			return true;
		}
		if(element instanceof MPart) {
			MPart part = (MPart)element;
			String label = (caseInsensitive) ? part.getLabel().toLowerCase() : part.getLabel();
			if(label.contains(searchPattern)) {
				return true;
			}
		}
		return false;
	}
}
