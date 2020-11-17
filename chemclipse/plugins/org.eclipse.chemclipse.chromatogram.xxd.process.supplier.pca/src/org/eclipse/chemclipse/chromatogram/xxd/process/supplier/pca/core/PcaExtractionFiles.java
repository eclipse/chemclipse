/*******************************************************************************
 * Copyright (c) 2020 Lablicate GmbH.
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.IDataInputEntry;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.PeakSampleData;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Sample;
import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model.Samples;
import org.eclipse.chemclipse.logging.core.Logger;
import org.eclipse.chemclipse.model.statistics.IVariable;
import org.eclipse.chemclipse.model.statistics.Target;
import org.eclipse.core.runtime.IProgressMonitor;

public class PcaExtractionFiles implements IExtractionData {

	private static final Logger logger = Logger.getLogger(PcaExtractionFiles.class);
	//
	private static final String DELIMITER = "\t";
	private final List<IDataInputEntry> dataInputEntries;

	public PcaExtractionFiles(List<IDataInputEntry> dataInputEntries) {

		this.dataInputEntries = dataInputEntries;
	}

	public static void getDemoContent(PrintWriter printWriter) {

		int start = 65; // ASCII -> A
		int variables = 10;
		/*
		 * Header
		 */
		printWriter.print("Sample");
		printWriter.print(DELIMITER);
		printWriter.print("Group Name");
		printWriter.print(DELIMITER);
		printWriter.print("Classification");
		printWriter.print(DELIMITER);
		printWriter.print("Description");
		printWriter.print(DELIMITER);
		for(int i = 0; i < variables; i++) {
			printWriter.print((char)(i + start));
			printWriter.print(DELIMITER);
		}
		printWriter.println();
		/*
		 * Data
		 */
		for(int j = 0; j < 20; j++) {
			//
			int sampleNumber = j + 1;
			double type = Math.random();
			//
			printWriter.print("Sample ");
			if(sampleNumber < 10) {
				printWriter.print("0");
			}
			printWriter.print(sampleNumber);
			printWriter.print(DELIMITER);
			printWriter.print(type >= 0.5 ? "O" : "U");
			printWriter.print(DELIMITER);
			printWriter.print(type >= 0.5 ? "+" : "-");
			printWriter.print(DELIMITER);
			printWriter.print(type >= 0.5 ? "Over Center" : "Under Center");
			printWriter.print(DELIMITER);
			for(int i = 0; i < variables; i++) {
				printWriter.print(Math.random());
				printWriter.print(DELIMITER);
			}
			printWriter.println();
		}
	}

	@Override
	public Samples process(IProgressMonitor monitor) {

		return extract();
	}

	private Samples extract() {

		Map<String, Sample> sampleMap = new HashMap<>();
		Map<String, Map<String, Target>> samplesVariablesMap = new HashMap<>();
		//
		for(IDataInputEntry dataInputEntry : dataInputEntries) {
			String inputFile = dataInputEntry.getInputFile();
			File file = new File(inputFile);
			if(file.exists()) {
				try (FileReader reader = new FileReader(file)) {
					/*
					 * Data
					 */
					CSVParser parser = new CSVParser(reader, CSVFormat.TDF.withHeader());
					Map<Integer, String> indexMap = extractIndexMap(parser);
					for(CSVRecord record : parser.getRecords()) {
						/*
						 * Sample
						 * GroupName
						 * Classification
						 * Description
						 * Variables...
						 */
						int size = record.size();
						if(size > 4) {
							/*
							 * Header
							 */
							String name = record.get(0).trim();
							String groupName = record.get(1).trim();
							String classification = record.get(2).trim();
							String description = record.get(3).trim();
							//
							if(!name.isEmpty()) {
								/*
								 * Sample
								 */
								Sample sample = sampleMap.get(name);
								if(sample == null) {
									sample = new Sample(name, groupName, classification, description);
									sampleMap.put(name, sample);
								}
								/*
								 * Variable(s)
								 */
								Map<String, Target> variablesMap = samplesVariablesMap.get(name);
								if(variablesMap == null) {
									variablesMap = new HashMap<>();
									samplesVariablesMap.put(name, variablesMap);
								}
								//
								for(int i = 4; i < size; i++) {
									try {
										String targetName = indexMap.getOrDefault(i, "");
										if(!targetName.isEmpty()) {
											double value = Double.parseDouble(record.get(i).trim());
											Target target = new Target(targetName);
											target.setValue(Double.toString(value));
											variablesMap.put(targetName, target);
										}
									} catch(NumberFormatException e) {
										// No Number
									}
								}
							}
						}
					}
					parser.close();
				} catch(FileNotFoundException e) {
					logger.warn(e);
				} catch(IOException e) {
					logger.warn(e);
				}
			}
		}
		/*
		 * Samples / Variables
		 */
		List<Sample> sampleList = new ArrayList<>(sampleMap.values());
		Collections.sort(sampleList, (s1, s2) -> s1.getName().compareTo(s2.getName()));
		Samples samples = new Samples(sampleList);
		List<? extends IVariable> variables = extractVariables(samplesVariablesMap);
		samples.getVariables().addAll(variables);
		setExtractData(samplesVariablesMap, samples);
		//
		return samples;
	}

	/**
	 * Map - sample id, Map
	 * Map - variable id, IVariable
	 * 
	 * @param samplesVariablesMap
	 * @return
	 */
	private List<? extends IVariable> extractVariables(Map<String, Map<String, Target>> samplesVariablesMap) {

		Map<String, Target> targets = new HashMap<>();
		/*
		 * Map the variables
		 */
		for(Map<String, Target> variableMap : samplesVariablesMap.values()) {
			for(Target mappedVariable : variableMap.values()) {
				//
				String key = mappedVariable.getTarget();
				Target target = targets.get(key);
				if(target == null) {
					target = new Target(key);
					targets.put(key, target);
				}
			}
		}
		//
		List<? extends IVariable> variables = new ArrayList<>(targets.values());
		Collections.sort(variables, (v1, v2) -> v1.compareTo(v2));
		return variables;
	}

	private void setExtractData(Map<String, Map<String, Target>> samplesVariablesMap, Samples samples) {

		List<IVariable> variables = samples.getVariables();
		//
		for(Sample sample : samples.getSampleList()) {
			Iterator<IVariable> iterator = variables.iterator();
			Map<String, Target> extractPeak = samplesVariablesMap.get(sample.getName());
			while(iterator.hasNext()) {
				String variable = iterator.next().getValue();
				Target target = extractPeak.get(variable);
				//
				boolean addEmpty = true;
				if(target != null) {
					try {
						double value = Double.parseDouble(target.getValue());
						PeakSampleData sampleData = new PeakSampleData(value, null);
						sample.getSampleData().add(sampleData);
						addEmpty = false;
					} catch(NumberFormatException e) {
						logger.warn(e);
					}
				}
				//
				if(addEmpty) {
					PeakSampleData sampleData = new PeakSampleData();
					sample.getSampleData().add(sampleData);
				}
			}
		}
	}

	private Map<Integer, String> extractIndexMap(CSVParser parser) {

		Map<Integer, String> indexMap = new HashMap<>();
		Map<String, Integer> headerMap = parser.getHeaderMap();
		//
		for(Map.Entry<String, Integer> entry : headerMap.entrySet()) {
			indexMap.put(entry.getValue(), entry.getKey());
		}
		//
		return indexMap;
	}
}
