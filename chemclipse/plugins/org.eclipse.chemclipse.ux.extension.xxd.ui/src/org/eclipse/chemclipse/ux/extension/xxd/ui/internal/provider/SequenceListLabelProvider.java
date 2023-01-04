/*******************************************************************************
 * Copyright (c) 2018, 2023 Lablicate GmbH.
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

import org.eclipse.chemclipse.converter.model.reports.ISequenceRecord;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.messages.ExtensionMessages;
import org.eclipse.chemclipse.ux.extension.xxd.ui.messages.IExtensionMessages;
import org.eclipse.swt.graphics.Image;

public class SequenceListLabelProvider extends AbstractChemClipseLabelProvider {

	public static final String SAMPLE_NAME = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.SAMPLE_NAME);
	public static final String DATA_PATH = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.DATA_PATH);
	public static final String DATA_FILE = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.DATA_FILE);
	public static final String ADVICE = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.ADVICE);
	public static final String VIAL = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.VIAL);
	public static final String SUBSTANCE = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.SUBSTANCE);
	public static final String DESCRIPTION = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.DESCRIPTION);
	public static final String PROCESS_METHOD = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.PROCESS_METHOD);
	public static final String MULTIPLIER = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.MULTIPLIER);
	public static final String INJECTION_VOLUME = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.INJECTION_VOLUME);
	public static final String REPORT_METHOD = ExtensionMessages.INSTANCE().getMessage(IExtensionMessages.REPORT_METHOD);
	//
	public static String[] TITLES = {//
			SAMPLE_NAME, //
			DATA_PATH, //
			DATA_FILE, //
			ADVICE, //
			VIAL, //
			SUBSTANCE, //
			DESCRIPTION, //
			PROCESS_METHOD, //
			REPORT_METHOD, //
			MULTIPLIER, //
			INJECTION_VOLUME //
	};
	//
	public static int[] BOUNDS = {//
			200, //
			150, //
			150, //
			200, //
			60, //
			150, //
			150, //
			150, //
			150, //
			60, //
			60 //
	};

	@Override
	public Image getColumnImage(Object element, int columnIndex) {

		if(columnIndex == 0) {
			return getImage(element);
		} else if(columnIndex == 3) {
			if(element instanceof ISequenceRecord sequenceRecord) {
				switch(sequenceRecord.getAdvice()) {
					case NONE:
						return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_STATUS_EMPTY, IApplicationImage.SIZE_16x16);
					case DATA_IS_VALID:
						return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_STATUS_OK, IApplicationImage.SIZE_16x16);
					case FILE_NOT_AVAILABLE:
						return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_STATUS_ERROR, IApplicationImage.SIZE_16x16);
				}
			}
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {

		DecimalFormat decimalFormat = getDecimalFormat();
		String text = "";
		if(element instanceof ISequenceRecord sequenceRecord) {
			//
			switch(columnIndex) {
				case 0:
					text = sequenceRecord.getSampleName();
					break;
				case 1:
					text = sequenceRecord.getDataPath();
					break;
				case 2:
					text = sequenceRecord.getDataFile();
					break;
				case 3:
					text = sequenceRecord.getAdvice().getDecsription();
					break;
				case 4:
					text = Integer.toString(sequenceRecord.getVial());
					break;
				case 5:
					text = sequenceRecord.getSubstance();
					break;
				case 6:
					text = sequenceRecord.getDescription();
					break;
				case 7:
					text = sequenceRecord.getProcessMethod();
					break;
				case 8:
					text = sequenceRecord.getReportMethod();
					break;
				case 9:
					text = decimalFormat.format(sequenceRecord.getMultiplier());
					break;
				case 10:
					text = decimalFormat.format(sequenceRecord.getInjectionVolume());
					break;
			}
		}
		return text;
	}

	@Override
	public Image getImage(Object element) {

		return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_PEAK, IApplicationImageProvider.SIZE_16x16);
	}
}
