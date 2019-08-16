/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.wsd.filter.core.chromatogram;

import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;

public class ChromatogramFilterSupplierWSD implements IChromatogramFilterSupplierWSD {

	private String id = "";
	private String description = "";
	private String filterName = "";
	private Class<? extends IChromatogramFilterSettings> settingsClass;

	@Override
	public String getDescription() {

		return description;
	}

	/**
	 * Sets the description of the chromatogram filter supplier.
	 * 
	 * @param description
	 */
	protected void setDescription(String description) {

		if(description != null) {
			this.description = description;
		}
	}

	@Override
	public String getFilterName() {

		return filterName;
	}

	/**
	 * Sets the name of the chromatogram filter supplier.
	 * 
	 * @param filterName
	 */
	protected void setFilterName(String filterName) {

		if(filterName != null) {
			this.filterName = filterName;
		}
	}

	@Override
	public String getId() {

		return id;
	}

	/**
	 * Sets the chromatogram filter supplier id like
	 * "org.eclipse.chemclipse.chromatogram.wsd.filter.supplier.backgroundRemover".
	 * 
	 * @param id
	 */
	protected void setId(String id) {

		if(id != null) {
			this.id = id;
		}
	}

	@Override
	public Class<? extends IChromatogramFilterSettings> getSettingsClass() {

		return this.settingsClass;
	}

	protected void setFilterSettingsClass(Class<? extends IChromatogramFilterSettings> settingsClass) {

		this.settingsClass = settingsClass;
	}

	@Override
	public boolean equals(Object other) {

		if(other == null) {
			return false;
		}
		if(this == other) {
			return true;
		}
		if(this.getClass() != other.getClass()) {
			return false;
		}
		IChromatogramFilterSupplierWSD otherSupplier = (IChromatogramFilterSupplierWSD)other;
		return id.equals(otherSupplier.getId()) && description.equals(otherSupplier.getDescription()) && filterName.equals(otherSupplier.getFilterName());
	}

	@Override
	public int hashCode() {

		return 7 * id.hashCode() + 11 * description.hashCode() + 13 * filterName.hashCode();
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName());
		builder.append("[");
		builder.append("id=" + id);
		builder.append(",");
		builder.append("description=" + description);
		builder.append(",");
		builder.append("filterName=" + filterName);
		builder.append("]");
		return builder.toString();
	}
}
