/*******************************************************************************
 * Copyright (c) 2016, 2018 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.eclipse.chemclipse.model.core.AbstractChromatogram;
import org.eclipse.chemclipse.msd.model.core.IIon;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.swt.graphics.Image;

public class ChromatogramScanInfoLabelProvider extends AbstractChemClipseLabelProvider {

	private static final int MAX_SIM_IONS = 10;
	private StringBuilder builder;

	public ChromatogramScanInfoLabelProvider() {
		super("0.000");
		builder = new StringBuilder();
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

		DecimalFormat decimalFormat = getDecimalFormat();
		String text = "";
		if(element instanceof IScanMSD) {
			IScanMSD scanMSD = (IScanMSD)element;
			switch(columnIndex) {
				case 0:
					text = Integer.toString(scanMSD.getScanNumber());
					break;
				case 1:
					text = decimalFormat.format(scanMSD.getRetentionTime() / AbstractChromatogram.MINUTE_CORRELATION_FACTOR);
					break;
				case 2:
					text = Integer.toString(scanMSD.getNumberOfIons());
					break;
				case 3:
					text = (scanMSD.getNumberOfIons() <= MAX_SIM_IONS) ? "SIM" : "SCAN";
					break;
				case 4:
					text = (scanMSD.getNumberOfIons() <= MAX_SIM_IONS) ? getIons(scanMSD) : "";
					break;
				default:
					text = "n.v.";
			}
		}
		return text;
	}

	private String getIons(IScanMSD scanMSD) {

		/*
		 * Clear the builder.
		 */
		builder.delete(0, builder.length());
		/*
		 * Get the ions.
		 */
		List<Integer> ions = new ArrayList<Integer>();
		for(IIon ion : scanMSD.getIons()) {
			ions.add((int)Math.round(ion.getIon()));
		}
		/*
		 * Get the list.
		 */
		Collections.sort(ions);
		Iterator<Integer> iterator = ions.iterator();
		while(iterator.hasNext()) {
			builder.append(Integer.toString(iterator.next()));
			if(iterator.hasNext()) {
				builder.append(" ");
			}
		}
		return builder.toString();
	}

	public Image getImage(Object element) {

		return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_MASS_SPECTRUM, IApplicationImage.SIZE_16x16);
	}
}
