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
package org.eclipse.chemclipse.pcr.converter.supplier.rdes.io;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import org.eclipse.chemclipse.pcr.model.core.IChannel;
import org.eclipse.chemclipse.pcr.model.core.IPlate;
import org.eclipse.chemclipse.pcr.model.core.IWell;
import org.eclipse.chemclipse.pcr.model.core.SampleType;
import org.eclipse.chemclipse.pcr.model.core.TargetType;

public class PCRWriter {

	private static final String DELIMITER = "\t";

	public void writePlate(File file, IPlate plate) throws IOException {

		try (PrintWriter printWriter = new PrintWriter(file)) {
			writeHeader(printWriter, plate);
			writeWells(printWriter, plate);
		}
	}

	private void writeHeader(PrintWriter printWriter, IPlate plate) {

		printWriter.print("Well");
		printWriter.print(DELIMITER);
		printWriter.print("Sample");
		printWriter.print(DELIMITER);
		printWriter.print("Sample Type");
		printWriter.print(DELIMITER);
		printWriter.print("Target");
		printWriter.print(DELIMITER);
		printWriter.print("Target Type");
		printWriter.print(DELIMITER);
		printWriter.print("Dye");
		printWriter.print(DELIMITER);
		printWriter.print("Cq");
		printWriter.print(DELIMITER);
		int cycles = getCycles(plate);
		for(int cycle = 1; cycle < cycles + 1; cycle++) {
			printWriter.print(cycle);
			printWriter.print(DELIMITER);
		}
		printWriter.println();
	}

	private int getCycles(IPlate plate) {

		int cycles = 0;
		for(IWell well : plate.getWells()) {
			IChannel channel = well.getChannels().get(plate.getActiveChannel());
			int dataPoints = Math.max(channel.getFluorescence().size(), channel.getColorCompensatedFluorescence().size());
			if(dataPoints > cycles) {
				cycles = dataPoints;
			}
		}
		return cycles;
	}

	private void writeWells(PrintWriter printWriter, IPlate plate) {

		for(IWell well : plate.getWells()) {
			printWriter.print(well.getPosition().toString());
			printWriter.print(DELIMITER);
			printWriter.print(well.getSampleName());
			printWriter.print(DELIMITER);
			printWriter.print(writeSampleType(well.getSampleType()));
			printWriter.print(DELIMITER);
			if(!well.getTargetName().isEmpty()) {
				printWriter.print(well.getTargetName());
			} else {
				printWriter.print(well.getSampleSubset());
			}
			printWriter.print(DELIMITER);
			printWriter.print(writeTargetType(well.getTargetType()));
			printWriter.print(DELIMITER);
			printWriter.print(plate.getDetectionFormat().getChannelSpecifications().get(plate.getActiveChannel()).getName());
			printWriter.print(DELIMITER);
			IChannel channel = well.getChannels().get(plate.getActiveChannel());
			printWriter.print(channel.getCrossingPoint());
			printWriter.print(DELIMITER);
			if(!channel.getColorCompensatedFluorescence().isEmpty()) {
				for(double colorCompensatedFluorescence : channel.getColorCompensatedFluorescence()) {
					printWriter.print(colorCompensatedFluorescence);
					printWriter.print(DELIMITER);
				}
			} else {
				for(double fluorescence : channel.getFluorescence()) {
					printWriter.print(fluorescence);
					printWriter.print(DELIMITER);
				}
			}
			printWriter.println();
		}
	}

	private String writeSampleType(SampleType sampleType) {

		switch(sampleType) {
			case UNKNOWN: {
				return "unkn";
			}
			case NON_TEMPLATE_CONTROL: {
				return "ntc";
			}
			case NO_AMPLICATION_CONTROL: {
				return "nac";
			}
			case STANDARD_SAMPLE: {
				return "std";
			}
			case NO_TARGET_PRESENT: {
				return "ntp";
			}
			case MINUS_RT: {
				return "nrt";
			}
			case POSITIVE_CONTROL: {
				return "pos";
			}
			case OPTICAL_CALIBRATOR: {
				return "opt";
			}
			default:
				return "unk";
		}
	}

	private String writeTargetType(TargetType targetType) {

		switch(targetType) {
			case TARGET_OF_INTEREST: {
				return "toi";
			}
			case REFERENCE_TARGET: {
				return "ref";
			}
			default:
				return "ref";
		}
	}
}
