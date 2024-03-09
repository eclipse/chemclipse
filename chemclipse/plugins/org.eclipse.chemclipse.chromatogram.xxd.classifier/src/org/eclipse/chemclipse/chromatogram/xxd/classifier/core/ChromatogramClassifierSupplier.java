/*******************************************************************************
 * Copyright (c) 2011, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.classifier.core;

import java.util.Objects;

import org.eclipse.chemclipse.chromatogram.xxd.classifier.settings.IChromatogramClassifierSettings;

public class ChromatogramClassifierSupplier implements IChromatogramClassifierSupplier {

	private String id = "";
	private String description = "";
	private String classifierName = "";
	private Class<? extends IChromatogramClassifierSettings> settingsClass;

	@Override
	public String getId() {

		return id;
	}

	/**
	 * Sets the chromatogram classifier supplier id like
	 * "org.eclipse.chemclipse.chromatogram.msd.classifier.supplier.ionchecker".
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
	 * Sets the description of the chromatogram classifier supplier.
	 * 
	 * @param description
	 */
	protected void setDescription(String description) {

		if(description != null) {
			this.description = description;
		}
	}

	@Override
	public String getClassifierName() {

		return classifierName;
	}

	/**
	 * Sets the name of the chromatogram classifier supplier.
	 * 
	 * @param classifierName
	 */
	protected void setClassifierName(String classifierName) {

		if(classifierName != null) {
			this.classifierName = classifierName;
		}
	}

	@Override
	public Class<? extends IChromatogramClassifierSettings> getSettingsClass() {

		return this.settingsClass;
	}

	protected void setSettingsClass(Class<? extends IChromatogramClassifierSettings> settingsClass) {

		this.settingsClass = settingsClass;
	}

	@Override
	public int hashCode() {

		return Objects.hash(classifierName, description, id);
	}

	@Override
	public boolean equals(Object obj) {

		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		ChromatogramClassifierSupplier other = (ChromatogramClassifierSupplier)obj;
		return Objects.equals(classifierName, other.classifierName) && Objects.equals(description, other.description) && Objects.equals(id, other.id);
	}

	@Override
	public String toString() {

		return "ChromatogramClassifierSupplier [id=" + id + ", description=" + description + ", classifierName=" + classifierName + ", settingsClass=" + settingsClass + "]";
	}
}
