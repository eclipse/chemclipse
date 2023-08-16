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
package org.eclipse.chemclipse.msd.converter.supplier.amdis.io.chromatogram;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.List;

import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.core.IScan;
import org.eclipse.chemclipse.model.identifier.ComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.LibraryInformation;
import org.eclipse.chemclipse.model.implementation.IdentificationTarget;
import org.eclipse.chemclipse.msd.converter.io.IMassSpectraWriter;
import org.eclipse.chemclipse.msd.model.core.IChromatogramMSD;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.core.runtime.IProgressMonitor;

public class ScanWriter {

	private IMassSpectraWriter massSpectraWriter;

	public ScanWriter(IMassSpectraWriter massSpectraWriter) {

		this.massSpectraWriter = massSpectraWriter;
	}

	public void writeChromatogram(File file, IChromatogramMSD chromatogram, IProgressMonitor monitor) throws FileIsNotWriteableException, IOException {

		if(chromatogram != null && file != null) {
			/*
			 * Header
			 */
			List<IScan> scans = chromatogram.getScans();
			DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish("0.000");
			try (PrintWriter printWriter = new PrintWriter(file)) {
				printWriter.print("# Name: ");
				printWriter.println(chromatogram.getName());
				printWriter.print("# Retention Time (Start): ");
				printWriter.println(decimalFormat.format(chromatogram.getStartRetentionTime() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR));
				printWriter.print("# Retention Time (Stop): ");
				printWriter.println(decimalFormat.format(chromatogram.getStopRetentionTime() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR));
				printWriter.print("# Number Scans: ");
				printWriter.println(scans.size());
				printWriter.println("");
			}
			/*
			 * Data
			 */
			int scanNumber = 1;
			for(IScan scan : scans) {
				if(scan instanceof IScanMSD scanMSD) {
					/*
					 * Scan Number Label
					 */
					IIdentificationTarget identificationTarget = getIdentificationTarget(scanNumber);
					scanMSD.getTargets().add(identificationTarget);
					massSpectraWriter.write(file, scanMSD, true, monitor);
					scanMSD.getTargets().remove(identificationTarget);
					scanNumber++;
				}
			}
		}
	}

	private IIdentificationTarget getIdentificationTarget(int scanNumber) {

		ILibraryInformation libraryInformation = new LibraryInformation();
		libraryInformation.setName("Scan " + Integer.toString(scanNumber));
		//
		return new IdentificationTarget(libraryInformation, ComparisonResult.createBestMatchComparisonResult());
	}
}