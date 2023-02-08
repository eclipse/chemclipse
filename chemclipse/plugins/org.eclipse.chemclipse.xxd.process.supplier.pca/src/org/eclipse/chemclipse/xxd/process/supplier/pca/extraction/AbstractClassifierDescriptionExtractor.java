/*******************************************************************************
 * Copyright (c) 2020, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.xxd.process.supplier.pca.extraction;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.eclipse.chemclipse.model.comparator.IdentificationTargetComparator;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.core.IPeak;
import org.eclipse.chemclipse.model.core.IPeakModel;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.DescriptionOption;
import org.eclipse.chemclipse.xxd.process.supplier.pca.model.Samples;

public abstract class AbstractClassifierDescriptionExtractor {

	private static final String DELIMITER = ",";
	private DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish("0.000");

	protected void setClassifierAndDescription(Samples samples, DescriptionOption descriptionOption) {

		for(int i = 0; i < samples.getVariables().size(); i++) {
			final int j = i;
			final Set<String> descriptions = new HashSet<>();
			final Set<String> classifier = new HashSet<>();
			/*
			 * Fetch classifications and descriptions.
			 */
			samples.getSampleList().stream().map(s -> s.getSampleData()).map(d -> d.get(j).getPeak()).forEach(peak -> {
				if(peak.isPresent()) {
					/*
					 * Classifier
					 */
					classifier.addAll(peak.get().getClassifier());
					/*
					 * Descriptions
					 */
					IPeak peakX = peak.get();
					IPeakModel peakModel = peakX.getPeakModel();
					float retentionIndex = peakModel.getPeakMaximum().getRetentionIndex();
					IdentificationTargetComparator identificationTargetComparator = new IdentificationTargetComparator(retentionIndex);
					ILibraryInformation libraryInformation = IIdentificationTarget.getBestLibraryInformation(peak.get().getTargets(), identificationTargetComparator);
					//
					if(libraryInformation != null) {
						String value;
						switch(descriptionOption) {
							case RETENTION_TIME_MIN:
								value = decimalFormat.format(peakModel.getRetentionTimeAtPeakMaximum() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR);
								break;
							case RETENTION_INDEX:
								value = Integer.toString(Math.round(peakModel.getPeakMaximum().getRetentionIndex()));
								break;
							case NAME:
								value = libraryInformation.getName();
								break;
							case CAS:
								value = libraryInformation.getCasNumber();
								break;
							case NAME_CAS:
								StringBuilder builder = new StringBuilder();
								builder.append(libraryInformation.getName());
								builder.append(" ");
								builder.append("(");
								builder.append(libraryInformation.getCasNumber());
								builder.append(")");
								value = builder.toString();
								break;
							case REFERENCE_IDENTIFIER:
								value = libraryInformation.getReferenceIdentifier();
								break;
							default:
								value = null;
								break;
						}
						/*
						 * Check
						 */
						if(value != null && !value.isEmpty()) {
							descriptions.add(value);
						}
					}
				}
			});
			/*
			 * Classifier
			 */
			if(!classifier.isEmpty()) {
				StringBuilder stringBuilder = new StringBuilder();
				List<String> list = new ArrayList<>(classifier);
				Collections.sort(list);
				Iterator<String> iterator = list.iterator();
				while(iterator.hasNext()) {
					stringBuilder.append(iterator.next());
					if(iterator.hasNext()) {
						stringBuilder.append(DELIMITER);
						stringBuilder.append(" ");
					}
				}
				//
				samples.getVariables().get(i).setClassification(stringBuilder.toString());
			}
			/*
			 * Description -> Best Target
			 */
			if(!descriptions.isEmpty()) {
				StringBuilder stringBuilder = new StringBuilder();
				List<String> list = new ArrayList<>(descriptions);
				Collections.sort(list);
				Iterator<String> iterator = list.iterator();
				while(iterator.hasNext()) {
					stringBuilder.append(iterator.next());
					if(iterator.hasNext()) {
						stringBuilder.append(DELIMITER);
						stringBuilder.append(" ");
					}
				}
				//
				samples.getVariables().get(i).setDescription(stringBuilder.toString());
			}
		}
	}
}