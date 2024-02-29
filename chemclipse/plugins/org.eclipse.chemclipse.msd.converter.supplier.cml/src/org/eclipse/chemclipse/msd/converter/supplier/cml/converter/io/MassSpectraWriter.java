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
import java.io.FileWriter;
import java.io.IOException;

import org.eclipse.chemclipse.converter.exceptions.FileIsNotWriteableException;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.msd.converter.io.AbstractMassSpectraWriter;
import org.eclipse.chemclipse.msd.converter.io.IMassSpectraWriter;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IMassSpectra;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.xxd.converter.supplier.cml.model.v3.Array;
import org.eclipse.chemclipse.xxd.converter.supplier.cml.model.v3.Cml;
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

	@Override
	public void writeMassSpectrum(FileWriter fileWriter, IScanMSD massSpectrum, IProgressMonitor monitor) throws IOException {

		throw new UnsupportedOperationException();
	}

	private Spectrum createSpectrum(IScanMSD massSpectrum) {

		Spectrum spectrum = new Spectrum();
		spectrum.setType(SpectrumType.MASS_SPECTRUM);
		spectrum.setSpectrumData(createSpectrumData(massSpectrum));
		return spectrum;
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
