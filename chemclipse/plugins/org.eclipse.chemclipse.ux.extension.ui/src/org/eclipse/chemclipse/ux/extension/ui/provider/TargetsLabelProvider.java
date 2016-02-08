/*******************************************************************************
 * Copyright (c) 2012, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.provider;

import java.text.DecimalFormat;

import org.eclipse.chemclipse.model.identifier.IComparisonResult;
import org.eclipse.chemclipse.model.identifier.IIdentificationTarget;
import org.eclipse.chemclipse.model.identifier.ILibraryInformation;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.swt.graphics.Image;

public class TargetsLabelProvider extends AbstractChemClipseLabelProvider {

	@Override
	public Image getColumnImage(Object element, int columnIndex) {

		if(columnIndex == 0) {
			if(element instanceof IIdentificationTarget) {
				IIdentificationTarget identificationTarget = (IIdentificationTarget)element;
				if(identificationTarget.isManuallyVerified()) {
					return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_SELECTED, IApplicationImage.SIZE_16x16);
				} else {
					return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_DESELECTED, IApplicationImage.SIZE_16x16);
				}
			}
		} else if(columnIndex == 1) {
			return getImage(element);
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {

		/*
		 * SYNCHRONIZE: PeakListLabelProvider PeakListLabelComparator PeakListView
		 */
		DecimalFormat decimalFormat = getDecimalFormat();
		String text = "";
		if(element instanceof IIdentificationTarget) {
			IIdentificationTarget identificationEntry = (IIdentificationTarget)element;
			ILibraryInformation libraryInformation = identificationEntry.getLibraryInformation();
			IComparisonResult comparisonResult = identificationEntry.getComparisonResult();
			switch(columnIndex) {
				case 0:
					text = "";
					break;
				case 1: // Name
					text = libraryInformation.getName();
					break;
				case 2: // CAS
					text = libraryInformation.getCasNumber();
					break;
				case 3: // MQ
					text = decimalFormat.format(comparisonResult.getMatchFactor());
					break;
				case 4: // RMQ
					text = decimalFormat.format(comparisonResult.getReverseMatchFactor());
					break;
				case 5: // Formula
					text = libraryInformation.getFormula();
					break;
				case 6: // Mol Weight
					text = decimalFormat.format(libraryInformation.getMolWeight());
					break;
				case 7: // Probability
					text = decimalFormat.format(comparisonResult.getProbability());
					break;
				case 8: // Advise
					text = comparisonResult.getAdvise();
					break;
				case 9: // Identifier
					text = identificationEntry.getIdentifier();
					break;
				case 10: // Miscellaneous
					text = libraryInformation.getMiscellaneous();
					break;
				case 11: // Comments
					text = libraryInformation.getComments();
					break;
				default:
					text = "n.v.";
			}
		}
		return text;
	}

	@Override
	public Image getImage(Object element) {

		Image image = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_TARGETS, IApplicationImage.SIZE_16x16);
		return image;
	}
}
