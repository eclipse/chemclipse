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
package org.eclipse.chemclipse.wsd.converter.supplier.cml.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.wsd.converter.supplier.cml.model.IVendorSpectrumWSD;
import org.eclipse.chemclipse.wsd.converter.supplier.cml.model.VendorSpectrumWSD;
import org.eclipse.chemclipse.wsd.model.core.implementation.SignalWSD;
import org.eclipse.chemclipse.xxd.converter.supplier.cml.io.RootElement;
import org.eclipse.chemclipse.xxd.converter.supplier.cml.model.v3.Array;
import org.eclipse.chemclipse.xxd.converter.supplier.cml.model.v3.Metadata;
import org.eclipse.chemclipse.xxd.converter.supplier.cml.model.v3.MetadataList;
import org.eclipse.chemclipse.xxd.converter.supplier.cml.model.v3.Molecule;
import org.eclipse.chemclipse.xxd.converter.supplier.cml.model.v3.Name;
import org.eclipse.chemclipse.xxd.converter.supplier.cml.model.v3.Sample;
import org.eclipse.chemclipse.xxd.converter.supplier.cml.model.v3.Spectrum;
import org.eclipse.chemclipse.xxd.converter.supplier.cml.model.v3.SpectrumData;
import org.eclipse.chemclipse.xxd.converter.supplier.cml.model.v3.SpectrumType;
import org.eclipse.chemclipse.xxd.converter.supplier.cml.model.v3.Xaxis;
import org.eclipse.chemclipse.xxd.converter.supplier.cml.model.v3.Yaxis;
import org.eclipse.core.runtime.IProgressMonitor;
import org.xml.sax.SAXException;

import jakarta.xml.bind.JAXBException;

public class ScanReader {

	private static final Logger logger = Logger.getLogger(ScanReader.class);

	public IVendorSpectrumWSD read(File file, IProgressMonitor monitor) {

		IVendorSpectrumWSD vendorScan = null;
		try {
			Spectrum spectrum = RootElement.getSpectrum(file);
			if(spectrum.getType() == SpectrumType.UV_VIS) {
				vendorScan = new VendorSpectrumWSD();
				vendorScan.setSampleName(spectrum.getTitle());
				vendorScan.setDataName(spectrum.getId());
				MetadataList metadataList = spectrum.getMetadataList();
				if(metadataList != null) {
					for(Metadata metadata : metadataList.getMetadata()) {
						vendorScan.setMiscInfo(vendorScan.getMiscInfo() + System.lineSeparator() + metadata.getValue());
					}
				}
				Sample sample = spectrum.getSample();
				if(sample != null) {
					List<Molecule> molecules = sample.getMolecule();
					if(molecules != null) {
						for(Molecule molecule : molecules) {
							Name name = molecule.getName();
							if(name != null) {
								if(name.getConvention().equals("cas:regno")) {
									vendorScan.setCasNumber(name.getValue());
								}
							}
						}
					}
				}
				SpectrumData spectrumData = spectrum.getSpectrumData();
				List<Double> wavelengths = new ArrayList<>();
				List<Double> absorbances = new ArrayList<>();
				if(spectrumData != null) {
					Xaxis xAxis = spectrumData.getXaxis();
					if(xAxis != null) {
						Array array = xAxis.getArray();
						if(!(array.getUnits().equals("unit:nm"))) {
							logger.warn("Unexpected wavelength unit " + array.getUnits());
						}
						if(array != null) {
							if(array.getStart() != null && array.getEnd() != null && array.getSize() != null) {
								double step = (array.getEnd() - array.getStart()) / (array.getSize().intValue() - 1);
								for(int i = 0; i < array.getSize().intValue(); i++) {
									double value = array.getStart() + i * step;
									wavelengths.add(value);
								}
							} else {
								wavelengths = Arrays.stream( //
										StringUtils.split(array.getValue())) //
										.map(Double::parseDouble) //
										.collect(Collectors.toList()); //
							}
						}
					}
					Yaxis yAxis = spectrumData.getYaxis();
					if(yAxis != null) {
						Array array = yAxis.getArray();
						if(array != null) {
							absorbances = Arrays.stream( //
									StringUtils.split(array.getValue())) //
									.map(Double::parseDouble).toList(); //
						}
					}
					int scans = Math.min(wavelengths.size(), absorbances.size());
					for(int i = 0; i < scans; i++) {
						vendorScan.getSignals().add(new SignalWSD(wavelengths.get(i).floatValue(), absorbances.get(i).floatValue(), 0d));
					}
				}
			}
		} catch(JAXBException | SAXException | IOException
				| ParserConfigurationException e) {
			logger.warn(e);
		}
		return vendorScan;
	}
}