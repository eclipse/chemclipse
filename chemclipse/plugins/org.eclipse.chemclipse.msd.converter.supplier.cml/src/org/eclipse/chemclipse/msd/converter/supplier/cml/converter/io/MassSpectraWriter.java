/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.converter.supplier.cml.converter.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.core.IMassSpectrumPeak;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.msd.converter.io.AbstractMassSpectraWriter;
import org.eclipse.chemclipse.msd.converter.io.IMassSpectraWriter;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.core.IStandaloneMassSpectrum;
import org.eclipse.chemclipse.xxd.converter.supplier.cml.model.v3.Array;
import org.eclipse.chemclipse.xxd.converter.supplier.cml.model.v3.Cml;
import org.eclipse.chemclipse.xxd.converter.supplier.cml.model.v3.Peak;
import org.eclipse.chemclipse.xxd.converter.supplier.cml.model.v3.PeakList;
import org.eclipse.chemclipse.xxd.converter.supplier.cml.model.v3.PeakShapeType;
import org.eclipse.chemclipse.xxd.converter.supplier.cml.model.v3.Spectrum;
import org.eclipse.chemclipse.xxd.converter.supplier.cml.model.v3.SpectrumData;
import org.eclipse.chemclipse.xxd.converter.supplier.cml.model.v3.SpectrumType;
import org.eclipse.chemclipse.xxd.converter.supplier.cml.model.v3.Xaxis;
import org.eclipse.chemclipse.xxd.converter.supplier.cml.model.v3.Yaxis;
import org.eclipse.core.runtime.IProgressMonitor;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

public class MassSpectraWriter extends AbstractMassSpectraWriter implements IMassSpectraWriter {

	private static final Logger logger = Logger.getLogger(MassSpectraWriter.class);

	@Override
	public void write(File file, IScanMSD massSpectrum, boolean append, IProgressMonitor monitor) throws FileIsNotWriteableException, IOException {

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Cml.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			Cml cml = new Cml();
			cml.setSpectrum(createSpectrum(massSpectrum));
			marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://www.xml-cml.org/schema ../../schema.xsd");
			marshaller.marshal(cml, file);
		} catch(JAXBException e) {
			logger.warn(e);
		}
	}

	@Override
	public void write(File file, IMassSpectra massSpectra, boolean append, IProgressMonitor monitor) throws FileIsNotWriteableException, IOException {

		write(file, massSpectra.getMassSpectrum(1), false, monitor);
	}

	private Spectrum createSpectrum(IScanMSD massSpectrum) {

		Spectrum spectrum = new Spectrum();
		spectrum.setType(SpectrumType.MASS_SPECTRUM);
		spectrum.setSpectrumData(createSpectrumData(massSpectrum));
		if(massSpectrum instanceof IStandaloneMassSpectrum standaloneMassSpectrum) {
			spectrum.setPeakList(createPeakList(standaloneMassSpectrum));
		}
		return spectrum;
	}

	private PeakList createPeakList(IStandaloneMassSpectrum standaloneMassSpectrum) {

		List<Peak> peaks = new ArrayList<>();
		for(IMassSpectrumPeak massSpectrumPeak : standaloneMassSpectrum.getPeaks()) {
			Peak peak = new Peak();
			peak.setXValue(massSpectrumPeak.getIon());
			peak.setXUnits("unit:mz");
			peak.setYValue(massSpectrumPeak.getAbundance());
			peak.setXUnits("cmls:relativeAbundance");
			peak.setPeakShape(PeakShapeType.SHARP);
			Iterator<IIdentificationTarget> targetsIterator = massSpectrumPeak.getTargets().iterator();
			if(targetsIterator.hasNext()) {
				peak.setTitle(targetsIterator.next().getLibraryInformation().getName());
			}
			peaks.add(peak);
		}
		PeakList peakList = new PeakList();
		peakList.setPeak(peaks);
		return peakList;
	}

	private SpectrumData createSpectrumData(IScanMSD massSpectrum) {

		SpectrumData spectrumData = new SpectrumData();
		spectrumData.setXaxis(createXAxis(massSpectrum));
		spectrumData.setYaxis(createYAxis(massSpectrum));
		return spectrumData;
	}

	private Xaxis createXAxis(IScanMSD massSpectrum) {

		Xaxis xAxis = new Xaxis();
		StringBuilder mzs = new StringBuilder();
		for(IIon ion : massSpectrum.getIons()) {
			mzs.append(ion.getIon());
			mzs.append(" ");
		}
		Array array = createArray();
		array.setValue(mzs.toString());
		array.setUnits("unit:mz");
		xAxis.setArray(array);
		return xAxis;
	}

	private Yaxis createYAxis(IScanMSD massSpectrum) {

		Yaxis yAxis = new Yaxis();
		StringBuilder abundances = new StringBuilder();
		for(IIon ion : massSpectrum.getIons()) {
			abundances.append(ion.getAbundance());
			abundances.append(" ");
		}
		Array array = createArray();
		array.setValue(abundances.toString());
		array.setUnits("cmls:relativeAbundance");
		yAxis.setArray(array);
		return yAxis;
	}

	private Array createArray() {

		Array array = new Array();
		array.setDataType("xsd:Double");
		return array;
	}
}
