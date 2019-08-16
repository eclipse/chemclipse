/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.identifier.settings;

public abstract class AbstractDatabaseIdentifierSettings extends AbstractIdentifierSettingsMSD implements IDatabaseIdentifierSettings {

	private int retentionTimeWindowForDatabase; // milliseconds
	private int retentionIndexWindowForDatabase;
	private String forceMatchFactorPenaltyCalculationForDatabase;

	@Override
	public String getForceMatchFactorPenaltyCalculationForDatabase() {

		return forceMatchFactorPenaltyCalculationForDatabase;
	}

	@Override
	public void setForceMatchFactorPenaltyCalculationForDatabase(String forceMatchFactorPenaltyCalculationForDatabase) {

		this.forceMatchFactorPenaltyCalculationForDatabase = forceMatchFactorPenaltyCalculationForDatabase;
	}

	@Override
	public int getRetentionTimeWindowForDatabase() {

		return retentionTimeWindowForDatabase;
	}

	@Override
	public void setRetentionTimeWindowForDatabase(int retentionTimeWindowForDatabase) {

		this.retentionTimeWindowForDatabase = retentionTimeWindowForDatabase;
	}

	@Override
	public int getRetentionIndexWindowForDatabase() {

		return retentionIndexWindowForDatabase;
	}

	@Override
	public void setRetentionIndexWindowForDatabase(int retentionIndexWindowForDatabase) {

		this.retentionIndexWindowForDatabase = retentionIndexWindowForDatabase;
	}
}
