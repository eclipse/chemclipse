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
 *******************************************************************************/
package org.eclipse.chemclipse.msd.identifier.supplier.nist.internal.results;

public class Hit implements IHit {

	private String name = "";
	private String formula = "";
	private float mf = 0.0f;
	private float rmf = 0.0f;
	private float prob = 0.0f;
	private String cas = "";
	private int mw = 0;
	private String lib = "";
	private int id = 0;

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
	public float getMF() {

		return mf;
	}

	@Override
	public void setMF(float mf) {

		this.mf = mf;
	}

	@Override
	public float getRMF() {

		return rmf;
	}

	@Override
	public void setRMF(float rmf) {

		this.rmf = rmf;
	}

	@Override
	public float getProb() {

		return prob;
	}

	@Override
	public void setProb(float prob) {

		this.prob = prob;
	}

	@Override
	public String getCAS() {

		return cas;
	}

	@Override
	public void setCAS(String cas) {

		if(cas != null) {
			this.cas = cas;
		}
	}

	@Override
	public int getMw() {

		return mw;
	}

	@Override
	public void setMw(int mw) {

		this.mw = mw;
	}

	@Override
	public String getLib() {

		return lib;
	}

	@Override
	public void setLib(String lib) {

		if(lib != null) {
			this.lib = lib;
		}
	}

	@Override
	public int getId() {

		return id;
	}

	@Override
	public void setId(int id) {

		this.id = id;
	}

	// ------------------------------toString, hashCode, equals
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
		IHit otherHit = (Hit)other;
		return getName().equals(otherHit.getName()) && getFormula().equals(otherHit.getFormula()) && getMF() == otherHit.getMF() && getRMF() == otherHit.getRMF() && getProb() == otherHit.getProb() && getCAS().equals(otherHit.getCAS()) && getMw() == otherHit.getMw();
	}

	@Override
	public int hashCode() {

		return 7 * name.hashCode() + 11 * formula.hashCode() + 13 * Float.valueOf(mf).hashCode() + 17 * Float.valueOf(rmf).hashCode() + 13 * Float.valueOf(prob).hashCode() + 11 * cas.hashCode() + 7 * Integer.valueOf(mw).hashCode();
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
		builder.append("mf=" + mf);
		builder.append(",");
		builder.append("rmf=" + rmf);
		builder.append(",");
		builder.append("prob=" + prob);
		builder.append(",");
		builder.append("CAS=" + cas);
		builder.append(",");
		builder.append("mw=" + mw);
		builder.append(",");
		builder.append("lib=" + lib);
		builder.append(",");
		builder.append("id=" + id);
		builder.append("]");
		return builder.toString();
	}
	// ------------------------------toString, hashCode, equals
}
