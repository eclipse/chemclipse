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
package org.eclipse.chemclipse.vsd.converter.supplier.cml.io;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.vsd.converter.supplier.cml.model.IVendorSpectrumVSD;
import org.eclipse.chemclipse.vsd.converter.supplier.cml.model.VendorSpectrumVSD;
import org.eclipse.chemclipse.vsd.model.implementation.SignalInfrared;
import org.eclipse.chemclipse.xxd.converter.supplier.cml.io.RootElement;
import org.eclipse.chemclipse.xxd.converter.supplier.cml.model.v3.Array;
import org.eclipse.chemclipse.xxd.converter.supplier.cml.model.v3.ConditionList;
import org.eclipse.chemclipse.xxd.converter.supplier.cml.model.v3.Formula;
import org.eclipse.chemclipse.xxd.converter.supplier.cml.model.v3.Metadata;
import org.eclipse.chemclipse.xxd.converter.supplier.cml.model.v3.MetadataList;
import org.eclipse.chemclipse.xxd.converter.supplier.cml.model.v3.Molecule;
import org.eclipse.chemclipse.xxd.converter.supplier.cml.model.v3.Name;
import org.eclipse.chemclipse.xxd.converter.supplier.cml.model.v3.Parameter;
import org.eclipse.chemclipse.xxd.converter.supplier.cml.model.v3.ParameterList;
import org.eclipse.chemclipse.xxd.converter.supplier.cml.model.v3.PeakList;
import org.eclipse.chemclipse.xxd.converter.supplier.cml.model.v3.Sample;
import org.eclipse.chemclipse.xxd.converter.supplier.cml.model.v3.Scalar;
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

	public IVendorSpectrumVSD read(File file, IProgressMonitor monitor) {

		IVendorSpectrumVSD vendorScan = null;
		try {
			Spectrum spectrum = RootElement.getSpectrum(file);
			if(spectrum.getType() == SpectrumType.INFRARED || spectrum.getType() == SpectrumType.IR) {
				vendorScan = new VendorSpectrumVSD();
				vendorScan.setSampleName(spectrum.getTitle());
				vendorScan.setDataName(spectrum.getId());
				MetadataList metadataList = spectrum.getMetadataList();
				if(metadataList != null) {
					for(Metadata metadata : metadataList.getMetadata()) {
						vendorScan.setMiscInfo(vendorScan.getMiscInfo() + System.lineSeparator() + metadata.getValue());
					}
				}
				ParameterList parameterList = spectrum.getParameterList();
				if(parameterList != null) {
					for(Parameter parameter : parameterList.getParameter()) {
						if(parameter.getDictRef().equals("jcamp:SpectrometerDataSystem")) {
							vendorScan.setInstrument(parameter.getValue());
						}
					}
				}
				Sample sample = spectrum.getSample();
				if(sample != null) {
					List<Molecule> molecules = sample.getMolecule();
					if(molecules != null) {
						for(Molecule molecule : molecules) {
							Formula formula = molecule.getFormula();
							if(formula.getConcise() != null) {
								vendorScan.setFormula(formula.getConcise());
							}
							if(formula.getInline() != null) {
								vendorScan.setFormula(formula.getInline());
							}
							Name name = molecule.getName();
							if(name != null) {
								if(name.getConvention().equals("jcamp:casregistryno") || name.getConvention().equals("ccml:casregno")) {
									vendorScan.setCasNumber(name.getValue());
								}
							}
						}
					}
				}
				ConditionList conditionsList = spectrum.getConditionList();
				if(conditionsList != null) {
					List<Scalar> scalars = conditionsList.getScalar();
					if(scalars != null) {
						for(Scalar scalar : scalars) {
							logger.info("Ignoring scalar " + scalar.getValue());
						}
					}
				}
				SpectrumData spectrumData = spectrum.getSpectrumData();
				List<Double> waveNumbers = new ArrayList<>();
				List<Double> absorbances = new ArrayList<>();
				List<Double> transmittances = new ArrayList<>();
				if(spectrumData != null) {
					Xaxis xAxis = spectrumData.getXaxis();
					if(xAxis != null) {
						Array array = xAxis.getArray();
						if(!(array.getUnits().equals("newunits:cm-1") || array.getUnits().equals("jcampUnits:1/cm"))) {
							logger.warn("Unexpected wavenumber unit " + array.getUnits());
						}
						if(array != null) {
							if(array.getStart() != null && array.getEnd() != null && array.getSize() != null) {
								double step = (array.getEnd() - array.getStart()) / (array.getSize().intValue() - 1);
								for(int i = 0; i < array.getSize().intValue(); i++) {
									double value = array.getStart() + i * step;
									waveNumbers.add(value);
								}
							} else {
								waveNumbers = Arrays.stream( //
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
							if(array.getUnits().equals("cml:absorbance")) {
								absorbances = Arrays.stream( //
										StringUtils.split(array.getValue())) //
										.map(Double::parseDouble).toList(); //
							} else if(array.getUnits().equals("jcampUnits:transmittance")) {
								transmittances = Arrays.stream( //
										StringUtils.split(array.getValue())) //
										.map(Double::parseDouble).toList(); //
							}
						}
					}
					if(!absorbances.isEmpty()) {
						int scans = Math.min(waveNumbers.size(), absorbances.size());
						for(int i = 0; i < scans; i++) {
							vendorScan.getScanVSD().getProcessedSignals().add(new SignalInfrared(waveNumbers.get(i), absorbances.get(i), 0));
						}
					}
					if(!transmittances.isEmpty()) {
						int scans = Math.min(waveNumbers.size(), transmittances.size());
						for(int i = 0; i < scans; i++) {
							vendorScan.getScanVSD().getProcessedSignals().add(new SignalInfrared(waveNumbers.get(i), 0, transmittances.get(i)));
						}
					}
				}
				PeakList peakList = spectrum.getPeakList();
				if(peakList != null) {
					if(peakList.getPeak() != null) {
						logger.info("Ignoring " + peakList.getPeak().size() + " peaks.");
					}
					if(peakList.getPeakGroup() != null) {
						logger.info("Ignoring " + peakList.getPeakGroup().size() + " peak groups.");
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