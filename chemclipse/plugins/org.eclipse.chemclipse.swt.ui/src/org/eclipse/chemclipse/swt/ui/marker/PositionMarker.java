/*******************************************************************************
 * Copyright (c) 2023, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.swt.ui.marker;

import java.util.Objects;

public class PositionMarker {

	private String identifier = "";
	private int retentionTime = 0;
	private int retentionIndex = 0;

	public PositionMarker(String identifier, int retentionTime, int retentionIndex) {

		this.identifier = identifier;
		this.retentionTime = retentionTime;
		this.retentionIndex = retentionIndex;
	}

	public String getIdentifier() {

		return identifier;
	}

	public int getRetentionTime() {

		return retentionTime;
	}

	public int getRetentionIndex() {

		return retentionIndex;
	}

	@Override
	public int hashCode() {

		return Objects.hash(identifier);
	}

	@Override
	public boolean equals(Object obj) {

		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		PositionMarker other = (PositionMarker)obj;
		return Objects.equals(identifier, other.identifier);
	}

	@Override
	public String toString() {

		return "PositionMarker [identifier=" + identifier + ", retentionTime=" + retentionTime + ", retentionIndex=" + retentionIndex + "]";
	}
}