/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.massbank.model;

public class Chromatography {

	private String columnName = null;
	private String flowGradient = null;
	private String flowRate = null;
	private String solvent = null;

	public String getColumnName() {

		return columnName;
	}

	public void setColumnName(String columnName) {

		this.columnName = columnName;
	}

	public String getFlowGradient() {

		return flowGradient;
	}

	public void setFlowGradient(String flowGradient) {

		this.flowGradient = flowGradient;
	}

	public String getFlowRate() {

		return flowRate;
	}

	public void setFlowRate(String flowRate) {

		this.flowRate = flowRate;
	}

	public String getSolvent() {

		return solvent;
	}

	public void setSolvent(String solvent) {

		this.solvent = solvent;
	}
}
