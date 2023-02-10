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
 * Christoph Läubrich - don't use private ProcessTypeSupport, add tooltip message in case of error
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.internal.provider;

import java.text.MessageFormat;
import java.util.Arrays;
import java.util.function.BiFunction;

import org.eclipse.chemclipse.processing.methods.IProcessEntry;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.chemclipse.processing.methods.ProcessEntryContainer;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplierContext;
import org.eclipse.chemclipse.processing.supplier.IProcessorPreferences;
import org.eclipse.chemclipse.rcp.ui.icons.core.ApplicationImageFactory;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImage;
import org.eclipse.chemclipse.rcp.ui.icons.core.IApplicationImageProvider;
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.chemclipse.ux.extension.xxd.ui.l10n.ExtensionMessages;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.graphics.Image;

public class MethodListLabelProvider extends AbstractChemClipseLabelProvider {

	private final IProcessSupplierContext processTypeSupport;
	private BiFunction<IProcessEntry, IProcessSupplierContext, IProcessorPreferences<?>> preferencesSupplier;
	//
	public static final String[] TITLES = {//
			"", //
			ExtensionMessages.name, //
			ExtensionMessages.description, //
			ExtensionMessages.type, //
			ExtensionMessages.settings, //
			ExtensionMessages.id //
	};
	//
	public static final int[] BOUNDS = {//
			50, //
			250, //
			250, //
			160, //
			300, //
			110 //
	};

	public MethodListLabelProvider(IProcessSupplierContext processTypeSupport, BiFunction<IProcessEntry, IProcessSupplierContext, IProcessorPreferences<?>> preferencesSupplier) {

		this.processTypeSupport = processTypeSupport;
		this.preferencesSupplier = preferencesSupplier;
	}

	@Override
	public Image getColumnImage(Object element, int columnIndex) {

		if(columnIndex == 0) {
			if(element instanceof IProcessEntry processEntry) {
				/*
				 * Validate
				 */
				IStatus status = validate(processEntry);
				//
				if(status.matches(IStatus.ERROR)) {
					return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_STATUS_ERROR, IApplicationImageProvider.SIZE_16x16);
				}
				if(status.matches(IStatus.WARNING)) {
					return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_STATUS_WARN, IApplicationImageProvider.SIZE_16x16);
				}
				if(status.matches(IStatus.INFO)) {
					return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_STATUS_EMPTY, IApplicationImageProvider.SIZE_16x16);
				}
				if(status.isOK()) {
					return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_STATUS_OK, IApplicationImageProvider.SIZE_16x16);
				}
			} else if(element instanceof IProcessMethod) {
				return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_METHOD, IApplicationImageProvider.SIZE_16x16);
			} else if(element instanceof ProcessEntryContainer) {
				return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_FOLDER_OPENED, IApplicationImageProvider.SIZE_16x16);
			}
		}
		return null;
	}

	@Override
	public String getToolTipText(Object element) {

		if(element instanceof IProcessEntry processEntry) {
			IStatus status = validate(processEntry);
			if(!status.isOK()) {
				return status.getMessage();
			}
		}
		return super.getToolTipText(element);
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {

		if(columnIndex == 0) {
			return "";
		}
		//
		if(element instanceof IProcessEntry processEntry) {
			IProcessSupplierContext supplierContext = IProcessEntry.getContext(processEntry, processTypeSupport);
			IProcessSupplier<?> processSupplier = supplierContext.getSupplier(processEntry.getProcessorId());
			//
			switch(columnIndex) {
				case 1:
					return processEntry.getName();
				case 2:
					return processEntry.getDescription();
				case 3: {
					if(processSupplier != null) {
						return Arrays.toString(processSupplier.getSupportedDataTypes().toArray());
					}
				}
					break;
				case 4:
					if(processSupplier != null) {
						if(processSupplier.getSettingsParser().getInputValues().isEmpty() || preferencesSupplier == null) {
							return "not configurable";
						}
						IProcessorPreferences<?> preferences = preferencesSupplier.apply(processEntry, supplierContext);
						if(preferences.isUseSystemDefaults()) {
							return "defaults";
						}
						String text = preferences.getUserSettingsAsString();
						if(text.startsWith("{")) {
							text = text.substring(1);
						}
						if(text.endsWith("}")) {
							text = text.substring(0, text.length() - 1);
						}
						return text.replace("\"", "");
					}
					break;
				case 5:
					return processEntry.getProcessorId();
				default:
					break;
			}
		} else if(element instanceof ProcessEntryContainer method) {
			switch(columnIndex) {
				case 1:
					return method.getName();
				case 2:
					return method.getDescription();
				default:
					return "";
			}
		}
		//
		return "n/a";
	}

	@Override
	public Image getImage(Object element) {

		return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_PROCESS_CONTROL, IApplicationImageProvider.SIZE_16x16);
	}

	private IStatus validate(IProcessEntry processEntry) {

		/*
		 * Validation
		 */
		if(processEntry == null) {
			return ValidationStatus.error(ExtensionMessages.processorNotAvailable);
		}
		//
		if(preferencesSupplier == null) {
			return ValidationStatus.error(ExtensionMessages.preferenceSupplierNotAvailable);
		}
		//
		if(processTypeSupport == null) {
			return ValidationStatus.error(ExtensionMessages.processTypeSupportNotAvailable);
		}
		/*
		 * Checks
		 */
		if(processEntry.isSkipValidation()) {
			return ValidationStatus.info(MessageFormat.format(ExtensionMessages.processorSkipValidationOptionSet, processEntry.getName()));
		}
		IProcessorPreferences<?> processorPreferences = preferencesSupplier.apply(processEntry, processTypeSupport);
		//
		if(processorPreferences == null) {
			return ValidationStatus.error(MessageFormat.format(ExtensionMessages.processorPreferencesNotAvailable, processEntry.getName()));
		}
		//
		if(processorPreferences.getSupplier().getSettingsClass() == null) {
			return ValidationStatus.warning(MessageFormat.format(ExtensionMessages.processorHasNoSettingsClass, processEntry.getName()));
		}
		//
		if(processorPreferences.isUseSystemDefaults()) {
			return ValidationStatus.info(MessageFormat.format(ExtensionMessages.processorUsesSystemDefaultSettings, processEntry.getName()));
		}
		try {
			processorPreferences.getUserSettings();
			return ValidationStatus.ok();
		} catch(Exception e) {
			return ValidationStatus.error(MessageFormat.format(ExtensionMessages.processorSettingsCannotBetParsed, processEntry.getName()), e);
		}
	}
}
