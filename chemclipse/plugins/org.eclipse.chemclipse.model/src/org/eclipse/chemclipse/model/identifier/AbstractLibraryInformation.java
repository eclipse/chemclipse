/*******************************************************************************
 * Copyright (c) 2010, 2022 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Dr. Alexander Kerner - implementation
 * Christoph Läubrich - implement the methods for classifiable
 *******************************************************************************/
package org.eclipse.chemclipse.model.identifier;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Set;

import org.eclipse.chemclipse.model.columns.ISeparationColumn;
import org.eclipse.chemclipse.model.columns.SeparationColumnFactory;
import org.eclipse.chemclipse.model.columns.SeparationColumnType;

public abstract class AbstractLibraryInformation implements ILibraryInformation {

	/**
	 * Renew the UUID on change.
	 */
	private static final long serialVersionUID = -7091140092667283293L;
	//
	private String name = "";
	private final Set<String> synonyms = new LinkedHashSet<>();
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
	private final Set<String> classification = new LinkedHashSet<>();
	private final Set<IColumnPositionMarker> columnPositionMarkers = new LinkedHashSet<>();
	private String moleculeStructure = "";
	/*
	 * Default Column Position Marker
	 */
	private ISeparationColumn separationColumn = SeparationColumnFactory.getSeparationColumn(SeparationColumnType.DEFAULT);
	private ColumnPositionMarker columnPositionMarker = new ColumnPositionMarker(separationColumn);

	public AbstractLibraryInformation() {

		this(null);
		columnPositionMarkers.add(columnPositionMarker);
	}

	/**
	 * Makes a copy of the given library information.
	 * 
	 * @param libraryInformation
	 */
	public AbstractLibraryInformation(ILibraryInformation libraryInformation) {

		columnPositionMarkers.add(columnPositionMarker);
		if(libraryInformation != null) {
			name = libraryInformation.getName();
			synonyms.addAll(libraryInformation.getSynonyms());
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
			classification.addAll(libraryInformation.getClassifier());
			columnPositionMarker.setRetentionTime(libraryInformation.getRetentionTime());
			columnPositionMarker.setRetentionIndex(libraryInformation.getRetentionIndex());
			moleculeStructure = libraryInformation.getMoleculeStructure();
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

		this.synonyms.clear();
		if(synonyms != null) {
			this.synonyms.addAll(synonyms);
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
	public Collection<String> getClassifier() {

		return Collections.unmodifiableCollection(classification);
	}

	@Override
	public void addClassifier(String classifier) {

		classification.add(classifier);
	}

	@Override
	public void removeClassifier(String classifier) {

		classification.remove(classifier);
	}

	@Override
	public Set<IColumnPositionMarker> getColumnPositionMarkers() {

		return Collections.unmodifiableSet(columnPositionMarkers);
	}

	@Override
	public void add(IColumnPositionMarker columnPositionMarker) {

		if(columnPositionMarker != null) {
			if(isDefaultColumnPositionMarker(columnPositionMarker)) {
				this.columnPositionMarker.setRetentionTime(columnPositionMarker.getRetentionTime());
				this.columnPositionMarker.setRetentionIndex(columnPositionMarker.getRetentionIndex());
			} else {
				columnPositionMarkers.add(columnPositionMarker);
			}
		}
	}

	@Override
	public void delete(IColumnPositionMarker columnPositionMarker) {

		if(columnPositionMarker != null) {
			if(isDefaultColumnPositionMarker(columnPositionMarker)) {
				this.columnPositionMarker.clear();
			} else {
				columnPositionMarkers.remove(columnPositionMarker);
			}
		}
	}

	@Override
	public int getRetentionTime() {

		return columnPositionMarker.getRetentionTime();
	}

	@Override
	public void setRetentionTime(int retentionTime) {

		columnPositionMarker.setRetentionTime(retentionTime);
	}

	@Override
	public float getRetentionIndex() {

		return columnPositionMarker.getRetentionIndex();
	}

	@Override
	public void setRetentionIndex(float retentionIndex) {

		this.columnPositionMarker.setRetentionIndex(retentionIndex);
	}

	@Override
	public String getMoleculeStructure() {

		return moleculeStructure;
	}

	@Override
	public void setMoleculeStructure(String moleculeStructure) {

		this.moleculeStructure = moleculeStructure;
	}

	private boolean isDefaultColumnPositionMarker(IColumnPositionMarker columnPositionMarker) {

		return this.columnPositionMarker.equals(columnPositionMarker);
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