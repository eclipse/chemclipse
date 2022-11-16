/*******************************************************************************
 * Copyright (c) 2017, 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 * Philip Wenig - adjustment for mass spectra
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.extraction;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.PeakSampleData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Sample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Samples;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.statistics.IVariable;
import org.eclipse.chemclipse.model.statistics.MassToChargeRatio;
import org.eclipse.chemclipse.msd.model.core.ILibraryMassSpectrum;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.xic.IExtractedIonSignal;
import org.eclipse.core.runtime.IProgressMonitor;

public class MassSpectrumExtractionSupport {

	public Samples process(Map<IDataInputEntry, IMassSpectra> dataInput, IProgressMonitor monitor) {

		/*
		 * Extract the m/z range of all spectra and add the scans.
		 */
		Set<Integer> mzSet = new HashSet<>();
		List<Sample> samplesList = new ArrayList<>();
		List<IScanMSD> libraryMassSpectra = new ArrayList<>();
		//
		for(IMassSpectra massSpectra : dataInput.values()) {
			for(IScanMSD scanMSD : massSpectra.getList()) {
				if(scanMSD instanceof ILibraryMassSpectrum libraryMassSpectrum) {
					//
					IExtractedIonSignal extractedIonSignal = scanMSD.getExtractedIonSignal();
					int startMZ = extractedIonSignal.getStartIon();
					int stopMZ = extractedIonSignal.getStopIon();
					for(int i = startMZ; i <= stopMZ; i++) {
						if(extractedIonSignal.getAbundance(i) > 0) {
							mzSet.add(i);
						}
					}
					//
					libraryMassSpectra.add(scanMSD);
					ILibraryInformation libraryInformation = libraryMassSpectrum.getLibraryInformation();
					String name = libraryInformation.getName();
					samplesList.add(new Sample(name, ""));
				}
			}
		}
		/*
		 * Create the samples
		 */
		Samples samples = new Samples(samplesList);
		/*
		 * Variables
		 */
		List<IVariable> variables = new ArrayList<>();
		List<Integer> ions = new ArrayList<>(mzSet);
		Collections.sort(ions);
		//
		for(int ion : ions) {
			variables.add(new MassToChargeRatio((double)ion));
		}
		samples.getVariables().addAll(variables);
		/*
		 * Data
		 */
		for(int i = 0; i < samplesList.size(); i++) {
			//
			Sample sample = samplesList.get(i);
			IScanMSD libraryMassSpectrum = libraryMassSpectra.get(i);
			IExtractedIonSignal extractedIonSignal = libraryMassSpectrum.getExtractedIonSignal();
			//
			List<PeakSampleData> sampleData = sample.getSampleData();
			for(int ion : ions) {
				double value = extractedIonSignal.getAbundance(ion);
				PeakSampleData peakSampleData = new PeakSampleData(value, null);
				sampleData.add(peakSampleData);
			}
		}
		//
		return samples;
	}
}