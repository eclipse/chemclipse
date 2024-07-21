/*******************************************************************************
 * Copyright (c) 2019, 2024 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import java.text.DecimalFormat;

import org.eclipse.chemclipse.model.ranges.TimeRange;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.l10n.ExtensionMessages;
import org.eclipse.swt.graphics.Image;

public class TimeRangesLabelProvider extends AbstractChemClipseLabelProvider {

	public static final String IDENTIFIER = ExtensionMessages.identifier;
	public static final String START = ExtensionMessages.startTimeMin;
	public static final String CENTER = ExtensionMessages.centerTimeMin;
	public static final String STOP = ExtensionMessages.stopTimeMin;
	//
	private DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish("0.000");
	//
	public static final String[] TITLES = { //
			IDENTIFIER, //
			START, //
			CENTER, //
			STOP //
	};
	public static final int[] BOUNDS = { //
			200, //
			130, //
			130, //
			130 //
	};

	@Override
	public Image getColumnImage(Object element, int columnIndex) {

		if(columnIndex == 0) {
			return getImage(element);
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {

		String text = "";
		if(element instanceof TimeRange timeRange) {
			switch(columnIndex) {
				case 0:
					text = timeRange.getIdentifier();
					break;
				case 1:
					text = calculateRetentionTimeMinutes(timeRange.getStart());
					break;
				case 2:
					text = calculateRetentionTimeMinutes(timeRange.getCenter());
					break;
				case 3:
					text = calculateRetentionTimeMinutes(timeRange.getStop());
					break;
			}
		}
		return text;
	}

	private String calculateRetentionTimeMinutes(int retentionTime) {

		return decimalFormat.format(retentionTime / TimeRange.MINUTE_FACTOR);
	}

	@Override
	public Image getImage(Object element) {

		return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_TARGETS, IApplicationImageProvider.SIZE_16x16);
	}
}