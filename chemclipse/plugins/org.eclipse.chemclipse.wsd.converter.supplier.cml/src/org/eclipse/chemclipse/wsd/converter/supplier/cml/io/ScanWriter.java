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
 * Philip Wenig - refactoring vibrational spectroscopy
 *******************************************************************************/
package org.eclipse.chemclipse.wsd.converter.supplier.cml.io;

import java.io.File;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.wsd.model.core.ISignalWSD;
import org.eclipse.chemclipse.wsd.model.core.ISpectrumWSD;
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

public class ScanWriter {

	private static final Logger logger = Logger.getLogger(ScanWriter.class);

	public void write(File file, ISpectrumWSD spectrum, IProgressMonitor monitor) {

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(Cml.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			Cml cml = new Cml();
			cml.setSpectrum(createSpectrum(spectrum));
			marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, "http://www.xml-cml.org/schema ../../schema.xsd");
			marshaller.marshal(cml, file);
		} catch(JAXBException e) {
			logger.warn(e);
		}
	}

	private Spectrum createSpectrum(ISpectrumWSD uvVis) {

		Spectrum spectrum = new Spectrum();
		spectrum.setType(SpectrumType.UV_VIS);
		spectrum.setSpectrumData(createSpectrumData(uvVis));
		return spectrum;
	}

	private SpectrumData createSpectrumData(ISpectrumWSD uvVis) {

		SpectrumData spectrumData = new SpectrumData();
		spectrumData.setXaxis(createXAxis(uvVis));
		spectrumData.setYaxis(createYAxis(uvVis));
		return spectrumData;
	}

	private Xaxis createXAxis(ISpectrumWSD uvVis) {

		Xaxis xAxis = new Xaxis();
		StringBuilder wavelengths = new StringBuilder();
		for(ISignalWSD signal : uvVis.getSignals()) {
			wavelengths.append(signal.getWavelength());
			wavelengths.append(" ");
		}
		Array array = createArray();
		array.setValue(wavelengths.toString());
		array.setUnits("unit:nm");
		xAxis.setArray(array);
		return xAxis;
	}

	private Yaxis createYAxis(ISpectrumWSD uvVis) {

		Yaxis yAxis = new Yaxis();
		StringBuilder absorbances = new StringBuilder();
		for(ISignalWSD signal : uvVis.getSignals()) {
			absorbances.append(signal.getAbsorbance());
			absorbances.append(" ");
		}
		Array array = createArray();
		array.setValue(absorbances.toString());
		array.setUnits("cml:absorbance");
		yAxis.setArray(array);
		return yAxis;
	}

	private Array createArray() {

		Array array = new Array();
		array.setDataType("xsd:Double");
		return array;
	}
}