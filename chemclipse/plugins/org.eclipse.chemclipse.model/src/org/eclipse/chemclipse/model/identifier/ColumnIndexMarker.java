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

public class ColumnIndexMarker extends AbstractColumnMarker implements IColumnIndexMarker {

	private static final long serialVersionUID = -1241514494966350544L;
	//
	private float retentionIndex = 0.0f;

	public ColumnIndexMarker(ISeparationColumn separationColumn) {

		this(separationColumn, 0.0f);
	}

	public ColumnIndexMarker(ISeparationColumn separationColumn, float retentionIndex) {

		super(separationColumn);
		this.retentionIndex = retentionIndex;
	}

	@Override
	public void clear() {

		retentionIndex = 0.0f;
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

		return Objects.hash(retentionIndex, getSeparationColumn().hashCode());
	}

	@Override
	public boolean equals(Object obj) {

		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		ColumnIndexMarker other = (ColumnIndexMarker)obj;
		return Float.floatToIntBits(retentionIndex) == Float.floatToIntBits(other.retentionIndex) && Objects.equals(getSeparationColumn(), other.getSeparationColumn());
	}

	@Override
	public String toString() {

		return "ColumnIndexMarker [separationColumn=" + getSeparationColumn() + ", retentionIndex=" + retentionIndex + "]";
	}
}