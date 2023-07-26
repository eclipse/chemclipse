/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - adjust to new API
 *******************************************************************************/
package org.eclipse.chemclipse.model.targets;

import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.core.ITargetSupplier;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.preferences.PreferenceSupplier;

public class TargetSupport {

	/**
	 * Returns the best target string representation or "" if none is available.
	 * 
	 * @param object
	 * @return {@link String}
	 */
	public static String getBestTargetLibraryField(Object object) {

		if(object instanceof ITargetSupplier targetSupplier) {
			/*
			 * Is Retention Index used for QC?
			 */
			float retentionIndex = 0;
			if(PreferenceSupplier.isUseRetentionIndexQC()) {
				if(object instanceof IPeak peak) {
					retentionIndex = peak.getPeakModel().getPeakMaximum().getRetentionIndex();
				} else if(object instanceof IScan scan) {
					retentionIndex = scan.getRetentionIndex();
				}
			}
			/*
			 * Best Match
			 */
			IIdentificationTarget identificationTarget = IIdentificationTarget.getIdentificationTarget(targetSupplier.getTargets(), retentionIndex);
			LibraryField libraryField = PreferenceSupplier.getBestTargetLibraryField();
			String name = libraryField.getTransformer().apply(identificationTarget);
			if(name != null) {
				return name;
			}
		}
		//
		return "";
	}
}