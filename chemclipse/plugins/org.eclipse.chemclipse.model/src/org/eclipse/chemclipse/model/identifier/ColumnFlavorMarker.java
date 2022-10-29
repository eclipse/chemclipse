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

import java.util.Objects;

import org.eclipse.chemclipse.model.columns.ISeparationColumn;

public class ColumnFlavorMarker extends AbstractColumnMarker implements IColumnFlavorMarker {

	private String odorQuality = "";
	private String matrix = "";
	private String solvent = "";
	private String literatureReference = "";

	public ColumnFlavorMarker(ISeparationColumn separationColumn) {

		super(separationColumn);
	}

	@Override
	public void clear() {

		odorQuality = "";
		matrix = "";
		solvent = "";
		literatureReference = "";
	}

	public String getOdorQuality() {

		return odorQuality;
	}

	public void setOdorQuality(String odorQuality) {

		this.odorQuality = odorQuality;
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

	public String getLiteratureReference() {

		return literatureReference;
	}

	public void setLiteratureReference(String literatureReference) {

		this.literatureReference = literatureReference;
	}

	@Override
	public int hashCode() {

		return Objects.hash(matrix, odorQuality, solvent, getSeparationColumn().hashCode());
	}

	@Override
	public boolean equals(Object obj) {

		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		ColumnFlavorMarker other = (ColumnFlavorMarker)obj;
		return Objects.equals(matrix, other.matrix) && Objects.equals(odorQuality, other.odorQuality) && Objects.equals(solvent, other.solvent) && Objects.equals(getSeparationColumn(), other.getSeparationColumn());
	}

	@Override
	public String toString() {

		return "ColumnFlavorMarker [separationColumn=" + getSeparationColumn() + ", odorQuality=" + odorQuality + ", matrix=" + matrix + ", solvent=" + solvent + "]";
	}
}