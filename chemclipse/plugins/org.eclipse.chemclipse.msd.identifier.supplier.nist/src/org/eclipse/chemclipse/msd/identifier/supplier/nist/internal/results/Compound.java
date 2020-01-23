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

import java.util.ArrayList;
import java.util.List;

public class Compound {

	private List<Hit> hits;
	private String identifier;
	private String compoundInLibraryFactor = "";

	public Compound() {
		hits = new ArrayList<Hit>();
		identifier = "";
	}

	public String getIdentfier() {

		return identifier;
	}

	public void setIdentifier(String identifier) {

		if(identifier != null) {
			this.identifier = identifier;
		}
	}

	public String getCompoundInLibraryFactor() {

		return compoundInLibraryFactor;
	}

	public void setCompoundInLibraryFactor(String compoundInLibraryFactor) {

		if(compoundInLibraryFactor != null) {
			this.compoundInLibraryFactor = compoundInLibraryFactor;
		}
	}

	public void add(Hit hit) {

		hits.add(hit);
	}

	public void remove(Hit hit) {

		hits.remove(hit);
	}

	public List<Hit> getHits() {

		return hits;
	}

	public Hit getHit(int index) {

		/*
		 * The user index starts with 1.
		 */
		index--;
		if(index >= 0 && index < hits.size()) {
			return hits.get(index);
		} else {
			return null;
		}
	}

	public int size() {

		return hits.size();
	}

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
		Compound otherCompound = (Compound)other;
		return size() == otherCompound.size() && hashCode() == otherCompound.hashCode();
	}

	@Override
	public int hashCode() {

		int hashCode = 0;
		int size = hits.size();
		for(int i = 0; i < size; i++) {
			hashCode = hits.get(i).hashCode() / size;
		}
		return hashCode;
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName());
		builder.append("[");
		builder.append("size=" + hits.size());
		builder.append(",");
		for(int i = 0; i < hits.size(); i++) {
			builder.append("hit=" + hits.get(i));
			builder.append(",");
		}
		builder.append("]");
		return builder.toString();
	}
}
