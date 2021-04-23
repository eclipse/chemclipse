/*******************************************************************************
 * Copyright (c) 2021 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.model.targets;

import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.identifier.ComparisonResult;
import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.LibraryInformation;
import org.eclipse.chemclipse.support.text.ValueFormat;

public class UnknownTargetBuilder {

	public static final String DELIMITER_TRACES = ",";
	public static final String DELIMITER_INTENSITY = "|";
	//
	private static final float MIN_FACTOR = 0.0f;
	private static final float MAX_FACTOR = 100.0f;

	public static ILibraryInformation getLibraryInformationUnknown(IScan unknown, TargetUnknownSettings targetUnknownSettings, String traces) {

		/*
		 * Unknown [57,71,43,85,41]
		 * Unknown [57,71,43,85,41 RI 782]
		 * Unknown [57,71,43,85,41 RT 4.34]
		 */
		ILibraryInformation libraryInformation = new LibraryInformation();
		//
		StringBuilder builder = new StringBuilder();
		builder.append(targetUnknownSettings.getTargetName());
		builder.append(" ");
		builder.append(targetUnknownSettings.getMarkerStart());
		/*
		 * Traces
		 */
		builder.append(traces);
		/*
		 * Retention Time
		 */
		if(targetUnknownSettings.isIncludeRetentionTime()) {
			builder.append(" ");
			builder.append("RT");
			builder.append(" ");
			double retentionTimeInMinutes = unknown.getRetentionTime() / IChromatogram.MINUTE_CORRELATION_FACTOR;
			builder.append(ValueFormat.getDecimalFormatEnglish("0.000").format(retentionTimeInMinutes));
		}
		/*
		 * Retention Index
		 */
		if(targetUnknownSettings.isIncludeRetentionIndex()) {
			builder.append(" ");
			builder.append("RI");
			builder.append(" ");
			float retentionIndex = unknown.getRetentionIndex();
			builder.append(ValueFormat.getDecimalFormatEnglish("0").format(retentionIndex));
		}
		//
		builder.append(targetUnknownSettings.getMarkerStop());
		libraryInformation.setName(builder.toString());
		//
		return libraryInformation;
	}

	public static IComparisonResult getComparisonResultUnknown(float matchQuality) {

		if(matchQuality < MIN_FACTOR || matchQuality > MAX_FACTOR) {
			matchQuality = MAX_FACTOR;
		}
		return new ComparisonResult(matchQuality, 0.0f, 0.0f, 0.0f);
	}
}
