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

	private String odorQuality = "";
	private Set<FlavorThreshold> flavorThresholds = new HashSet<>();
	private String matrix = "";
	private String solvent = "";
	private String samplePreparation = "";
	private String literatureReference = ""; // if possible, DOI

	@Override
	public void clear() {

		odorQuality = "";
		flavorThresholds.clear();
		matrix = "";
		solvent = "";
		samplePreparation = "";
		literatureReference = "";
	}

	public String getOdorQuality() {

		return odorQuality;
	}

	public void setOdorQuality(String odorQuality) {

		this.odorQuality = odorQuality;
	}

	public void add(FlavorThreshold flavorThreshold) {

		flavorThresholds.add(flavorThreshold);
	}

	public void remove(FlavorThreshold flavorThreshold) {

		flavorThresholds.remove(flavorThreshold);
	}

	/**
	 * Returns an unmodifiable set.
	 * 
	 * @return
	 */
	public Set<FlavorThreshold> getFlavorThresholds() {

		return Collections.unmodifiableSet(flavorThresholds);
	}

	public String getMatrix() {

		return matrix;
	}

	public void setMatrix(String matrix) {

		this.matrix = matrix;
	}

	public String getSolvent() {

		return solvent;
	}

	public void setSolvent(String solvent) {

		this.solvent = solvent;
	}

	public String getSamplePreparation() {

		return samplePreparation;
	}

	public void setSamplePreparation(String samplePreparation) {

		this.samplePreparation = samplePreparation;
	}

	public String getLiteratureReference() {

		return literatureReference;
	}

	public void setLiteratureReference(String literatureReference) {

		this.literatureReference = literatureReference;
	}

	@Override
	public int hashCode() {

		return Objects.hash(matrix, odorQuality, solvent);
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
		return Objects.equals(matrix, other.matrix) && Objects.equals(odorQuality, other.odorQuality) && Objects.equals(solvent, other.solvent);
	}

	@Override
	public String toString() {

		return "FlavorMarker [odorQuality=" + odorQuality + ", matrix=" + matrix + ", solvent=" + solvent + "]";
	}
}