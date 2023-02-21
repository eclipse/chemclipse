/*******************************************************************************
 * Copyright (c) 2021, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.internal.provider;

import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.support.ui.processors.Processor;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class ProcessorListFilter extends ViewerFilter {

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
		if(element instanceof Processor processor) {
			IProcessSupplier<?> processSupplier = processor.getProcessSupplier();
			//
			if(!caseSensitive) {
				searchText = searchText.toLowerCase();
			}
			//
			if(isMatch(processSupplier.getName())) {
				return true;
			}
			//
			for(DataCategory dataCategory : processSupplier.getSupportedDataTypes()) {
				if(isMatch(dataCategory.name())) {
					return true;
				}
			}
			//
			if(isMatch(processSupplier.getDescription())) {
				return true;
			}
		}
		//
		return false;
	}

	private boolean isMatch(String term) {

		return getSearchTerm(term).contains(searchText);
	}

	private String getSearchTerm(String term) {

		return !caseSensitive ? term.toLowerCase() : term;
	}
}
