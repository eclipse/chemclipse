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
package org.eclipse.chemclipse.chromatogram.xxd.integrator.core.combined;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.core.settings.combined.ICombinedIntegrationSettings;

public class CombinedIntegratorSupplier implements ICombinedIntegratorSupplier {

	private String id = "";
	private String description = "";
	private String integratorName = "";
	private Class<? extends ICombinedIntegrationSettings> settingsClass;

	@Override
	public String getId() {

		return id;
	}

	/**
	 * Sets the supplier id like
	 * "org.eclipse.chemclipse.chromatogram.xxd.integrator.supplier.trapezoid".
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
	 * Sets the description of the integrator supplier.
	 * 
	 * @param description
	 */
	protected void setDescription(String description) {

		if(description != null) {
			this.description = description;
		}
	}

	@Override
	public String getIntegratorName() {

		return integratorName;
	}

	/**
	 * Sets the name of the integrator supplier.
	 * 
	 * @param integratorName
	 */
	protected void setIntegratorName(String integratorName) {

		if(integratorName != null) {
			this.integratorName = integratorName;
		}
	}

	@Override
	public Class<? extends ICombinedIntegrationSettings> getSettingsClass() {

		return this.settingsClass;
	}

	protected void setSettingsClass(Class<? extends ICombinedIntegrationSettings> settingsClass) {

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
		CombinedIntegratorSupplier otherSupplier = (CombinedIntegratorSupplier)other;
		return id.equals(otherSupplier.id) && description.equals(otherSupplier.description) && integratorName.equals(otherSupplier.integratorName);
	}

	@Override
	public int hashCode() {

		return 7 * id.hashCode() + 11 * description.hashCode() + 13 * integratorName.hashCode();
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
		builder.append("integratorName=" + integratorName);
		builder.append("]");
		return builder.toString();
	}
}
