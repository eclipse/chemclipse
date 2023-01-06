/*******************************************************************************
 * Copyright (c) 2016, 2023 Lablicate GmbH.
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
import java.util.Map;

import org.eclipse.chemclipse.model.columns.IRetentionIndexEntry;
import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.model.preferences.PreferenceSupplier;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.text.ValueFormat;
import org.eclipse.chemclipse.ux.extension.xxd.ui.messages.ExtensionMessages;
import org.eclipse.chemclipse.ux.extension.xxd.ui.messages.IExtensionMessages;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class RetentionIndexLabelProvider extends LabelProvider implements ITableLabelProvider {

	public static final String RETENTION_TIME = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.RETENTION_TIME_MINUTES);
	public static final String RETENTION_INDEX = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.RETENTION_INDEX);
	public static final String NAME = "Name";
	//
	public static final String[] TITLES = { //
			RETENTION_TIME, //
			RETENTION_INDEX, //
			NAME //
	};
	//
	public static final int[] BOUNDS = { //
			200, //
			150, //
			200 //
	};
	//
	private DecimalFormat decimalFormat = ValueFormat.getDecimalFormatEnglish("0.0000");

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
		/*
		 * Retention Time
		 * Map<Integer, IRetentionIndexEntry>
		 */
		if(element instanceof Map.Entry<?, ?> entry) {
			element = entry.getValue();
		}
		//
		if(element instanceof IRetentionIndexEntry retentionIndexEntry) {
			switch(columnIndex) {
				case 0:
					text = decimalFormat.format(retentionIndexEntry.getRetentionTime() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR);
					break;
				case 1:
					if(PreferenceSupplier.showRetentionIndexWithoutDecimals()) {
						text = Integer.toString((int)retentionIndexEntry.getRetentionIndex());
					} else {
						text = decimalFormat.format(retentionIndexEntry.getRetentionIndex());
					}
					break;
				case 2:
					text = retentionIndexEntry.getName();
					break;
				default:
					text = "n.v.";
			}
		}
		return text;
	}

	@Override
	public Image getImage(Object element) {

		return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_PEAK, IApplicationImageProvider.SIZE_16x16);
	}
}
