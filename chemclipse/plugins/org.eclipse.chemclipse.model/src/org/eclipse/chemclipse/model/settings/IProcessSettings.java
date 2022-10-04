/*******************************************************************************
 * Copyright (c) 2018, 2022 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.settings;

public interface IProcessSettings {

	public static final String VARIABLE_CHROMATOGRAM_NAME = "{chromatogram_name}";
	public static final String VARIABLE_CHROMATOGRAM_DATANAME = "{chromatogram_dataname}";
	public static final String VARIABLE_CHROMATOGRAM_SAMPLEGROUP = "{chromatogram_samplegroup}";
	public static final String VARIABLE_CHROMATOGRAM_SHORTINFO = "{chromatogram_shortinfo}";
	public static final String VARIABLE_EXTENSION = "{extension}";

	/**
	 * Use this method to set specific system settings.
	 * 
	 * @deprecated this method will be removed soon
	 */
	@Deprecated
	default void setSystemSettings() {

		throw new UnsupportedOperationException();
	}
}