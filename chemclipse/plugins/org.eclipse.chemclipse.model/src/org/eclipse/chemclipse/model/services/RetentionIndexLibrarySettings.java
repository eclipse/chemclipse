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
package org.eclipse.chemclipse.model.services;

public class RetentionIndexLibrarySettings {

	private String searchColumn = "";
	private boolean caseSensitive = false;
	private boolean removeWhiteSpace = false;
	private float retentionIndexDelta = 10.0f;

	public String getSearchColumn() {

		return searchColumn;
	}

	public void setSearchColumn(String searchColumn) {

		this.searchColumn = searchColumn;
	}

	public boolean isCaseSensitive() {

		return caseSensitive;
	}

	public void setCaseSensitive(boolean caseSensitive) {

		this.caseSensitive = caseSensitive;
	}

	public boolean isRemoveWhiteSpace() {

		return removeWhiteSpace;
	}

	public void setRemoveWhiteSpace(boolean removeWhiteSpace) {

		this.removeWhiteSpace = removeWhiteSpace;
	}

	public float getRetentionIndexDelta() {

		return retentionIndexDelta;
	}

	/**
	 * The allowed delta depends on the underlying database.
	 * 
	 * @param retentionIndexDelta
	 */
	public void setRetentionIndexDelta(float retentionIndexDelta) {

		this.retentionIndexDelta = retentionIndexDelta;
	}
}