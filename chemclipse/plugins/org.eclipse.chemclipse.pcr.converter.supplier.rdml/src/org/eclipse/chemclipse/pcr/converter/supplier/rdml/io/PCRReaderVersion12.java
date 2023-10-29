/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.pcr.converter.supplier.rdml.io;

import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;

import javax.xml.datatype.XMLGregorianCalendar;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.chemclipse.pcr.converter.supplier.rdml.internal.v12.model.DataType;
import org.eclipse.chemclipse.pcr.converter.supplier.rdml.internal.v12.model.DpAmpCurveType;
import org.eclipse.chemclipse.pcr.converter.supplier.rdml.internal.v12.model.DyeType;
import org.eclipse.chemclipse.pcr.converter.supplier.rdml.internal.v12.model.ExperimentType;
import org.eclipse.chemclipse.pcr.converter.supplier.rdml.internal.v12.model.IdReferencesType;
import org.eclipse.chemclipse.pcr.converter.supplier.rdml.internal.v12.model.ObjectFactory;
import org.eclipse.chemclipse.pcr.converter.supplier.rdml.internal.v12.model.PcrFormatType;
import org.eclipse.chemclipse.pcr.converter.supplier.rdml.internal.v12.model.Rdml;
import org.eclipse.chemclipse.pcr.converter.supplier.rdml.internal.v12.model.ReactType;
import org.eclipse.chemclipse.pcr.converter.supplier.rdml.internal.v12.model.RunType;
import org.eclipse.chemclipse.pcr.converter.supplier.rdml.model.IPCRReader;
import org.eclipse.chemclipse.pcr.converter.supplier.rdml.model.IVendorPlate;
import org.eclipse.chemclipse.pcr.converter.supplier.rdml.model.VendorPlate;
import org.eclipse.chemclipse.pcr.converter.support.AmplificationAnalysis;
import org.eclipse.chemclipse.pcr.model.core.Channel;
import org.eclipse.chemclipse.pcr.model.core.ChannelSpecification;
import org.eclipse.chemclipse.pcr.model.core.DetectionFormat;
import org.eclipse.chemclipse.pcr.model.core.IChannelSpecification;
import org.eclipse.chemclipse.pcr.model.core.IPlate;
import org.eclipse.chemclipse.pcr.model.core.IWell;
import org.eclipse.chemclipse.pcr.model.core.Position;
import org.eclipse.chemclipse.pcr.model.core.Well;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Unmarshaller;

public class PCRReaderVersion12 implements IPCRReader {

	@Override
	public IPlate readData(InputStream inputStream) throws SAXException, IOException, JAXBException, ParserConfigurationException, InvalidParameterException {

		IVendorPlate vendorPlate = new VendorPlate();
		Rdml rdml = getRDML(inputStream);
		setDate(vendorPlate, rdml);
		setDefaultDetectionFormat(vendorPlate);
		readExperiment(vendorPlate, rdml);
		AmplificationAnalysis.calculateCrossingPoints(vendorPlate);
		return vendorPlate;
	}

	private static Rdml getRDML(InputStream inputStream) throws SAXException, IOException, JAXBException, ParserConfigurationException {

		DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
		Document document = documentBuilder.parse(inputStream);
		NodeList topNode = document.getElementsByTagName("rdml");
		//
		JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		return (Rdml)unmarshaller.unmarshal(topNode.item(0));
	}

	private void setDate(IVendorPlate vendorPlate, Rdml rdml) {

		XMLGregorianCalendar dateMade = rdml.getDateMade();
		if(dateMade != null) {
			vendorPlate.setDate(dateMade.toGregorianCalendar().getTime());
		}
	}

	private void readExperiment(IVendorPlate vendorPlate, Rdml rdml) throws InvalidParameterException {

		for(ExperimentType experiment : rdml.getExperiment()) {
			vendorPlate.setName(experiment.getId());
			vendorPlate.setDetailedInfo(experiment.getDescription());
			for(RunType run : experiment.getRun()) {
				PcrFormatType pcrFormatType = run.getPcrFormat();
				int w = 0;
				for(ReactType react : run.getReact()) {
					IdReferencesType sample = react.getSample();
					IWell well = new Well();
					well.setPosition(calculateCoordinate(w + 1, pcrFormatType));
					well.putHeaderData(IWell.SAMPLE_ID, sample.getId());
					Channel channel = createChannel(rdml);
					for(DataType data : react.getData()) {
						for(DpAmpCurveType adp : data.getAdp()) {
							channel.getFluorescence().add((double)adp.getFluor());
						}
					}
					well.getChannels().put(0, channel);
					well.putHeaderData(IWell.SAMPLE_SUBSET, "Default");
					vendorPlate.getWells().add(well);
					w++;
				}
			}
		}
	}

	private Channel createChannel(Rdml rdml) {

		Channel channel = new Channel();
		channel.setId(0);
		for(DyeType dye : rdml.getDye()) {
			channel.setDetectionName(dye.getId());
		}
		channel.setValid(true);
		return channel;
	}

	private void setDefaultDetectionFormat(IVendorPlate vendorPlate) {

		DetectionFormat detectionFormat = new DetectionFormat();
		IChannelSpecification channelSpecification = new ChannelSpecification();
		channelSpecification.putHeaderData(IChannelSpecification.NAME, "Default");
		detectionFormat.getChannelSpecifications().add(channelSpecification);
		vendorPlate.setDetectionFormat(detectionFormat);
	}

	private static Position calculateCoordinate(int id, PcrFormatType pcrFormatType) {

		id--; // adjust for 0-based indexing
		int row = id % pcrFormatType.getRows();
		int column = id / pcrFormatType.getRows();
		char rowLetter = (char)('A' + row);
		int columnNumber = column + 1;
		Position position = new Position(String.valueOf(rowLetter), columnNumber);
		position.setId(id);
		return position;
	}
}