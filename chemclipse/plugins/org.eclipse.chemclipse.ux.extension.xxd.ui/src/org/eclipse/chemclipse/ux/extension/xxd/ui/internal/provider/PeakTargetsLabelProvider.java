/*******************************************************************************
 * Copyright (c) 2016, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This
 * program and the accompanying materials are made available under the terms of
 * the Eclipse Public License v1.0 which accompanies this distribution, and is
 * available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import java.text.DecimalFormat;

import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.model.identifier.IRatingSupplier;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class PeakTargetsLabelProvider extends LabelProvider implements ITableLabelProvider {

	private DecimalFormat decimalFormat;

	public PeakTargetsLabelProvider() {

		decimalFormat = ValueFormat.getDecimalFormatEnglish("0.000");
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {

		if(columnIndex == 0) {
			/*
			 * Rating
			 */
			if(element instanceof IIdentificationTarget identificationTarget) {
				IComparisonResult comparisonResult = identificationTarget.getComparisonResult();
				IRatingSupplier ratingSupplier = comparisonResult.getRatingSupplier();
				//
				String fileName;
				switch(ratingSupplier.getStatus()) {
					case VERY_GOOD:
						fileName = IApplicationImage.IMAGE_RATING_VERY_GOOD;
						break;
					case GOOD:
						fileName = IApplicationImage.IMAGE_RATING_GOOD;
						break;
					case AVERAGE:
						fileName = IApplicationImage.IMAGE_RATING_AVERAGE;
						break;
					case BAD:
						fileName = IApplicationImage.IMAGE_RATING_BAD;
						break;
					case VERY_BAD:
						fileName = IApplicationImage.IMAGE_RATING_VERY_BAD;
						break;
					default:
						fileName = "";
						break;
				}
				//
				if(!fileName.isEmpty()) {
					return ApplicationImageFactory.getInstance().getImage(fileName, IApplicationImage.SIZE_16x16);
				}
			}
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {

		String text = "";
		if(element instanceof IIdentificationTarget identificationEntry) {
			ILibraryInformation libraryInformation = identificationEntry.getLibraryInformation();
			IComparisonResult comparisonResult = identificationEntry.getComparisonResult();
			switch(columnIndex) {
				case 0: // Name
					text = libraryInformation.getName();
					break;
				case 1: // MQ
					text = decimalFormat.format(comparisonResult.getMatchFactor());
					break;
				case 2: // RMQ
					text = decimalFormat.format(comparisonResult.getReverseMatchFactor());
					break;
				case 3: // MQD
					text = decimalFormat.format(comparisonResult.getMatchFactorDirect());
					break;
				case 4: // RMQD
					text = decimalFormat.format(comparisonResult.getReverseMatchFactorDirect());
					break;
				default:
					text = "n.v.";
			}
		}
		return text;
	}
}