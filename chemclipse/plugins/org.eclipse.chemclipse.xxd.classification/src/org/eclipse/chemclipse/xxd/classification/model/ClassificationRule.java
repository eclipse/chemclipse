/*******************************************************************************
 * Copyright (c) 2022, 2024 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 * Philip Wenig - use object identity
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.classification.model;

import java.util.Objects;

public class ClassificationRule {

	private String searchExpression = "";
	private String classification = "";
	private Reference reference = Reference.NAME;

	public ClassificationRule() {

		this("", "");
	}

	public ClassificationRule(String searchExpression, String classification) {

		this(searchExpression, classification, Reference.NAME);
	}

	public ClassificationRule(String searchExpression, String classification, Reference reference) {

		this.searchExpression = searchExpression;
		this.classification = classification;
		this.reference = reference;
	}

	public String getSearchExpression() {

		return searchExpression;
	}

	public void setSearchExpression(String searchExpression) {

		this.searchExpression = searchExpression;
	}

	public String getClassification() {

		return classification;
	}

	public void setClassification(String classification) {

		this.classification = classification;
	}

	public Reference getReference() {

		return reference;
	}

	public void setReference(Reference reference) {

		this.reference = reference;
	}

	@Override
	public int hashCode() {

		return Objects.hash(classification, reference, searchExpression);
	}

	@Override
	public boolean equals(Object obj) {

		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		ClassificationRule other = (ClassificationRule)obj;
		return Objects.equals(classification, other.classification) && reference == other.reference && Objects.equals(searchExpression, other.searchExpression);
	}

	@Override
	public String toString() {

		return "ClassificationRule [searchExpression=" + searchExpression + ", classification=" + classification + ", reference=" + reference + "]";
	}
}