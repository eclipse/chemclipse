/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.swt.ui.internal.provider;

import java.util.Map;

import org.eclipse.chemclipse.model.columns.SeparationColumnType;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;

public class ColumMappingFilter extends ViewerFilter {

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
		if(element instanceof Map.Entry<?, ?> setting) {
			//
			if(!caseSensitive) {
				searchText = searchText.toLowerCase();
			}
			/*
			 * Key
			 */
			if(contains(searchText, caseSensitive, setting.getKey().toString())) {
				return true;
			}
			/*
			 * Value
			 */
			String value;
			Object object = setting.getValue();
			if(object instanceof SeparationColumnType separationColumnType) {
				value = separationColumnType.label();
			} else {
				value = object.toString();
			}
			//
			if(contains(searchText, caseSensitive, value)) {
				return true;
			}
		}
		//
		return false;
	}

	private boolean contains(String searchText, boolean caseSensitive, String value) {

		String content = (!caseSensitive) ? value.toLowerCase() : value;
		return content.contains(searchText);
	}
}