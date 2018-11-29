/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.identifier;

public class TargetTemplate implements ITargetTemplate {

	private String name = "";
	private String casNumber = "";
	private String comment = "";
	private String contributor = "";
	private String referenceId = "";

	@Override
	public String getName() {

		return name;
	}

	@Override
	public void setName(String name) {

		this.name = name;
	}

	@Override
	public String getCasNumber() {

		return casNumber;
	}

	@Override
	public void setCasNumber(String casNumber) {

		this.casNumber = casNumber;
	}

	@Override
	public String getComment() {

		return comment;
	}

	@Override
	public void setComment(String comment) {

		this.comment = comment;
	}

	@Override
	public String getContributor() {

		return contributor;
	}

	@Override
	public void setContributor(String contributor) {

		this.contributor = contributor;
	}

	@Override
	public String getReferenceId() {

		return referenceId;
	}

	@Override
	public void setReferenceId(String referenceId) {

		this.referenceId = referenceId;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		TargetTemplate other = (TargetTemplate)obj;
		if(name == null) {
			if(other.name != null)
				return false;
		} else if(!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {

		return "TargetTemplate [name=" + name + ", casNumber=" + casNumber + ", comment=" + comment + ", contributor=" + contributor + ", referenceId=" + referenceId + "]";
	}
}
