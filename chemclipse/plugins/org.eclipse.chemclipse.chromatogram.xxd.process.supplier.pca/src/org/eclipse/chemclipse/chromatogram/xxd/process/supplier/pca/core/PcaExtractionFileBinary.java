/*******************************************************************************
 * Copyright (c) 2022 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.core;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.PeakSampleData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Sample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Samples;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.statistics.IRetentionIndex;
import org.eclipse.chemclipse.model.statistics.IRetentionTime;
import org.eclipse.chemclipse.model.statistics.IVariable;
import org.eclipse.chemclipse.model.statistics.RetentionIndex;
import org.eclipse.chemclipse.model.statistics.RetentionTime;
import org.eclipse.core.runtime.IProgressMonitor;

public class PcaExtractionFileBinary implements IExtractionData {

	private static final Logger logger = Logger.getLogger(PcaExtractionFileBinary.class);
	//
	public static final String DESCRIPTION = "PCA Data Binary";
	public static final String FILE_EXTENSION = ".pdb";
	public static final String FILE_NAME = DESCRIPTION.replaceAll("\\s", "") + FILE_EXTENSION;
	public static final String FILTER_EXTENSION = "*" + FILE_EXTENSION;
	public static final String FILTER_NAME = DESCRIPTION + " (*" + FILE_EXTENSION + ")";
	//
	private File file;

	public PcaExtractionFileBinary(File file) {

		this.file = file;
	}

	@Override
	public Samples process(IProgressMonitor monitor) {

		return extract();
	}

	public static void write(File file, Samples samples) {

		try (DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(file))) {
			/*
			 * Samples
			 */
			List<Sample> sampleList = samples.getSampleList();
			int sizeSamples = sampleList.size();
			dataOutputStream.writeInt(sizeSamples);
			for(Sample sample : sampleList) {
				dataOutputStream.writeUTF(sample.getSampleName());
				dataOutputStream.writeUTF(sample.getRGB());
				dataOutputStream.writeUTF(sample.getGroupName());
				dataOutputStream.writeUTF(sample.getClassification());
				dataOutputStream.writeUTF(sample.getDescription());
				List<PeakSampleData> peakSampleDataList = sample.getSampleData();
				int sizeSampleData = peakSampleDataList.size();
				dataOutputStream.writeInt(sizeSampleData);
				for(PeakSampleData peakSampleData : peakSampleDataList) {
					dataOutputStream.writeDouble(peakSampleData.getData());
				}
			}
			/*
			 * Variables
			 */
			List<IVariable> variables = samples.getVariables();
			int sizeVariables = variables.size();
			dataOutputStream.writeInt(sizeVariables);
			for(IVariable variable : variables) {
				dataOutputStream.writeUTF(variable.getType());
				dataOutputStream.writeUTF(variable.getValue());
				dataOutputStream.writeBoolean(variable.isSelected());
				dataOutputStream.writeUTF(variable.getClassification());
				dataOutputStream.writeUTF(variable.getDescription());
			}
			dataOutputStream.flush();
		} catch(Exception e) {
			logger.warn(e);
		}
	}

	private Samples extract() {

		Samples samples = null;
		//
		try (DataInputStream dataInputStream = new DataInputStream(new FileInputStream(file))) {
			/*
			 * Samples
			 */
			List<Sample> sampleList = new ArrayList<>();
			int sizeSamples = dataInputStream.readInt();
			for(int i = 0; i < sizeSamples; i++) {
				String sampleName = dataInputStream.readUTF();
				String rgb = dataInputStream.readUTF();
				String groupName = dataInputStream.readUTF();
				String classification = dataInputStream.readUTF();
				String description = dataInputStream.readUTF();
				Sample sample = new Sample(sampleName, groupName, classification, description);
				sample.setRGB(rgb);
				int sizeSampleData = dataInputStream.readInt();
				for(int j = 0; j < sizeSampleData; j++) {
					double data = dataInputStream.readDouble();
					sample.getSampleData().add(new PeakSampleData(data, null));
				}
				sampleList.add(sample);
			}
			samples = new Samples(sampleList);
			/*
			 * Variables
			 */
			int sizeVariables = dataInputStream.readInt();
			for(int i = 0; i < sizeVariables; i++) {
				try {
					/*
					 * Data
					 */
					String type = dataInputStream.readUTF();
					String value = dataInputStream.readUTF();
					boolean isSelected = dataInputStream.readBoolean();
					String classification = dataInputStream.readUTF();
					String descripttion = dataInputStream.readUTF();
					//
					IVariable variable = null;
					if(type.equals(IRetentionTime.TYPE)) {
						variable = new RetentionTime(Integer.parseInt(value));
					} else if(type.equals(IRetentionIndex.TYPE)) {
						variable = new RetentionIndex(Integer.parseInt(value));
					}
					//
					if(variable != null) {
						variable.setSelected(isSelected);
						variable.setClassification(classification);
						variable.setDescription(descripttion);
						samples.getVariables().add(variable);
					}
				} catch(NumberFormatException e) {
					logger.warn(e);
				}
			}
		} catch(Exception e) {
			logger.warn(e);
		}
		//
		return samples;
	}
}