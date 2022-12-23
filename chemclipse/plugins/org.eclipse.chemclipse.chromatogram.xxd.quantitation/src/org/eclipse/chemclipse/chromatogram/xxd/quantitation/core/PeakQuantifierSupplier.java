/*******************************************************************************
 * Copyright (c) 2013, 2022 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.quantitation.core;

import org.eclipse.chemclipse.chromatogram.xxd.quantitation.settings.IPeakQuantifierSettings;

public class PeakQuantifierSupplier implements IPeakQuantifierSupplier {

	private String id = "";
	private String description = "";
	private String peakQuantifierName = "";
	private Class<? extends IPeakQuantifierSettings> settingsClass;

	@Override
	public Class<? extends IPeakQuantifierSettings> getSettingsClass() {

		return settingsClass;
	}

	protected void setSettingsClass(Class<? extends IPeakQuantifierSettings> settingsClass) {

		this.settingsClass = settingsClass;
	}

	@Override
	public String getId() {

		return id;
	}

	/**
	 * Sets the supplier id like
	 * "org.eclipse.chemclipse.chromatogram.msd.quantifier.supplier.chemclipse".
	 * 
	 * @param id
	 */
	protected void setId(String id) {

		if(id != null) {
			this.id = id;
		}
	}

	@Override
	public String getDescription() {

		return description;
	}

	/**
	 * Sets the description of the quantifier supplier.
	 * 
	 * @param description
	 */
	protected void setDescription(String description) {

		if(description != null) {
			this.description = description;
		}
	}

	@Override
	public String getPeakQuantifierName() {

		return peakQuantifierName;
	}

	/**
	 * Sets the detector name of the baseline detection supplier.
	 * 
	 * @param comparatorName
	 */
	protected void setPeakQuantifierName(String peakQuantifierName) {

		if(peakQuantifierName != null) {
			this.peakQuantifierName = peakQuantifierName;
		}
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
		PeakQuantifierSupplier otherSupplier = (PeakQuantifierSupplier)other;
		return id.equals(otherSupplier.id) && description.equals(otherSupplier.description) && peakQuantifierName.equals(otherSupplier.peakQuantifierName);
	}

	@Override
	public int hashCode() {

		return 7 * id.hashCode() + 11 * description.hashCode() + 13 * peakQuantifierName.hashCode();
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
		builder.append("peakQuantifierName=" + peakQuantifierName);
		builder.append("]");
		return builder.toString();
	}
}