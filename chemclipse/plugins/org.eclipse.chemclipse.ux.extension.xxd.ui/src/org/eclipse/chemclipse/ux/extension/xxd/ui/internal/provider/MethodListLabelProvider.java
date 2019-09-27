/*******************************************************************************
 * Copyright (c) 2018, 2019 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Christoph Läubrich - don't use private ProcessTypeSupport, add tooltip message in case of error
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import java.io.IOException;
import java.util.Arrays;

import org.eclipse.chemclipse.model.methods.IProcessEntry;
import org.eclipse.chemclipse.processing.supplier.ProcessorPreferences;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.chemclipse.xxd.process.support.ProcessTypeSupport;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.graphics.Image;

public class MethodListLabelProvider extends AbstractChemClipseLabelProvider {

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
				IStatus status = validate((IProcessEntry)element);
				if(status.matches(IStatus.ERROR)) {
					return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_STATUS_ERROR, IApplicationImage.SIZE_16x16);
				} else if(status.matches(IStatus.WARNING)) {
					return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_STATUS_WARN, IApplicationImage.SIZE_16x16);
				} else if(status.matches(IStatus.INFO)) {
					return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_STATUS_EMPTY, IApplicationImage.SIZE_16x16);
				} else if(status.isOK()) {
					return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_STATUS_OK, IApplicationImage.SIZE_16x16);
				}
			}
		}
		return null;
	}

	@Override
	public String getToolTipText(Object element) {

		if(element instanceof IProcessEntry) {
			IStatus status = validate((IProcessEntry)element);
			if(!status.isOK()) {
				return status.getMessage();
			}
		}
		return super.getToolTipText(element);
	}

	private final ProcessTypeSupport processTypeSupport;

	public MethodListLabelProvider(ProcessTypeSupport processTypeSupport) {
		this.processTypeSupport = processTypeSupport;
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

	public IStatus validate(IProcessEntry processEntry) {

		if(processEntry == null) {
			return ValidationStatus.error("Entry is null");
		}
		ProcessorPreferences<?> preferences = IProcessEntry.getProcessEntryPreferences(processEntry, processTypeSupport);
		if(preferences == null) {
			return ValidationStatus.error("Processor " + processEntry.getName() + " not avaiable");
		}
		if(preferences.getSupplier().getSettingsClass() == null) {
			return ValidationStatus.warning("Processor " + processEntry.getName() + " has no settingsclass");
		}
		if(preferences.isUseSystemDefaults()) {
			return ValidationStatus.info("Processor " + processEntry.getName() + " uses system default settings");
		} else {
			try {
				preferences.getUserSettings();
			} catch(IOException e) {
				return ValidationStatus.error("Loading settings for Processor " + processEntry.getName() + "failed", e);
			}
			return ValidationStatus.ok();
		}
	}
}
