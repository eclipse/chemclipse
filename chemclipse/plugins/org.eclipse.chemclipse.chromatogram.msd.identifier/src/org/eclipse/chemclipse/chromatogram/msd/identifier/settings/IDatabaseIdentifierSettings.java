/*******************************************************************************
 * Copyright (c) 2016 Lablicate UG (haftungsbeschränkt).
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

public interface IDatabaseIdentifierSettings extends IIdentifierSettings {

	// Database
	String getForceMatchFactorPenaltyCalculationForDatabase();

	void setForceMatchFactorPenaltyCalculationForDatabase(String forceMatchFactorPenaltyCalculation);

	float getRetentionIndexWindowForDatabase();

	void setRetentionIndexWindowForDatabase(float retentionIndexWindow);

	float getRetentionTimeWindowForDatabase();

	void setRetentionTimeWindowForDatabase(float retentionTimeWindow);
}
