/*******************************************************************************
 * Copyright (c) 2008, 2020 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.nist.internal.results;

public class Hit {

	private String name = "";
	private String formula = "";
	private float matchFactor = 0.0f;
	private float reverseMatchFactor = 0.0f;
	private float probability = 0.0f;
	private String cas = "";
	private int molecularWeight = 0;
	private String lib = "";
	private int id = 0;
	private int retentionIndex = 0;

	public String getName() {

		return name;
	}

	public void setName(String name) {

		if(name != null) {
			this.name = name;
		}
	}

	public String getFormula() {

		return formula;
	}

	public void setFormula(String formula) {

		if(formula != null) {
			this.formula = formula;
		}
	}

	public float getMatchFactor() {

		return matchFactor;
	}

	public void setMatchFactor(float matchFactor) {

		this.matchFactor = matchFactor;
	}

	public float getReverseMatchFactor() {

		return reverseMatchFactor;
	}

	public void setReverseMatchFactor(float reverseMatchFactor) {

		this.reverseMatchFactor = reverseMatchFactor;
	}

	public float getProbability() {

		return probability;
	}

	public void setProbability(float probability) {

		this.probability = probability;
	}

	public String getCAS() {

		return cas;
	}

	public void setCAS(String cas) {

		if(cas != null) {
			this.cas = cas;
		}
	}

	public int getMolecularWeight() {

		return molecularWeight;
	}

	public void setMolecularWeight(int molecularWeight) {

		this.molecularWeight = molecularWeight;
	}

	public String getLib() {

		return lib;
	}

	public void setLib(String lib) {

		if(lib != null) {
			this.lib = lib;
		}
	}

	public int getId() {

		return id;
	}

	public void setId(int id) {

		this.id = id;
	}

	public int getRetentionIndex() {

		return retentionIndex;
	}

	public void setRetentionIndex(int retentionIndex) {

		this.retentionIndex = retentionIndex;
	}

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
		Hit otherHit = (Hit)other;
		return getName().equals(otherHit.getName()) && getFormula().equals(otherHit.getFormula()) && getMatchFactor() == otherHit.getMatchFactor() && getReverseMatchFactor() == otherHit.getReverseMatchFactor() && getProbability() == otherHit.getProbability() && getCAS().equals(otherHit.getCAS()) && getMolecularWeight() == otherHit.getMolecularWeight();
	}

	@Override
	public int hashCode() {

		return 7 * name.hashCode() + 11 * formula.hashCode() + 13 * Float.valueOf(matchFactor).hashCode() + 17 * Float.valueOf(reverseMatchFactor).hashCode() + 13 * Float.valueOf(probability).hashCode() + 11 * cas.hashCode() + 7 * Integer.valueOf(molecularWeight).hashCode();
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName());
		builder.append("[");
		builder.append("name=" + name);
		builder.append(",");
		builder.append("formula=" + formula);
		builder.append(",");
		builder.append("mf=" + matchFactor);
		builder.append(",");
		builder.append("rmf=" + reverseMatchFactor);
		builder.append(",");
		builder.append("prob=" + probability);
		builder.append(",");
		builder.append("CAS=" + cas);
		builder.append(",");
		builder.append("mw=" + molecularWeight);
		builder.append(",");
		builder.append("lib=" + lib);
		builder.append(",");
		builder.append("id=" + id);
		builder.append(",");
		builder.append("ri=" + retentionIndex);
		builder.append("]");
		return builder.toString();
	}
}
