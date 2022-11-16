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
package org.eclipse.chemclipse.xxd.process.supplier.pca.io;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.List;

import org.eclipse.chemclipse.model.statistics.ISampleData;
import org.eclipse.chemclipse.model.statistics.IVariable;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.Feature;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.FeatureDataMatrix;

public class FeatureDataMatrixIO {

	public static final String DESCRIPTION = "Feature Data Matrix";
	public static final String FILE_EXTENSION = ".tsv";
	public static final String FILE_NAME = DESCRIPTION.replaceAll("\\s", "") + FILE_EXTENSION;
	public static final String FILTER_EXTENSION = "*" + FILE_EXTENSION;
	public static final String FILTER_NAME = DESCRIPTION + " (*" + FILE_EXTENSION + ")";
	//
	private static final String VALUE_DELIMITER = "\t";

	public static void write(File file, FeatureDataMatrix featureDataMatrix) throws FileNotFoundException {

		if(featureDataMatrix != null) {
			try (PrintWriter printWriter = new PrintWriter(file)) {
				/*
				 * Data
				 */
				List<String> sampleNames = featureDataMatrix.getSampleNames();
				Iterator<String> iteratorSampleNames = sampleNames.iterator();
				int samplesSize = sampleNames.size();
				DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish("0.0000");
				/*
				 * Header
				 */
				printWriter.print("Variable");
				printWriter.print(VALUE_DELIMITER);
				printWriter.print("Use");
				printWriter.print(VALUE_DELIMITER);
				printWriter.print("Classification");
				printWriter.print(VALUE_DELIMITER);
				printWriter.print("Description");
				while(iteratorSampleNames.hasNext()) {
					printWriter.print(VALUE_DELIMITER);
					printWriter.print(iteratorSampleNames.next());
				}
				printWriter.println();
				/*
				 * Data
				 */
				for(Feature feature : featureDataMatrix.getFeatures()) {
					IVariable variable = feature.getVariable();
					//
					printWriter.print(variable.getValue());
					printWriter.print(VALUE_DELIMITER);
					printWriter.print(variable.isSelected());
					printWriter.print(VALUE_DELIMITER);
					printWriter.print(variable.getClassification());
					printWriter.print(VALUE_DELIMITER);
					printWriter.print(variable.getDescription());
					//
					List<ISampleData<?>> sampleData = feature.getSampleData();
					for(int i = 0; i < samplesSize; i++) {
						double value = sampleData.get(i).getData();
						printWriter.print(VALUE_DELIMITER);
						printWriter.print(Double.isNaN(value) ? "NaN" : decimalFormat.format(value));
					}
					printWriter.println();
				}
			}
		}
	}
}