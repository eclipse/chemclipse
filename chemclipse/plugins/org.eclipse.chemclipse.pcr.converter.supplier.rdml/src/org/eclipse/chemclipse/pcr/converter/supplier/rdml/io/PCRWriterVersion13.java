/*******************************************************************************
 * Copyright (c) 2024 Lablicate GmbH.
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

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.pcr.converter.supplier.rdml.internal.v13.model.DataType;
import org.eclipse.chemclipse.pcr.converter.supplier.rdml.internal.v13.model.DpAmpCurveType;
import org.eclipse.chemclipse.pcr.converter.supplier.rdml.internal.v13.model.DyeType;
import org.eclipse.chemclipse.pcr.converter.supplier.rdml.internal.v13.model.ExperimentType;
import org.eclipse.chemclipse.pcr.converter.supplier.rdml.internal.v13.model.IdReferencesType;
import org.eclipse.chemclipse.pcr.converter.supplier.rdml.internal.v13.model.ObjectFactory;
import org.eclipse.chemclipse.pcr.converter.supplier.rdml.internal.v13.model.PcrFormatType;
import org.eclipse.chemclipse.pcr.converter.supplier.rdml.internal.v13.model.Rdml;
import org.eclipse.chemclipse.pcr.converter.supplier.rdml.internal.v13.model.ReactType;
import org.eclipse.chemclipse.pcr.converter.supplier.rdml.internal.v13.model.RunType;
import org.eclipse.chemclipse.pcr.converter.supplier.rdml.model.WellComparator;
import org.eclipse.chemclipse.pcr.model.core.IChannel;
import org.eclipse.chemclipse.pcr.model.core.IPlate;
import org.eclipse.chemclipse.pcr.model.core.IWell;
import org.eclipse.core.runtime.IProgressMonitor;

import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.JAXBException;
import jakarta.xml.bind.Marshaller;

public class PCRWriterVersion13 {

	private static final Logger logger = Logger.getLogger(PCRWriterVersion13.class);

	public void writePlate(File file, IPlate plate, IProgressMonitor monitor) {

		try {
			JAXBContext jaxbContext = JAXBContext.newInstance(ObjectFactory.class);
			Marshaller marshaller = jaxbContext.createMarshaller();
			Rdml rdml = new Rdml();
			rdml.setVersion("1.3");
			rdml.getExperiment().add(createExperiment(plate));
			setDate(plate, rdml);
			setDyes(plate.getWell(0).getChannels().values(), rdml);
			marshaller.marshal(rdml, file);
		} catch(JAXBException e) {
			logger.warn(e);
		}
	}

	private void setDate(IPlate plate, Rdml rdml) {

		Date date = plate.getDate();
		if(date != null) {
			GregorianCalendar calendar = new GregorianCalendar();
			calendar.setTime(date);
			try {
				rdml.setDateMade(DatatypeFactory.newInstance().newXMLGregorianCalendar(calendar));
			} catch(DatatypeConfigurationException e) {
				logger.warn(e);
			}
		}
	}

	private ExperimentType createExperiment(IPlate plate) {

		ExperimentType experiment = new ExperimentType();
		experiment.setId(plate.getName());
		experiment.setDescription(plate.getDetailedInfo());
		experiment.getRun().add(createRun(plate));
		return experiment;
	}

	private RunType createRun(IPlate plate) {

		RunType run = new RunType();
		run.setInstrument(plate.getInstrument());
		run.setPcrFormat(createPcrFormat());
		Set<IWell> sortedWells = new TreeSet<>(new WellComparator());
		sortedWells.addAll(plate.getWells());
		for(IWell well : sortedWells) {
			ReactType react = new ReactType();
			IdReferencesType idReference = new IdReferencesType();
			idReference.setId(well.getSampleName());
			react.setSample(idReference);
			for(IChannel channel : well.getChannels().values()) {
				DataType data = new DataType();
				data.setCq((float)channel.getCrossingPoint());
				for(Double fluorescense : channel.getFluorescence()) {
					DpAmpCurveType adp = new DpAmpCurveType();
					adp.setFluor(fluorescense.floatValue());
					data.getAdp().add(adp);
				}
				react.getData().add(data);
			}
			run.getReact().add(react);
		}
		return run;
	}

	private PcrFormatType createPcrFormat() {

		PcrFormatType pcrFormatType = new PcrFormatType();
		pcrFormatType.setRows(8);
		pcrFormatType.setColumns(12);
		pcrFormatType.setRowLabel("ABC");
		pcrFormatType.setColumnLabel("123");
		return pcrFormatType;
	}

	private void setDyes(Collection<IChannel> channels, Rdml rdml) {

		for(IChannel channel : channels) {
			DyeType dye = new DyeType();
			dye.setId(channel.getDetectionName());
			rdml.getDye().add(dye);
		}
	}
}