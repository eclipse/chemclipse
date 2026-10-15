/*******************************************************************************
 * Copyright (c) 2018, 2026 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.process.supplier.batchprocess.preferences;

import org.eclipse.chemclipse.support.preferences.AbstractPreferenceSupplier;
import org.eclipse.chemclipse.support.preferences.IPreferenceSupplier;
import org.osgi.framework.FrameworkUtil;

public class PreferenceSupplier extends AbstractPreferenceSupplier {

	public static final String P_FILTER_PATH_IMPORT_RECORDS = "filterPathImportRecords";
	public static final String DEF_FILTER_PATH_IMPORT_RECORDS = "";

	public static IPreferenceSupplier INSTANCE() {

		return INSTANCE(PreferenceSupplier.class);
	}

	@Override
	public String getPreferenceNode() {

		return FrameworkUtil.getBundle(PreferenceSupplier.class).getSymbolicName();
	}

	@Override
	public void initializeDefaults() {

		putDefault(P_FILTER_PATH_IMPORT_RECORDS, DEF_FILTER_PATH_IMPORT_RECORDS);
	}
}