/*******************************************************************************
 * Copyright (c) 2018 pwenig.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * pwenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.pcr.model.core;

public class Position implements Comparable<Position> {

	private String row;
	private int column;

	public String getRow() {

		return row;
	}

	public void setRow(String row) {

		this.row = row;
	}

	public int getColumn() {

		return column;
	}

	public void setColumn(int column) {

		this.column = column;
	}

	@Override
	public int hashCode() {

		final int prime = 31;
		int result = 1;
		result = prime * result + column;
		result = prime * result + ((row == null) ? 0 : row.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {

		if(this == obj)
			return true;
		if(obj == null)
			return false;
		if(getClass() != obj.getClass())
			return false;
		Position other = (Position)obj;
		if(column != other.column)
			return false;
		if(row == null) {
			if(other.row != null)
				return false;
		} else if(!row.equals(other.row))
			return false;
		return true;
	}

	@Override
	public String toString() {

		return "Position [row=" + row + ", column=" + column + "]";
	}

	@Override
	public int compareTo(Position position) {

		int result = 0;
		if(position != null) {
			result = row.compareTo(position.getRow());
			if(result == 0) {
				result = Integer.compare(column, position.getColumn());
			}
		}
		return result;
	}
}
