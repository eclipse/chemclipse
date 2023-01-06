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

import org.eclipse.chemclipse.model.core.IChromatogramOverview;
import org.eclipse.chemclipse.msd.model.core.IScanMSD;
import org.eclipse.chemclipse.msd.model.support.ScanSupport;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;
import org.eclipse.chemclipse.ux.extension.xxd.ui.messages.ExtensionMessages;
import org.eclipse.chemclipse.ux.extension.xxd.ui.messages.IExtensionMessages;
import org.eclipse.chemclipse.ux.extension.xxd.ui.preferences.PreferenceConstants;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Image;

public class ScanInfoLabelProvider extends AbstractChemClipseLabelProvider {

	public static final String LABEL_EMPTY = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.EMPTY_CAPS);
	public static final String LABEL_SIM = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.SIM);
	public static final String LABEL_SCAN = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.SCAN_CAPS);
	public static final String LABEL_DOTS = "...";
	public static final String LABEL_BLANK = "--";
	//
	public static final String SCAN = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.SCAN_HASH);
	public static final String RETENTION_TIME = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.TIME_MIN);
	public static final String COUNT_IONS = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.COUNT_IONS);
	public static final String CLASSIFICATION = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.SIM_SCAN);
	public static final String IONS = "m/z...";
	//
	private IPreferenceStore preferenceStore = Activator.getDefault().getPreferenceStore();
	private int limitSimTraces = preferenceStore.getInt(PreferenceConstants.P_LIMIT_SIM_TRACES);
	//
	public static final String[] TITLES = { //
			SCAN, //
			RETENTION_TIME, //
			COUNT_IONS, //
			CLASSIFICATION, //
			IONS //
	};
	//
	public static final int[] BOUNDS = { //
			120, //
			120, //
			120, //
			120, //
			120 //
	};

	public ScanInfoLabelProvider() {

		super("0.000");
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
		if(element instanceof IScanMSD scanMSD) {
			int numberOfIons = scanMSD.getNumberOfIons();
			switch(columnIndex) {
				case 0:
					text = Integer.toString(scanMSD.getScanNumber());
					break;
				case 1:
					text = decimalFormat.format(scanMSD.getRetentionTime() / IChromatogramOverview.MINUTE_CORRELATION_FACTOR);
					break;
				case 2:
					text = Integer.toString(numberOfIons);
					break;
				case 3:
					if(numberOfIons > 0) {
						text = (numberOfIons <= limitSimTraces) ? LABEL_SIM : LABEL_SCAN;
					} else {
						text = LABEL_EMPTY;
					}
					break;
				case 4:
					if(numberOfIons > 0) {
						text = (scanMSD.getNumberOfIons() <= limitSimTraces) ? ScanSupport.getSortedTraces(scanMSD) : LABEL_DOTS;
					} else {
						text = LABEL_BLANK;
					}
					break;
				default:
					text = "n.v.";
			}
		}
		return text;
	}

	@Override
	public Image getImage(Object element) {

		return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_MASS_SPECTRUM, IApplicationImageProvider.SIZE_16x16);
	}
}
