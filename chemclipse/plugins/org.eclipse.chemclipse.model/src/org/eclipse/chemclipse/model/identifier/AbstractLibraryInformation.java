/*******************************************************************************
 * Copyright (c) 2010, 2023 Lablicate GmbH.
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

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.model.columns.ISeparationColumn;
import org.eclipse.chemclipse.model.columns.SeparationColumnFactory;
import org.eclipse.chemclipse.model.columns.SeparationColumnType;

public abstract class AbstractLibraryInformation implements ILibraryInformation {

	private static final long serialVersionUID = 3604067975171089730L;
	//
	private String name = "";
	private final Set<String> synonyms = new LinkedHashSet<>();
	private final List<String> casNumbers = new ArrayList<>();
	private String comments = "";
	private String referenceIdentifier = "";
	private String miscellaneous = "";
	private String formula = "";
	private String inChI = "";
	private String inChIKey = "";
	private String smiles = "";
	private double molWeight = 0.0d;
	private double exactMass = 0.0d;
	private String database = "";
	private int databaseIndex = -1;
	private String contributor = "";
	private String hit;
	private final Set<String> classification = new LinkedHashSet<>();
	private int retentionTime = 0;
	private final Set<IColumnIndexMarker> columnIndexMarkers = new LinkedHashSet<>();
	private final Set<IFlavorMarker> flavorMarkers = new HashSet<>();
	private String moleculeStructure = "";
	private String compoundClass = "";
	/*
	 * Default Column Position Marker
	 */
	private ISeparationColumn separationColumn = SeparationColumnFactory.getSeparationColumn(SeparationColumnType.DEFAULT);
	private ColumnIndexMarker columnIndexMarker = new ColumnIndexMarker(separationColumn);

	public AbstractLibraryInformation() {

		this(null);
		columnIndexMarkers.add(columnIndexMarker);
	}

	/**
	 * Makes a copy of the given library information.
	 * 
	 * @param libraryInformation
	 */
	public AbstractLibraryInformation(ILibraryInformation libraryInformation) {

		columnIndexMarkers.add(columnIndexMarker);
		if(libraryInformation != null) {
			name = libraryInformation.getName();
			synonyms.addAll(libraryInformation.getSynonyms());
			casNumbers.addAll(libraryInformation.getCasNumbers());
			comments = libraryInformation.getComments();
			referenceIdentifier = libraryInformation.getReferenceIdentifier();
			miscellaneous = libraryInformation.getMiscellaneous();
			formula = libraryInformation.getFormula();
			inChI = libraryInformation.getInChI();
			inChIKey = libraryInformation.getInChIKey();
			smiles = libraryInformation.getSmiles();
			molWeight = libraryInformation.getMolWeight();
			exactMass = libraryInformation.getExactMass();
			database = libraryInformation.getDatabase();
			databaseIndex = libraryInformation.getDatabaseIndex();
			contributor = libraryInformation.getContributor();
			hit = libraryInformation.getHit();
			classification.addAll(libraryInformation.getClassifier());
			retentionTime = libraryInformation.getRetentionTime();
			columnIndexMarkers.addAll(libraryInformation.getColumnIndexMarkers());
			columnIndexMarker.setRetentionIndex(libraryInformation.getRetentionIndex());
			flavorMarkers.addAll(libraryInformation.getFlavorMarkers());
			moleculeStructure = libraryInformation.getMoleculeStructure();
			compoundClass = libraryInformation.getCompoundClass();
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

		return casNumbers.isEmpty() ? "" : casNumbers.get(0);
	}

	@Override
	public void setCasNumber(String casNumber) {

		if(casNumber != null) {
			if(!casNumbers.isEmpty()) {
				casNumbers.remove(0);
				casNumbers.add(0, casNumber);
			} else {
				casNumbers.add(casNumber);
			}
		}
	}

	@Override
	public void addCasNumber(String casNumber) {

		if(casNumber != null) {
			if(!casNumbers.contains(casNumber)) {
				casNumbers.add(casNumber);
			}
		}
	}

	@Override
	public void deleteCasNumber(String casNumber) {

		if(casNumber != null) {
			casNumbers.remove(casNumber);
		}
	}

	@Override
	public List<String> getCasNumbers() {

		return Collections.unmodifiableList(casNumbers);
	}

	@Override
	public void clearCasNumbers() {

		casNumbers.clear();
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
	public String getInChIKey() {

		return inChIKey;
	}

	@Override
	public void setInChIKey(String inChIKey) {

		if(inChIKey != null) {
			this.inChIKey = inChIKey;
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
	public double getExactMass() {

		return exactMass;
	}

	@Override
	public void setExactMass(double exactMass) {

		this.exactMass = exactMass;
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
	public int getDatabaseIndex() {

		return databaseIndex;
	}

	@Override
	public void setDatabaseIndex(int databaseIndex) {

		this.databaseIndex = databaseIndex;
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
	public List<IColumnIndexMarker> getColumnIndexMarkers() {

		return Collections.unmodifiableList(new ArrayList<>(columnIndexMarkers));
	}

	@Override
	public void add(IColumnIndexMarker columnIndexMarker) {

		if(columnIndexMarker != null) {
			if(isDefaultColumnIndexMarker(columnIndexMarker)) {
				this.columnIndexMarker.setRetentionIndex(columnIndexMarker.getRetentionIndex());
			} else {
				columnIndexMarkers.add(columnIndexMarker);
			}
		}
	}

	@Override
	public void delete(IColumnIndexMarker columnIndexMarker) {

		if(columnIndexMarker != null) {
			if(isDefaultColumnIndexMarker(columnIndexMarker)) {
				this.columnIndexMarker.clear();
			} else {
				columnIndexMarkers.remove(columnIndexMarker);
			}
		}
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

		return columnIndexMarker.getRetentionIndex();
	}

	@Override
	public void setRetentionIndex(float retentionIndex) {

		this.columnIndexMarker.setRetentionIndex(retentionIndex);
	}

	@Override
	public List<IFlavorMarker> getFlavorMarkers() {

		return Collections.unmodifiableList(new ArrayList<>(flavorMarkers));
	}

	@Override
	public void add(IFlavorMarker flavorMarker) {

		flavorMarkers.add(flavorMarker);
	}

	@Override
	public void delete(IFlavorMarker flavorMarker) {

		flavorMarkers.remove(flavorMarker);
	}

	@Override
	public void clearFlavorMarker() {

		flavorMarkers.clear();
	}

	@Override
	public String getMoleculeStructure() {

		return moleculeStructure;
	}

	@Override
	public void setMoleculeStructure(String moleculeStructure) {

		this.moleculeStructure = moleculeStructure;
	}

	@Override
	public String getCompoundClass() {

		return compoundClass;
	}

	@Override
	public void setCompoundClass(String compoundClass) {

		this.compoundClass = compoundClass;
	}

	private boolean isDefaultColumnIndexMarker(IColumnIndexMarker columnIndexMarker) {

		/*
		 * Compare the identity of the separation column and not of the index marker.
		 * Because, the index marker additionally contains the Retention Index, which is
		 * part of the equals method.
		 */
		return this.columnIndexMarker.getSeparationColumn().equals(columnIndexMarker.getSeparationColumn());
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

		return 7 * name.hashCode() + 11 * getCasNumber().hashCode() + 13 * comments.hashCode() + 11 * miscellaneous.hashCode() + 7 * formula.hashCode() + 11 * Double.valueOf(molWeight).hashCode();
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName());
		builder.append("[");
		builder.append("name=" + name);
		builder.append(",");
		builder.append("casNumber=" + getCasNumber());
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
