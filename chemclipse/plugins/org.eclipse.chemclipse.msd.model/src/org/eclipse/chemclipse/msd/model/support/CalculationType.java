/*******************************************************************************
 * Copyright (c) 2020, 2021 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.model.support;

public enum CalculationType {
	SUM("Sum"), //
	MEAN("Mean"), //
	MEDIAN("Median");

	private String label = "";

	private CalculationType(String label) {

		this.label = label;
	}

	public String getLabel() {

		return label;
	}

	public static String[][] getCalculationTypes() {

		CalculationType[] calculationTypes = values();
		String[][] elements = new String[calculationTypes.length][2];
		//
		int counter = 0;
		for(CalculationType calculationType : calculationTypes) {
			elements[counter][0] = calculationType.getLabel();
			elements[counter][1] = calculationType.name();
			counter++;
		}
		//
		return elements;
	}
}
