/*******************************************************************************
 * Copyright (c) 2011, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.classifier.core;

import org.eclipse.chemclipse.chromatogram.msd.classifier.settings.IChromatogramClassifierSettings;

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

	// ------------------------------------hashCode, equals, toString
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
		IChromatogramClassifierSupplier otherSupplier = (IChromatogramClassifierSupplier)other;
		return id.equals(otherSupplier.getId()) && description.equals(otherSupplier.getDescription()) && classifierName.equals(otherSupplier.getClassifierName());
	}

	@Override
	public int hashCode() {

		return 7 * id.hashCode() + 11 * description.hashCode() + 13 * classifierName.hashCode();
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
		builder.append("classifierName=" + classifierName);
		builder.append("]");
		return builder.toString();
	}
	// ------------------------------------hashCode, equals, toString
}
