/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.support;

import org.eclipse.chemclipse.model.cas.CasSupport;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.model.TracesSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;

public class IdentificationTargetSupport {

	private static final String IDENTIFIER_UNKNOWN = "Manual Identification";

	public static IIdentificationTarget getTargetUnknown(IScan scan) {

		IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
		float matchFactor = preferenceStore.getFloat(PreferenceConstants.P_MATCH_QUALITY_UNKNOWN_TARGET);
		boolean addRetentionIndex = preferenceStore.getBoolean(PreferenceConstants.P_UNKNOWN_TARGET_ADD_RETENTION_INDEX);
		IIdentificationTarget identificationTarget = IIdentificationTarget.createDefaultTarget(getUnknownTargetName(scan, addRetentionIndex), CasSupport.CAS_DEFAULT, IDENTIFIER_UNKNOWN, matchFactor);
		identificationTarget.setVerified(preferenceStore.getBoolean(PreferenceConstants.P_VERIFY_UNKNOWN_TARGET));
		//
		return identificationTarget;
	}

	public static String getUnknownTargetName(IScan scan, boolean addRetentionIndex) {

		StringBuilder builder = new StringBuilder();
		String traces = TracesSupport.getTraces(scan);
		//
		builder.append("Unknown");
		if(!traces.isEmpty()) {
			builder.append(" [");
			builder.append(traces);
			builder.append("]");
		}
		//
		if(addRetentionIndex) {
			int retentionIndex = Math.round(scan.getRetentionIndex());
			if(retentionIndex > 0) {
				builder.append(" - ");
				builder.append(Integer.toString(retentionIndex));
			}
		}
		//
		return builder.toString();
	}
}