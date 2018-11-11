/*******************************************************************************
 * Copyright (c) 2018 Lablicate GmbH.
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

import java.util.Arrays;

import org.eclipse.chemclipse.model.methods.IProcessEntry;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.chemclipse.xxd.process.support.ProcessTypeSupport;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.graphics.Image;

public class MethodListLabelProvider extends AbstractChemClipseLabelProvider {

	private ProcessTypeSupport processTypeSupport = new ProcessTypeSupport();
	//
	public static String[] TITLES = {//
			"Status", //
			"Name", //
			"Description", //
			"Type", //
			"Settings", //
			"ID" //
	};
	//
	public static int[] BOUNDS = {//
			30, //
			250, //
			250, //
			160, //
			300, //
			110 //
	};

	@Override
	public Image getColumnImage(Object element, int columnIndex) {

		if(columnIndex == 0) {
			if(element instanceof IProcessEntry) {
				Image image;
				int status = processTypeSupport.validate((IProcessEntry)element);
				switch(status) {
					case IStatus.ERROR:
						image = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_STATUS_ERROR, IApplicationImage.SIZE_16x16);
						break;
					case IStatus.WARNING:
						image = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_STATUS_WARN, IApplicationImage.SIZE_16x16);
						break;
					case IStatus.INFO:
						image = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_STATUS_EMPTY, IApplicationImage.SIZE_16x16);
						break;
					case IStatus.OK:
						image = ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_STATUS_OK, IApplicationImage.SIZE_16x16);
						break;
					default:
						image = null;
						break;
				}
				return image;
			}
		}
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {

		String text = "";
		if(element instanceof IProcessEntry) {
			IProcessEntry entry = (IProcessEntry)element;
			switch(columnIndex) {
				case 0:
					text = ""; // Validation
					break;
				case 1:
					text = entry.getName();
					break;
				case 2:
					text = entry.getDescription();
					break;
				case 3:
					text = Arrays.toString(entry.getSupportedDataTypes().toArray());
					break;
				case 4:
					String jsonSettings = entry.getJsonSettings();
					if(jsonSettings.equals(IProcessEntry.EMPTY_JSON_SETTINGS)) {
						text = "System Settings";
					} else {
						text = jsonSettings;
					}
					break;
				case 5:
					text = entry.getProcessorId();
					break;
				default:
					break;
			}
		}
		return text;
	}

	@Override
	public Image getImage(Object element) {

		return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_PROCESS_CONTROL, IApplicationImage.SIZE_16x16);
	}
}
