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

public class Compound implements ICompound {

	private List<IHit> hits;
	private String identifier;
	private String compoundInLibraryFactor = "";

	public Compound() {
		hits = new ArrayList<IHit>();
		identifier = "";
	}

	@Override
	public String getIdentfier() {

		return identifier;
	}

	@Override
	public void setIdentifier(String identifier) {

		if(identifier != null) {
			this.identifier = identifier;
		}
	}

	@Override
	public String getCompoundInLibraryFactor() {

		return compoundInLibraryFactor;
	}

	@Override
	public void setCompoundInLibraryFactor(String compoundInLibraryFactor) {

		if(compoundInLibraryFactor != null) {
			this.compoundInLibraryFactor = compoundInLibraryFactor;
		}
	}

	@Override
	public void add(IHit hit) {

		hits.add(hit);
	}

	@Override
	public void remove(IHit hit) {

		hits.remove(hit);
	}

	@Override
	public List<IHit> getHits() {

		return hits;
	}

	@Override
	public IHit getHit(int index) {

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

	@Override
	public int size() {

		return hits.size();
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
		ICompound otherCompound = (ICompound)other;
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
	// ------------------------------toString, hashCode, equals
}
