/*******************************************************************************
 * Copyright (c) 2008, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.core;

import org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.settings.IBaselineDetectorSettings;

public class BaselineDetectorSupplier implements IBaselineDetectorSupplier {

	private String id = "";
	private String description = "";
	private String detectorName = "";
	private Class<? extends IBaselineDetectorSettings> settingsClass;

	@Override
	public String getId() {

		return id;
	}

	/**
	 * Sets the supplier id like
	 * "org.eclipse.chemclipse.chromatogram.xxd.baseline.detector.supplier.chemstation".
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
	 * Sets the description of the baseline detector supplier.
	 * 
	 * @param description
	 */
	protected void setDescription(String description) {

		if(description != null) {
			this.description = description;
		}
	}

	@Override
	public String getDetectorName() {

		return detectorName;
	}

	/**
	 * Sets the detector name of the baseline detection supplier.
	 * 
	 * @param comparatorName
	 */
	protected void setDetectorName(String detectorName) {

		if(detectorName != null) {
			this.detectorName = detectorName;
		}
	}

	@Override
	public Class<? extends IBaselineDetectorSettings> getSettingsClass() {

		return this.settingsClass;
	}

	protected void setSettingsClass(Class<? extends IBaselineDetectorSettings> settingsClass) {

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
		BaselineDetectorSupplier otherSupplier = (BaselineDetectorSupplier)other;
		return id.equals(otherSupplier.id) && description.equals(otherSupplier.description) && detectorName.equals(otherSupplier.detectorName);
	}

	@Override
	public int hashCode() {

		return 7 * id.hashCode() + 11 * description.hashCode() + 13 * detectorName.hashCode();
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
		builder.append("detectorName=" + detectorName);
		builder.append("]");
		return builder.toString();
	}
}
