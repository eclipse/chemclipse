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
package org.eclipse.chemclipse.wsd.converter.supplier.spectroml.io;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.wsd.converter.supplier.spectroml.model.IVendorSpectrumWSD;
import org.eclipse.chemclipse.wsd.converter.supplier.spectroml.model.VendorSpectrumWSD;
import org.eclipse.chemclipse.wsd.converter.supplier.spectroml.model.v1.DataCore;
import org.eclipse.chemclipse.wsd.converter.supplier.spectroml.model.v1.Experiment;
import org.eclipse.chemclipse.wsd.converter.supplier.spectroml.model.v1.InstrumentDescription;
import org.eclipse.chemclipse.wsd.converter.supplier.spectroml.model.v1.SampleDescription;
import org.eclipse.chemclipse.wsd.converter.supplier.spectroml.model.v1.SampleDesignation;
import org.eclipse.chemclipse.wsd.converter.supplier.spectroml.model.v1.SpectroML;
import org.eclipse.chemclipse.wsd.converter.supplier.spectroml.model.v1.TimeStamp;
import org.eclipse.chemclipse.wsd.converter.supplier.spectroml.model.v1.Value;
import org.eclipse.chemclipse.wsd.model.core.implementation.SignalWSD;
import org.eclipse.core.runtime.IProgressMonitor;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

public class ScanReader {

	private static final Logger logger = Logger.getLogger(ScanReader.class);

	public IVendorSpectrumWSD read(File file, IProgressMonitor monitor) {

		IVendorSpectrumWSD vendorSpectrum = null;
		try {
			SpectroML spectroML = getSpectroML(file);
			vendorSpectrum = new VendorSpectrumWSD();
			for(Experiment experiment : spectroML.getExperimentList()) {
				if(!experiment.getType().equals("UV/Vis")) {
					logger.warn("Unknown experiment type " + experiment.getType());
				}
				vendorSpectrum.setDataName(experiment.getFile().getTitle());
				vendorSpectrum.setDate(convertDate(experiment.getFile().getTimeStamp()));
				vendorSpectrum.setDetailedInfo(experiment.getFile().getComment());
				for(InstrumentDescription instrumentDescription : experiment.getInstrument().getInstrumentDescriptionList()) {
					vendorSpectrum.setInstrument(instrumentDescription.getInstrumentDesignation().getModel());
					vendorSpectrum.setOperator(instrumentDescription.getInstrumentApplication().getOperator().getName());
				}
				for(SampleDescription sampleDescription : experiment.getSample().getSampleDescriptionList()) {
					SampleDesignation designation = sampleDescription.getSampleDesignation();
					vendorSpectrum.setBarcode(designation.getIdentifier());
					vendorSpectrum.setSampleName(designation.getName());
					vendorSpectrum.setCasNumber(designation.getCasNumber());
					vendorSpectrum.setFormula(designation.getFormula());
				}
				for(DataCore dataCore : experiment.getData().getDataCoreList()) {
					List<Double> wavelengths = new ArrayList<>();
					List<Double> transmittances = new ArrayList<>();
					for(Value values : dataCore.getValuesList()) {
						if(values.getDimension().equals("x")) {
							wavelengths = Arrays.stream( //
									StringUtils.split(values.getValue())) //
									.map(Double::parseDouble) //
									.toList(); //
						}
						if(values.getDimension().equals("y")) {
							transmittances = Arrays.stream( //
									StringUtils.split(values.getValue())) //
									.map(Double::parseDouble) //
									.toList(); //
						}
					}
					int scans = Math.min(wavelengths.size(), transmittances.size());
					for(int i = 0; i < scans; i++) {
						vendorSpectrum.getSignals().add(new SignalWSD(wavelengths.get(i).floatValue(), 0d, transmittances.get(0)));
					}
				}
			}
		} catch(JAXBException | SAXException | IOException
				| ParserConfigurationException e) {
			logger.warn(e);
		}
		return vendorSpectrum;
	}

	private Date convertDate(TimeStamp timestamp) {

		LocalTime time = LocalTime.parse(timestamp.getTime());
		LocalDateTime dateTime = timestamp.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
		LocalDateTime updatedDateTime = dateTime.withHour(time.getHour()).withMinute(time.getMinute()).withSecond(time.getSecond());
		return Date.from(updatedDateTime.atZone(ZoneId.systemDefault()).toInstant());
	}

	private static SpectroML getSpectroML(File file) throws SAXException, IOException, JAXBException, ParserConfigurationException {

		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		documentBuilderFactory.setNamespaceAware(true);
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.parse(file);
		NodeList topNode = document.getElementsByTagName("SpectroML");
		//
		JAXBContext jaxbContext = JAXBContext.newInstance(SpectroML.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		return (SpectroML)unmarshaller.unmarshal(topNode.item(0));
	}
}