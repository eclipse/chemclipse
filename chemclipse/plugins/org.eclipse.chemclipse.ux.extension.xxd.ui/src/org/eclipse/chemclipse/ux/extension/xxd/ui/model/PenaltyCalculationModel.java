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
package org.eclipse.chemclipse.ux.extension.xxd.ui.model;

import org.eclipse.chemclipse.model.identifier.PenaltyCalculation;

public class PenaltyCalculationModel {

	private PenaltyCalculation penaltyCalculation;
	private double referenceValue;
	private double penaltyWindow;
	private double penaltyLevelFactor;
	private double maxPenalty;

	public PenaltyCalculationModel(PenaltyCalculation penaltyCalculation, double referenceValue, double penaltyWindow, double penaltyLevelFactor, double maxPenalty) {

		this.penaltyCalculation = penaltyCalculation;
		this.referenceValue = referenceValue;
		this.penaltyWindow = penaltyWindow;
		this.penaltyLevelFactor = penaltyLevelFactor;
		this.maxPenalty = maxPenalty;
	}

	public PenaltyCalculation getPenaltyCalculation() {

		return penaltyCalculation;
	}

	public double getReferenceValue() {

		return referenceValue;
	}

	public double getPenaltyWindow() {

		return penaltyWindow;
	}

	public double getPenaltyLevelFactor() {

		return penaltyLevelFactor;
	}

	public double getMaxPenalty() {

		return maxPenalty;
	}
}