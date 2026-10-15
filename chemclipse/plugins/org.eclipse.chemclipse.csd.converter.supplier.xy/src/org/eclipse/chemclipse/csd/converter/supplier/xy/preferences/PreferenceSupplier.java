/*******************************************************************************
 * Copyright (c) 2018, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.converter.supplier.xy.preferences;

import org.eclipse.chemclipse.csd.converter.supplier.xy.io.DelimiterFormat;
import org.eclipse.chemclipse.csd.converter.supplier.xy.io.RetentionTimeFormat;
import org.eclipse.chemclipse.support.preferences.AbstractPreferenceSupplier;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.osgi.framework.FrameworkUtil;

public class PreferenceSupplier extends AbstractPreferenceSupplier {

	public static final String P_AUTO_DETECT_FORMAT = "autoDetectFormat";
	public static final boolean DEF_AUTO_DETECT_FORMAT = true;
	public static final String P_DELIMITER_FORMAT = "delimiterFormat";
	public static final String DEF_DELIMITER_FORMAT = DelimiterFormat.TAB.name();
	public static final String P_RETENTION_TIME_FORMAT = "timeFormat";
	public static final String DEF_RETENTION_TIME_FORMAT = RetentionTimeFormat.MINUTES.name();

	public static IPreferenceSupplier INSTANCE() {

		return INSTANCE(PreferenceSupplier.class);
	}

	public static boolean isAvailable() {

		return FrameworkUtil.getBundle(PreferenceSupplier.class) != null;
	}

	@Override
	public String getPreferenceNode() {

		return FrameworkUtil.getBundle(PreferenceSupplier.class).getSymbolicName();
	}

	@Override
	public void initializeDefaults() {

		putDefault(P_AUTO_DETECT_FORMAT, Boolean.toString(DEF_AUTO_DETECT_FORMAT));
		putDefault(P_DELIMITER_FORMAT, DEF_DELIMITER_FORMAT);
		putDefault(P_RETENTION_TIME_FORMAT, DEF_RETENTION_TIME_FORMAT);
	}

	public static boolean isAutoDetectFormat() {

		return INSTANCE().getBoolean(P_AUTO_DETECT_FORMAT, DEF_AUTO_DETECT_FORMAT);
	}

	public static DelimiterFormat getDelimiterFormat() {

		try {
			return DelimiterFormat.valueOf(INSTANCE().get(P_DELIMITER_FORMAT, DEF_DELIMITER_FORMAT));
		} catch(Exception e) {
			return DelimiterFormat.TAB;
		}
	}

	public static RetentionTimeFormat getRetentionTimeFormat() {

		try {
			return RetentionTimeFormat.valueOf(INSTANCE().get(P_RETENTION_TIME_FORMAT, DEF_RETENTION_TIME_FORMAT));
		} catch(Exception e) {
			return RetentionTimeFormat.MINUTES;
		}
	}
}