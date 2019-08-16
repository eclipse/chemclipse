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
 *******************************************************************************/
package org.eclipse.chemclipse.model.identifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AbstractIdentificationResult implements IIdentificationResult {

	private List<IIdentificationTarget> entries;

	public AbstractIdentificationResult() {
		entries = new ArrayList<>();
	}

	@Override
	public void add(IIdentificationTarget entry) {

		entries.add(entry);
	}

	// TODO optimieren
	@Override
	public IIdentificationTarget getBestHit() {

		float actMQ = Float.MIN_VALUE;
		IIdentificationTarget actualEntry = null;
		IComparisonResult comparisonResult;
		for(IIdentificationTarget entry : entries) {
			if(entry != null) {
				comparisonResult = entry.getComparisonResult();
				/*
				 * If the comparison result is null, continue with the next one.
				 */
				if(comparisonResult == null) {
					continue;
				}
				/*
				 * Otherwise check the quality and set the new object if
				 * appropriate.
				 */
				if(comparisonResult.getMatchFactor() >= actMQ) {
					actualEntry = entry;
					actMQ = comparisonResult.getMatchFactor();
				}
			}
		}
		return actualEntry;
	}

	@Override
	public void remove(IIdentificationTarget entry) {

		entries.remove(entry);
	}

	@Override
	public void removeAll() {

		entries.clear();
	}

	@Override
	public Collection<IIdentificationTarget> getIdentificationEntries() {

		return entries;
	}

	// ----------------------------hashCode, equals, toString
	// TODO
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
		IIdentificationResult otherResult = (IIdentificationResult)other;
		return hashCode() == otherResult.hashCode();
	}

	@Override
	public int hashCode() {

		return entries.hashCode();
	}

	@Override
	public String toString() {

		StringBuilder builder = new StringBuilder();
		builder.append(getClass().getName());
		builder.append("[");
		builder.append("size=" + entries.size());
		builder.append("]");
		return builder.toString();
	}
	// ----------------------------hashCode, equals, toString
}
