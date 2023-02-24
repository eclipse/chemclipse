/*******************************************************************************
 * Copyright (c) 2016, 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.filter.core.chromatogram;

import org.eclipse.chemclipse.chromatogram.filter.settings.IChromatogramFilterSettings;

public class ChromatogramFilterSupplier implements IChromatogramFilterSupplier {

	private String id = ""; //$NON-NLS-1$
	private String description = ""; //$NON-NLS-1$
	private String filterName = ""; //$NON-NLS-1$
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
	 * "org.eclipse.chemclipse.chromatogram.msd.filter.supplier.backgroundRemover".
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

	protected void setSettingsClass(Class<? extends IChromatogramFilterSettings> settingsClass) {

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
		IChromatogramFilterSupplier otherSupplier = (IChromatogramFilterSupplier)other;
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
		builder.append("["); //$NON-NLS-1$
		builder.append("id=" + id); //$NON-NLS-1$
		builder.append(","); //$NON-NLS-1$
		builder.append("description=" + description); //$NON-NLS-1$
		builder.append(","); //$NON-NLS-1$
		builder.append("filterName=" + filterName); //$NON-NLS-1$
		builder.append("]"); //$NON-NLS-1$
		return builder.toString();
	}
}
