/*******************************************************************************
 * Copyright (c) 2008, 2017 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.core;

public abstract class AbstractSupplier implements ISupplierSetter {

	private String id = "";
	private String description = "";
	private String identifierName = "";

	public String getId() {

		return id;
	}

	@Override
	public void setId(final String id) {

		if(id != null) {
			this.id = id;
		}
	}

	public String getDescription() {

		return description;
	}

	@Override
	public void setDescription(final String description) {

		if(description != null) {
			this.description = description;
		}
	}

	public String getIdentifierName() {

		return identifierName;
	}

	@Override
	public void setIdentifierName(final String identifierName) {

		if(identifierName != null) {
			this.identifierName = identifierName;
		}
	}

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
	public int hashCode() {

		return id.hashCode() + description.hashCode() + identifierName.hashCode();
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
