/*******************************************************************************
 * Copyright (c) 2010, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors: Philip
 * (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.identifier;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractLibraryInformation implements ILibraryInformation {

	private String name = "";
	private Set<String> synonyms;
	private String casNumber = "";
	private String comments = "";
	private String referenceIdentifier = "";
	private String miscellaneous = "";
	private String formula = "";
	private double molWeight = 0;
	private String database = "";
	private String contributor = "";

	// -----------------------------------------------ILibraryInformation
	public AbstractLibraryInformation() {
		synonyms = new HashSet<String>();
	}

	@Override
	public String getName() {

		return name;
	}

	@Override
	public void setName(String name) {

		if(name != null) {
			this.name = name;
		}
	}

	@Override
	public Set<String> getSynonyms() {

		return synonyms;
	}

	@Override
	public void setSynonyms(Set<String> synonyms) {

		if(synonyms != null) {
			this.synonyms = synonyms;
		}
	}

	@Override
	public String getCasNumber() {

		return casNumber;
	}

	@Override
	public void setCasNumber(String casNumber) {

		if(casNumber != null) {
			this.casNumber = casNumber;
		}
	}

	@Override
	public String getFormula() {

		return formula;
	}

	@Override
	public void setFormula(String formula) {

		if(formula != null) {
			this.formula = formula;
		}
	}

	@Override
	public double getMolWeight() {

		return molWeight;
	}

	@Override
	public void setMolWeight(double molWeight) {

		this.molWeight = molWeight;
	}

	@Override
	public String getComments() {

		return comments;
	}

	@Override
	public void setComments(String comments) {

		if(comments != null) {
			this.comments = comments;
		}
	}

	@Override
	public String getReferenceIdentifier() {

		return referenceIdentifier;
	}

	@Override
	public void setReferenceIdentifier(String referenceIdentifier) {

		if(referenceIdentifier != null) {
			this.referenceIdentifier = referenceIdentifier;
		}
	}

	@Override
	public String getMiscellaneous() {

		return miscellaneous;
	}

	@Override
	public void setMiscellaneous(String miscellaneous) {

		if(miscellaneous != null) {
			this.miscellaneous = miscellaneous;
		}
	}

	@Override
	public String getDatabase() {

		return database;
	}

	@Override
	public void setDatabase(String database) {

		if(database != null) {
			this.database = database;
		}
	}

	@Override
	public String getContributor() {

		return contributor;
	}

	@Override
	public void setContributor(String contributor) {

		if(contributor != null) {
			this.contributor = contributor;
		}
	}

	// -----------------------------------------------ILibraryInformation
	// -------------------------------equals, hashCode, toString
	@Override
	public boolean equals(Object other) {

		if(this == other) {
			return true;
		}
		if(other == null) {
			return false;
		}
		if(getClass() != other.getClass()) {
			return false;
		}
		ILibraryInformation otherEntry = (ILibraryInformation)other;
		return getName().equals(otherEntry.getName()) && getCasNumber().equals(otherEntry.getCasNumber()) && getComments().equals(otherEntry.getComments()) && getMiscellaneous().equals(otherEntry.getMiscellaneous()) && getFormula().endsWith(otherEntry.getFormula()) && getMolWeight() == otherEntry.getMolWeight();
	}

	@Override
	public int hashCode() {

		return 7 * name.hashCode() + 11 * casNumber.hashCode() + 13 * comments.hashCode() + 11 * miscellaneous.hashCode() + 7 * formula.hashCode() + 11 * Double.valueOf(molWeight).hashCode();
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName());
		builder.append("[");
		builder.append("name=" + name);
		builder.append(",");
		builder.append("casNumber=" + casNumber);
		builder.append(",");
		builder.append("comments=" + comments);
		builder.append(",");
		builder.append("miscellaneous=" + miscellaneous);
		builder.append(",");
		builder.append("formula=" + formula);
		builder.append(",");
		builder.append("molWeight=" + molWeight);
		builder.append("]");
		return builder.toString();
	}
	// -------------------------------equals, hashCode, toString
}
