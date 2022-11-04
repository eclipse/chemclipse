/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.identifier;

import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class FlavorMarker implements IFlavorMarker {

	private String odor = "";
	private String matrix = "";
	private String solvent = "";
	private String samplePreparation = "";
	private String literatureReference = ""; // if possible, DOI
	private Set<IOdorThreshold> odorThresholds = new HashSet<>();

	public FlavorMarker(String odor, String matrix, String solvent) {

		this.odor = odor;
		this.matrix = matrix;
		this.solvent = solvent;
	}

	@Override
	public void clear() {

		odor = "";
		matrix = "";
		solvent = "";
		samplePreparation = "";
		literatureReference = "";
		odorThresholds.clear();
	}

	@Override
	public String getOdor() {

		return odor;
	}

	@Override
	public String getMatrix() {

		return matrix;
	}

	@Override
	public String getSolvent() {

		return solvent;
	}

	@Override
	public String getSamplePreparation() {

		return samplePreparation;
	}

	@Override
	public void setSamplePreparation(String samplePreparation) {

		this.samplePreparation = samplePreparation;
	}

	@Override
	public String getLiteratureReference() {

		return literatureReference;
	}

	@Override
	public void setLiteratureReference(String literatureReference) {

		this.literatureReference = literatureReference;
	}

	@Override
	public void add(IOdorThreshold flavorThreshold) {

		odorThresholds.add(flavorThreshold);
	}

	@Override
	public void remove(IOdorThreshold flavorThreshold) {

		odorThresholds.remove(flavorThreshold);
	}

	@Override
	public void clearOdorThresholds() {

		odorThresholds.clear();
	}

	@Override
	public Set<IOdorThreshold> getOdorThresholds() {

		return Collections.unmodifiableSet(odorThresholds);
	}

	@Override
	public int hashCode() {

		return Objects.hash(matrix, odor, solvent);
	}

	@Override
	public boolean equals(Object obj) {

		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		FlavorMarker other = (FlavorMarker)obj;
		return Objects.equals(matrix, other.matrix) && Objects.equals(odor, other.odor) && Objects.equals(solvent, other.solvent);
	}

	@Override
	public String toString() {

		return "FlavorMarker [odorQuality=" + odor + ", matrix=" + matrix + ", solvent=" + solvent + "]";
	}
}