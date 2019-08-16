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
 * Jan Holy - implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.peak.detector.core;

import org.eclipse.chemclipse.chromatogram.peak.detector.settings.IPeakDetectorSettings;

public abstract class AbstractPeakDetectorSupplier<S extends IPeakDetectorSettings> implements IPeakDetectorSupplier {

	private String id = "";
	private String description = "";
	private String peakDetectorName = "";
	private Class<? extends S> settingsClass;

	public AbstractPeakDetectorSupplier(String id, String description, String peakDetectorName) {
		setId(id);
		setDescription(description);
		setPeakDetectorName(peakDetectorName);
	}

	@Override
	public String getId() {

		return id;
	}

	@Override
	public String getDescription() {

		return description;
	}

	@Override
	public String getPeakDetectorName() {

		return peakDetectorName;
	}

	/**
	 * Sets the supplier id like
	 * "org.eclipse.chemclipse.chromatogram.msd.peak.detector.supplier.firstderivative".
	 * 
	 * @param id
	 */
	private void setId(String id) {

		if(id != null) {
			this.id = id;
		}
	}

	/**
	 * Sets the description of the integrator supplier.
	 * 
	 * @param description
	 */
	private void setDescription(String description) {

		if(description != null) {
			this.description = description;
		}
	}

	/**
	 * Sets the name of the peak detector supplier.
	 * 
	 * @param peakDetectorName
	 */
	private void setPeakDetectorName(String peakDetectorName) {

		if(peakDetectorName != null) {
			this.peakDetectorName = peakDetectorName;
		}
	}

	public Class<? extends S> getSettingsClass() {

		return this.settingsClass;
	}

	public void setSettingsClass(Class<? extends S> settingsClass) {

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
		AbstractPeakDetectorSupplier<?> otherSupplier = (AbstractPeakDetectorSupplier<?>)other;
		return id.equals(otherSupplier.id) && description.equals(otherSupplier.description) && peakDetectorName.equals(otherSupplier.peakDetectorName);
	}

	@Override
	public int hashCode() {

		return 7 * id.hashCode() + 11 * description.hashCode() + 13 * peakDetectorName.hashCode();
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
		builder.append("peakDetectorName=" + peakDetectorName);
		builder.append("]");
		return builder.toString();
	}
}
