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

public class ColumnPositionMarker extends AbstractColumnMarker implements IColumnPositionMarker {

	private int retentionTime = 0;
	private float retentionIndex = 0.0f;

	public ColumnPositionMarker(ISeparationColumn separationColumn) {

		super(separationColumn);
	}

	@Override
	public void clear() {

		retentionTime = 0;
		retentionIndex = 0.0f;
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

		return retentionIndex;
	}

	@Override
	public void setRetentionIndex(float retentionIndex) {

		this.retentionIndex = retentionIndex;
	}

	@Override
	public int hashCode() {

		return Objects.hash(retentionIndex, retentionTime, getSeparationColumn().hashCode());
	}

	@Override
	public boolean equals(Object obj) {

		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		ColumnPositionMarker other = (ColumnPositionMarker)obj;
		return Float.floatToIntBits(retentionIndex) == Float.floatToIntBits(other.retentionIndex) && retentionTime == other.retentionTime && Objects.equals(getSeparationColumn(), other.getSeparationColumn());
	}

	@Override
	public String toString() {

		return "SpecificPositionMarker [separationColumn=" + getSeparationColumn() + ", retentionTime=" + retentionTime + ", retentionIndex=" + retentionIndex + "]";
	}
}