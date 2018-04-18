/*******************************************************************************
 * Copyright (c) 2010, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Dr. Alexander Kerner - implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.identifier;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractLibraryInformation implements ILibraryInformation {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = -7091140092667283293L;
	//
	private String name = "";
	private Set<String> synonyms;
	private String casNumber = "";
	private String comments = "";
	private String referenceIdentifier = "";
	private String miscellaneous = "";
	private String formula = "";
	private String inChI = "";
	private String smiles = "";
	private double molWeight = 0;
	private String database = "";
	private String contributor = "";
	private String hit;
	private String classification;
	private int retentionTime = 0;
	private float retentionIndex = 0.0f;

	public AbstractLibraryInformation() {
		synonyms = new HashSet<String>();
	}

	/**
	 * Makes a copy of the given library information.
	 * 
	 * @param libraryInformation
	 */
	public AbstractLibraryInformation(ILibraryInformation libraryInformation) {
		this();
		if(libraryInformation != null) {
			name = libraryInformation.getName();
			for(String synonym : libraryInformation.getSynonyms()) {
				synonyms.add(synonym);
			}
			casNumber = libraryInformation.getCasNumber();
			comments = libraryInformation.getComments();
			referenceIdentifier = libraryInformation.getReferenceIdentifier();
			miscellaneous = libraryInformation.getMiscellaneous();
			formula = libraryInformation.getFormula();
			inChI = libraryInformation.getInChI();
			smiles = libraryInformation.getSmiles();
			molWeight = libraryInformation.getMolWeight();
			database = libraryInformation.getDatabase();
			contributor = libraryInformation.getContributor();
			hit = libraryInformation.getHit();
			classification = libraryInformation.getClassification();
			retentionTime = libraryInformation.getRetentionTime();
			retentionIndex = libraryInformation.getRetentionIndex();
		}
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
	public String getSmiles() {

		return smiles;
	}

	@Override
	public void setSmiles(String smiles) {

		if(smiles != null) {
			this.smiles = smiles;
		}
	}

	@Override
	public String getInChI() {

		return inChI;
	}

	@Override
	public void setInChI(String inChI) {

		if(inChI != null) {
			this.inChI = inChI;
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

	@Override
	public String getHit() {

		return hit;
	}

	public void setHit(String hit) {

		this.hit = hit;
	}

	@Override
	public String getClassification() {

		return classification;
	}

	public void setClassification(String classification) {

		this.classification = classification;
	}

	@Override
	public int getRetentionTime() {

		return retentionTime;
	}

	@Override
	public void setRetentionTime(int retentionTime) {

		this.retentionTime = retentionTime;
	}

	@Override
	public float getRetentionIndex() {

		return retentionIndex;
	}

	@Override
	public void setRetentionIndex(float retentionIndex) {

		this.retentionIndex = retentionIndex;
	}

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
}
