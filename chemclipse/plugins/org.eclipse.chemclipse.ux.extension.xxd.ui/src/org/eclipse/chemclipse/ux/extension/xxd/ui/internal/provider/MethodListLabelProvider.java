/*******************************************************************************
 * Copyright (c) 2018, 2022 Lablicate GmbH.
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
import org.eclipse.chemclipse.support.ui.provider.AbstractChemClipseLabelProvider;
import org.eclipse.core.databinding.validation.ValidationStatus;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.swt.graphics.Image;

public class MethodListLabelProvider extends AbstractChemClipseLabelProvider {

	private final IProcessSupplierContext processTypeSupport;
	private BiFunction<IProcessEntry, IProcessSupplierContext, IProcessorPreferences<?>> preferencesSupplier;
	//
	public static final String[] TITLES = {//
			"", //
			"Name", //
			"Description", //
			"Type", //
			"Settings", //
			"ID" //
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
			if(element instanceof IProcessEntry) {
				/*
				 * Validate
				 */
				IProcessEntry processEntry = (IProcessEntry)element;
				IStatus status = validate(processEntry);
				//
				if(status.matches(IStatus.ERROR)) {
					return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_STATUS_ERROR, IApplicationImage.SIZE_16x16);
				} else if(status.matches(IStatus.WARNING)) {
					return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_STATUS_WARN, IApplicationImage.SIZE_16x16);
				} else if(status.matches(IStatus.INFO)) {
					return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_STATUS_EMPTY, IApplicationImage.SIZE_16x16);
				} else if(status.isOK()) {
					return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_STATUS_OK, IApplicationImage.SIZE_16x16);
				}
			} else if(element instanceof IProcessMethod) {
				return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_METHOD, IApplicationImage.SIZE_16x16);
			} else if(element instanceof ProcessEntryContainer) {
				return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_FOLDER_OPENED, IApplicationImage.SIZE_16x16);
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

	@Override
	public String getColumnText(Object element, int columnIndex) {

		if(columnIndex == 0) {
			return "";
		}
		//
		if(element instanceof IProcessEntry) {
			IProcessEntry processEntry = (IProcessEntry)element;
			IProcessSupplierContext supplierContext = IProcessEntry.getContext(processEntry, processTypeSupport);
			IProcessSupplier<?> processSupplier = supplierContext.getSupplier(processEntry.getProcessorId());
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
						} else {
							IProcessorPreferences<?> preferences = preferencesSupplier.apply(processEntry, supplierContext);
							if(preferences.isUseSystemDefaults()) {
								return "defaults";
							} else {
								String text = preferences.getUserSettingsAsString();
								if(text.startsWith("{")) {
									text = text.substring(1);
								}
								if(text.endsWith("}")) {
									text = text.substring(0, text.length() - 1);
								}
								return text.replace("\"", "");
							}
						}
					}
					break;
				case 5:
					return processEntry.getProcessorId();
				default:
					break;
			}
		} else if(element instanceof ProcessEntryContainer) {
			ProcessEntryContainer method = (ProcessEntryContainer)element;
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

		return ApplicationImageFactory.getInstance().getImage(IApplicationImage.IMAGE_PROCESS_CONTROL, IApplicationImage.SIZE_16x16);
	}

	private IStatus validate(IProcessEntry processEntry) {

		/*
		 * Validation
		 */
		if(processEntry == null) {
			return ValidationStatus.error("The processor is not available.");
		}
		//
		if(preferencesSupplier == null) {
			return ValidationStatus.error("The preference supplier is not available.");
		}
		//
		if(processTypeSupport == null) {
			return ValidationStatus.error("The process type support is not available.");
		}
		/*
		 * Checks
		 */
		IProcessorPreferences<?> processorPreferences = preferencesSupplier.apply(processEntry, processTypeSupport);
		//
		if(processorPreferences == null) {
			return ValidationStatus.error("The processor " + processEntry.getName() + " preferences are not available.");
		}
		//
		if(processorPreferences.getSupplier().getSettingsClass() == null) {
			return ValidationStatus.warning("The processor " + processEntry.getName() + " has no settings class.");
		}
		//
		if(processorPreferences.isUseSystemDefaults()) {
			return ValidationStatus.info("The processor " + processEntry.getName() + " uses system default settings.");
		} else {
			try {
				processorPreferences.getUserSettings();
				return ValidationStatus.ok();
			} catch(Exception e) {
				return ValidationStatus.error("The processor " + processEntry.getName() + " settings can't be parsed.", e);
			}
		}
	}
}
