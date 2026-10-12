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
package org.eclipse.chemclipse.model.settings;

import java.util.List;

import org.eclipse.chemclipse.support.literature.LiteratureReference;

public interface IProcessSettings {

	/*
	 * File name placeholder
	 */
	public static final String VARIABLE_CHROMATOGRAM_NAME = "{chromatogram_name}";
	public static final String VARIABLE_CHROMATOGRAM_DATANAME = "{chromatogram_dataname}";
	public static final String VARIABLE_CHROMATOGRAM_SAMPLEGROUP = "{chromatogram_samplegroup}";
	public static final String VARIABLE_CHROMATOGRAM_SHORTINFO = "{chromatogram_shortinfo}";
	public static final String VARIABLE_CHROMATOGRAM_SAMPLENAME = "{chromatogram_samplename}";
	public static final String VARIABLE_CHROMATOGRAM_OPERATOR = "{chromatogram_operator}";
	public static final String VARIABLE_CHROMATOGRAM_INSTRUMENT = "{chromatogram_instrument}";
	public static final String VARIABLE_CHROMATOGRAM_TAGS = "{chromatogram_tags}";

	public static final String VARIABLE_MASSSPECTRUM_NAME = "{massspectrum_name}";
	public static final String VARIABLE_MASSSPECTRUM_SAMPLENAME = "{massspectrum_samplename}";
	public static final String VARIABLE_MASSSPECTRUM_OPERATOR = "{massspectrum_operator}";
	public static final String VARIABLE_MASSSPECTRUM_INSTRUMENT = "{massspectrum_instrument}";
	public static final String VARIABLE_MASSSPECTRUM_PLATE = "{massspectrum_plate}";
	public static final String VARIABLE_MASSSPECTRUM_POSITION = "{massspectrum_position}";

	public static final String VARIABLE_EXTENSION = "{extension}";
	/*
	 * Directory placeholder
	 */
	public static final String VARIABLE_CURRENT_DIRECTORY = "{current_directory}";

	List<LiteratureReference> getLiteratureReferences();
}