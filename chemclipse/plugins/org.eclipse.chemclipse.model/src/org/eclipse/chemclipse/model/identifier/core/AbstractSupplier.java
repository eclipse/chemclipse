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
package org.eclipse.chemclipse.model.identifier.core;

import org.eclipse.chemclipse.model.identifier.IIdentifierSettings;

public abstract class AbstractSupplier<S extends IIdentifierSettings> implements ISupplierSetter {

	private String description = "";
	private String id = "";
	private String identifierName = "";
	private Class<? extends S> identifierSettingsClass;

	// -----------------------------------------------equals, hashCode, toString
	@Override
	public boolean equals(final Object otherObject) {

		if(this == otherObject) {
			return true;
		}
		if(otherObject == null) {
			return false;
		}
		if(getClass() != otherObject.getClass()) {
			return false;
		}
		ISupplier other = (ISupplier)otherObject;
		return id.equals(other.getId()) && description.equals(other.getDescription()) && identifierName.equals(other.getIdentifierName());
	}

	@Override
	public String getDescription() {

		return description;
	}

	@Override
	public String getId() {

		return id;
	}

	@Override
	public String getIdentifierName() {

		return identifierName;
	}

	protected Class<? extends S> getSpecificIdentifierSettingsClass() {

		return this.identifierSettingsClass;
	}

	@Override
	public int hashCode() {

		return id.hashCode() + description.hashCode() + identifierName.hashCode();
	}

	@Override
	public void setDescription(final String description) {

		if(description != null) {
			this.description = description;
		}
	}

	@Override
	public void setId(final String id) {

		if(id != null) {
			this.id = id;
		}
	}

	@Override
	public void setIdentifierName(final String identifierName) {

		if(identifierName != null) {
			this.identifierName = identifierName;
		}
	}

	public void setIdentifierSettingsClass(Class<? extends S> identifierSettingsClass) {

		this.identifierSettingsClass = identifierSettingsClass;
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName());
		builder.append("[");
		builder.append("id=" + this.id);
		builder.append(",");
		builder.append("description=" + this.description);
		builder.append(",");
		builder.append("identifierName=" + this.identifierName);
		builder.append("]");
		return builder.toString();
	}
	// -----------------------------------------------equals, hashCode, toString
}
