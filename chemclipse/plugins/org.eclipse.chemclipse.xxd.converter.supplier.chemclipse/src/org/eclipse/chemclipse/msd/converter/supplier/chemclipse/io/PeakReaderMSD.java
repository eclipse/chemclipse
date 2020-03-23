/*******************************************************************************
 * Copyright (c) 2013, 2020 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.chemclipse.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.eclipse.chemclipse.converter.exceptions.FileIsEmptyException;
import org.eclipse.chemclipse.converter.exceptions.FileIsNotReadableException;
import org.eclipse.chemclipse.msd.converter.io.IPeakReader;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.internal.io.PeakReader_0701;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.internal.io.PeakReader_0801;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.internal.io.PeakReader_0802;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.internal.io.PeakReader_0803;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.internal.io.PeakReader_0901;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.internal.io.PeakReader_0902;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.internal.io.PeakReader_0903;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.internal.io.PeakReader_1001;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.internal.io.PeakReader_1002;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.internal.io.PeakReader_1003;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.internal.io.PeakReader_1004;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.internal.io.PeakReader_1005;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.internal.io.PeakReader_1006;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.internal.io.PeakReader_1007;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.internal.io.PeakReader_1100;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.internal.io.PeakReader_1300;
import org.eclipse.chemclipse.msd.converter.supplier.chemclipse.internal.io.PeakReader_1301;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.IFormat;
import org.eclipse.chemclipse.xxd.converter.supplier.chemclipse.internal.support.ReaderHelper;
import org.eclipse.core.runtime.IProgressMonitor;

public class PeakReaderMSD implements IPeakReader {

	@SuppressWarnings("rawtypes")
	@Override
	public IProcessingInfo read(File file, IProgressMonitor monitor) throws FileNotFoundException, FileIsNotReadableException, FileIsEmptyException, IOException {

		IProcessingInfo processingInfo = null;
		ReaderHelper readerHelper = new ReaderHelper();
		String version = readerHelper.getVersion(file);
		/*
		 * It's used to support older versions of
		 * the *.ocb format.
		 * TODO Optimize
		 */
		IPeakReader peakReader = null;
		if(version.equals(IFormat.CHROMATOGRAM_VERSION_0701)) {
			peakReader = new PeakReader_0701();
		} else if(version.equals(IFormat.CHROMATOGRAM_VERSION_0801)) {
			peakReader = new PeakReader_0801();
		} else if(version.equals(IFormat.CHROMATOGRAM_VERSION_0802)) {
			peakReader = new PeakReader_0802();
		} else if(version.equals(IFormat.CHROMATOGRAM_VERSION_0803)) {
			peakReader = new PeakReader_0803();
		} else if(version.equals(IFormat.CHROMATOGRAM_VERSION_0901)) {
			peakReader = new PeakReader_0901();
		} else if(version.equals(IFormat.CHROMATOGRAM_VERSION_0902)) {
			peakReader = new PeakReader_0902();
		} else if(version.equals(IFormat.CHROMATOGRAM_VERSION_0903)) {
			peakReader = new PeakReader_0903();
		} else if(version.equals(IFormat.CHROMATOGRAM_VERSION_1001)) {
			peakReader = new PeakReader_1001();
		} else if(version.equals(IFormat.CHROMATOGRAM_VERSION_1002)) {
			peakReader = new PeakReader_1002();
		} else if(version.equals(IFormat.CHROMATOGRAM_VERSION_1003)) {
			peakReader = new PeakReader_1003();
		} else if(version.equals(IFormat.CHROMATOGRAM_VERSION_1004)) {
			peakReader = new PeakReader_1004();
		} else if(version.equals(IFormat.CHROMATOGRAM_VERSION_1005)) {
			peakReader = new PeakReader_1005();
		} else if(version.equals(IFormat.CHROMATOGRAM_VERSION_1006)) {
			peakReader = new PeakReader_1006();
		} else if(version.equals(IFormat.CHROMATOGRAM_VERSION_1007)) {
			peakReader = new PeakReader_1007();
		} else if(version.equals(IFormat.CHROMATOGRAM_VERSION_1100)) {
			peakReader = new PeakReader_1100();
		} else if(version.equals(IFormat.CHROMATOGRAM_VERSION_1300)) {
			peakReader = new PeakReader_1300();
		} else if(version.equals(IFormat.CHROMATOGRAM_VERSION_1301)) {
			peakReader = new PeakReader_1301();
		}
		//
		if(peakReader != null) {
			processingInfo = peakReader.read(file, monitor);
		}
		return processingInfo;
	}
}
