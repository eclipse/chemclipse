/*******************************************************************************
 * Copyright (c) 2015 Lablicate UG (haftungsbeschränkt).
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.internal.provider;

import org.eclipse.chemclipse.msd.model.core.IIonTransition;
import org.eclipse.chemclipse.msd.model.core.support.IMarkedIonTransition;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class IonTransitionLabelProvider extends LabelProvider implements ITableLabelProvider {

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

		String text = "";
		if(element instanceof IMarkedIonTransition) {
			IMarkedIonTransition markedIonTransition = (IMarkedIonTransition)element;
			IIonTransition ionTransition = markedIonTransition.getIonTransition();
			switch(columnIndex) {
				case 0:
					text = ionTransition.getCompoundName();
					break;
				case 1:
					text = Integer.valueOf(ionTransition.getQ1Ion()).toString();
					break;
				case 2:
					text = Double.valueOf(ionTransition.getQ1Resolution()).toString();
					break;
				case 3:
					text = Double.valueOf(ionTransition.getQ3Ion()).toString();
					break;
				case 4:
					text = Double.valueOf(ionTransition.getQ3Resolution()).toString();
					break;
				case 5:
					text = Double.valueOf(ionTransition.getCollisionEnergy()).toString();
					break;
				case 6:
					text = Boolean.valueOf(markedIonTransition.isSelected()).toString();
					break;
				default:
					text = "n.v.";
			}
		}
		return text;
	}

	public Image getImage(Object element) {

		Image image = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_ION_TRANSITION, IApplicationImage.SIZE_16x16);
		return image;
	}
}
