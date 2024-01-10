/*******************************************************************************
 * Copyright (c) 2018, 2024 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.csd.converter.supplier.xy.preferences;

import org.eclipse.chemclipse.csd.converter.supplier.xy.Activator;
import org.eclipse.chemclipse.csd.converter.supplier.xy.io.DelimiterFormat;
import org.eclipse.chemclipse.csd.converter.supplier.xy.io.RetentionTimeFormat;
import org.eclipse.chemclipse.support.preferences.AbstractPreferenceSupplier;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;

public class PreferenceSupplier extends AbstractPreferenceSupplier implements IPreferenceSupplier {

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

		return Activator.getContext() != null;
	}

	@Override
	public String getPreferenceNode() {

		return Activator.getContext().getBundle().getSymbolicName();
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