/*******************************************************************************
 * Copyright (c) 2018, 2021 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.columns;

import java.util.TreeMap;

public class SeparationColumnIndices extends TreeMap<Integer, IRetentionIndexEntry> implements ISeparationColumnIndices {

	private static final long serialVersionUID = -104734015201641034L;
	private ISeparationColumn separationColumn = SeparationColumnFactory.getSeparationColumn(SeparationColumnType.DEFAULT);

	@Override
	public ISeparationColumn getSeparationColumn() {

		return separationColumn;
	}

	@Override
	public void setSeparationColumn(ISeparationColumn separationColumn) {

		this.separationColumn = separationColumn;
	}

	@Override
	public void put(IRetentionIndexEntry retentionIndexEntry) {

		if(retentionIndexEntry != null) {
			put(retentionIndexEntry.getRetentionTime(), retentionIndexEntry);
		}
	}

	@Override
	public String toString() {

		return "SeparationColumnIndices [separationColumn=" + separationColumn + "]";
	}

	@Override
	public int hashCode() {

		return separationColumn.hashCode();
	}

	@Override
	public boolean equals(Object obj) {

		if(this == obj) {
			return true;
		}
		if(obj == null) {
			return false;
		}
		if(getClass() != obj.getClass()) {
			return false;
		}
		ISeparationColumnIndices other = (ISeparationColumnIndices)obj;
		if(!other.equals(this)) {
			return false;
		}
		return true;
	}
}
