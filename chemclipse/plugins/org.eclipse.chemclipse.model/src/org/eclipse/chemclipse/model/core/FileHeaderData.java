/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.core;

import java.util.Objects;
import java.util.regex.Pattern;

import org.eclipse.chemclipse.model.core.support.HeaderField;

public class FileHeaderData {

	private HeaderField headerField;
	private String regularExpression;
	private int groupIndex;
	/*
	 * Transient
	 */
	private Pattern pattern = null;

	public FileHeaderData() {

		this(HeaderField.DEFAULT, "", 1);
	}

	public FileHeaderData(HeaderField headerField, String regularExpression, int groupIndex) {

		this.headerField = headerField;
		this.regularExpression = regularExpression;
		this.groupIndex = groupIndex;
	}

	public HeaderField getHeaderField() {

		return headerField;
	}

	public void setHeaderField(HeaderField headerField) {

		this.headerField = headerField;
	}

	public String getRegularExpression() {

		return regularExpression;
	}

	public void setRegularExpression(String regularExpression) {

		this.regularExpression = regularExpression;
		this.pattern = null;
	}

	public int getGroupIndex() {

		return groupIndex;
	}

	public void setGroupIndex(int groupIndex) {

		this.groupIndex = groupIndex;
	}

	public Pattern getPattern() {

		if(pattern == null) {
			pattern = Pattern.compile(regularExpression);
		}
		//
		return pattern;
	}

	@Override
	public int hashCode() {

		return Objects.hash(groupIndex, headerField, regularExpression);
	}

	@Override
	public boolean equals(Object obj) {

		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		FileHeaderData other = (FileHeaderData)obj;
		return groupIndex == other.groupIndex && headerField == other.headerField && Objects.equals(regularExpression, other.regularExpression);
	}

	@Override
	public String toString() {

		return "FileHeaderData [headerField=" + headerField + ", regularExpression=" + regularExpression + ", groupIndex=" + groupIndex + "]";
	}
}