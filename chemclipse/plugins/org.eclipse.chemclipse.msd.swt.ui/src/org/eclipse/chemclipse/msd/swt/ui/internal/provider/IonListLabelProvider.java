/*******************************************************************************
 * Copyright (c) 2012, 2018 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.msd.swt.ui.internal.provider;

import java.text.DecimalFormat;

import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IIonTransition;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.swt.graphics.Image;

public class IonListLabelProvider extends AbstractChemClipseLabelProvider {

	public IonListLabelProvider() {
		super("0.0###");
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {

		if(columnIndex == 0) {
			return getImage(element);
		} else {
			return null;
		}
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {

		/*
		 * SYNCHRONIZE: IonListLabelProvider IonListLabelComparator MassSpectrumIonListView
		 */
		DecimalFormat decimalFormat = getDecimalFormat();
		String text = "";
		if(element instanceof IIon) {
			IIon ion = (IIon)element;
			IIonTransition ionTransition = ion.getIonTransition();
			switch(columnIndex) {
				case 0: // m/z (normal 28.3 or with Transition 128 > 78.4)
					String mz = decimalFormat.format(ion.getIon());
					text = (ionTransition == null) ? mz : Integer.toString((int)ionTransition.getQ1StartIon()) + " > " + mz;
					break;
				case 1: // abundance
					text = decimalFormat.format(ion.getAbundance());
					break;
				case 2: // parent m/z
					text = (ionTransition == null) ? "" : decimalFormat.format(ionTransition.getQ1Ion());
					break;
				case 3: // parent resolution
					text = (ionTransition == null) ? "" : decimalFormat.format(ionTransition.getQ1Resolution());
					break;
				case 4: // daughter m/z
					text = (ionTransition == null) ? "" : decimalFormat.format(ionTransition.getQ3Ion());
					break;
				case 5: // daughter resolution
					text = (ionTransition == null) ? "" : decimalFormat.format(ionTransition.getQ3Resolution());
					break;
				case 6: // collision energy
					text = (ionTransition == null) ? "" : decimalFormat.format(ionTransition.getCollisionEnergy());
					break;
				default:
					text = "n.v.";
			}
		}
		return text;
	}

	public Image getImage(Object element) {

		return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ION, IApplicationImage.SIZE_16x16);
	}
}
