/*******************************************************************************
 * Copyright (c) 2018, 2021 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - add method to get/set masspectrum comparator
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.wsd.identifier.settings;

import org.eclipse.chemclipse.model.identifier.IdentifierAdapterSettings;
import org.eclipse.chemclipse.model.identifier.PenaltyCalculation;

/**
 * Default settings class, which sets all identifier settings values
 * to their defaults. Additionally, no JsonAnnotations are declared, so
 * that each identifier settings class, which don't need the underlying
 * settings, can re-use this default class.
 */
public abstract class IdentifierAdapterSettingsWSD extends IdentifierAdapterSettings implements IIdentifierSettingsWSD {

	@Override
	public boolean isSetResultAutomatically() {

		return super.isSetResultAutomatically();
	}

	@Override
	public void setSetResultAutomatically(boolean setResultAutomatically) {

		super.setSetResultAutomatically(setResultAutomatically);
	}

	@Override
	public PenaltyCalculation getPenaltyCalculation() {

		return super.getPenaltyCalculation();
	}

	@Override
	public void setPenaltyCalculation(PenaltyCalculation penaltyCalculation) {

		super.setPenaltyCalculation(penaltyCalculation);
	}

	@Override
	public int getRetentionTimeWindow() {

		return super.getRetentionTimeWindow();
	}

	@Override
	public void setRetentionTimeWindow(int retentionTimeWindow) {

		super.setRetentionTimeWindow(retentionTimeWindow);
	}

	@Override
	public float getRetentionIndexWindow() {

		return super.getRetentionIndexWindow();
	}

	@Override
	public void setRetentionIndexWindow(float retentionIndexWindow) {

		super.setRetentionIndexWindow(retentionIndexWindow);
	}

	@Override
	public float getPenaltyCalculationLevelFactor() {

		return super.getPenaltyCalculationLevelFactor();
	}

	@Override
	public void setPenaltyCalculationLevelFactor(float penaltyCalculationLevelFactor) {

		super.setPenaltyCalculationLevelFactor(penaltyCalculationLevelFactor);
	}

	@Override
	public float getMaxPenalty() {

		return super.getMaxPenalty();
	}

	@Override
	public void setMaxPenalty(float maxPenalty) {

		super.setMaxPenalty(maxPenalty);
	}
}
