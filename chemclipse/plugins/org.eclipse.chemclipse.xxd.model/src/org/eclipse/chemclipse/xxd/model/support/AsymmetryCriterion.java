/*******************************************************************************
 * Copyright (c) 2020, 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Alexander Stark - initial API and implementation
 * Philip Wenig - refactoring ILabel support
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.model.support;

import org.eclipse.chemclipse.support.text.ILabel;

public enum AsymmetryCriterion implements ILabel {
	ASYMMETRY_FACTOR_SMALLER_THAN_LIMIT("Asymmetry < Factor"), // Select peaks whose peak asymmetry factor is too small
	ASYMMETRY_FACTOR_GREATER_THAN_LIMIT("Asymmetry > Factor"); // Select peaks whose peak asymmetry factor is too large

	private String label;

	private AsymmetryCriterion(String label) {

		this.label = label;
	}

	@Override
	public String label() {

		return label;
	}

	public static String[][] getOptions() {

		return ILabel.getOptions(values());
	}
}