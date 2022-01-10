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
package org.eclipse.chemclipse.model.identifier;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;

public class DeltaCalculationSupport {

	public static boolean useTarget(IScan scanUnknown, IScan scanReference, IIdentifierSettings identifierSettings) {

		int retentionTimeUnknown = scanUnknown.getRetentionTime();
		float retentionIndexUnknown = scanUnknown.getRetentionIndex();
		int retentionTimeReference = scanReference.getRetentionTime();
		float retentionIndexReference = scanReference.getRetentionIndex();
		return useTarget(retentionTimeUnknown, retentionIndexUnknown, retentionTimeReference, retentionIndexReference, identifierSettings);
	}

	public static boolean useTarget(int retentionTimeUnknown, float retentionIndexUnknown, int retentionTimeReference, float retentionIndexReference, IIdentifierSettings identifierSettings) {

		final boolean useTarget;
		switch(identifierSettings.getDeltaCalculation()) {
			case RETENTION_TIME_MS:
				useTarget = useTarget(retentionTimeUnknown, retentionTimeReference, identifierSettings.getDeltaWindow());
				break;
			case RETENTION_TIME_MIN:
				useTarget = useTarget(retentionTimeUnknown / IChromatogram.MINUTE_CORRELATION_FACTOR, retentionTimeReference / IChromatogram.MINUTE_CORRELATION_FACTOR, identifierSettings.getDeltaWindow());
				break;
			case RETENTION_INDEX:
				useTarget = useTarget(retentionIndexUnknown, retentionIndexReference, identifierSettings.getDeltaWindow());
				break;
			default:
				useTarget = true;
				break;
		}
		//
		return useTarget;
	}

	/**
	 * Validates if the current target shall be used.
	 * 
	 * @param valueUnknown
	 * @param valueReference
	 * @param valueDelta
	 * @return boolean
	 */
	public static boolean useTarget(double valueUnknown, double valueReference, double valueDelta) {

		return valueReference >= (valueUnknown - valueDelta) && valueReference <= (valueUnknown + valueDelta);
	}
}