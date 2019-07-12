/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.swt.editors;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.processing.core.DefaultProcessingResult;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;
import org.eclipse.chemclipse.support.settings.parser.InputValue;
import org.eclipse.chemclipse.ux.extension.xxd.ui.methods.SettingsPreferencesWizard;
import org.eclipse.chemclipse.xxd.process.support.IProcessTypeSupplier;
import org.eclipse.chemclipse.xxd.process.support.ProcessorPreferences;
import org.eclipse.chemclipse.xxd.process.support.ProcessorSupplier;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtchart.extensions.core.ScrollableChart;
import org.eclipse.swtchart.extensions.menu.AbstractChartMenuEntry;
import org.eclipse.swtchart.extensions.menu.IChartMenuEntry;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class ProcessorSupplierMenuEntry extends AbstractChartMenuEntry implements IChartMenuEntry {

	private IProcessTypeSupplier<?> typeSupplier;
	private ProcessorSupplier processorSupplier;
	private Supplier<IChromatogramSelection<?, ?>> supplier;
	private BiConsumer<IRunnableWithProgress, Shell> executionConsumer;
	private ProcessorPreferences preferences;

	public ProcessorSupplierMenuEntry(Supplier<IChromatogramSelection<?, ?>> chromatogramSupplier, BiConsumer<IRunnableWithProgress, Shell> executionConsumer, IProcessTypeSupplier<?> typeSupplier, ProcessorSupplier processorSupplier, ProcessorPreferences processorPreferences) {
		this.supplier = chromatogramSupplier;
		this.executionConsumer = executionConsumer;
		this.typeSupplier = typeSupplier;
		this.processorSupplier = processorSupplier;
		this.preferences = processorPreferences;
	}

	@Override
	public String getCategory() {

		return typeSupplier.getCategory();
	}

	@Override
	public String getName() {

		return processorSupplier.getName();
	}

	@Override
	public String getToolTipText() {

		return processorSupplier.getDescription();
	}

	@Override
	public void execute(Shell shell, ScrollableChart scrollableChart) {

		IChromatogramSelection<?, ?> chromatogramSelection = supplier.get();
		if(chromatogramSelection != null) {
			Class<? extends IProcessSettings> settingsClass = processorSupplier.getSettingsClass();
			IProcessSettings settings;
			if(settingsClass == null) {
				settings = null;
			} else {
				try {
					if(preferences.isAskForSettings()) {
						List<InputValue> values = InputValue.readJSON(settingsClass, preferences.getUserSettings());
						if(!values.isEmpty()) {
							if(!SettingsPreferencesWizard.openWizard(shell, values, preferences, processorSupplier)) {
								// user has canceled the wizard so cancel the processing also
								return;
							}
						}
					}
					if(preferences.isUseSystemDefaults()) {
						settings = settingsClass.newInstance();
					} else {
						String userSettings = preferences.getUserSettings();
						ObjectMapper objectMapper = new ObjectMapper();
						objectMapper.disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
						settings = objectMapper.readValue(userSettings, settingsClass);
					}
				} catch(IOException | ReflectiveOperationException e) {
					DefaultProcessingResult<Object> result = new DefaultProcessingResult<>();
					result.addErrorMessage(getName(), "can't process settings", e);
					return;
				}
			}
			executionConsumer.accept(new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

					IProcessingInfo<?> result = typeSupplier.applyProcessor(chromatogramSelection, processorSupplier.getId(), settings, monitor);
					updateResult(shell, result);
				}
			}, shell);
		}
	}

	public void updateResult(Shell shell, IProcessingInfo<?> result) {

		if(result != null) {
			shell.getDisplay().asyncExec(() -> ProcessingInfoViewSupport.updateProcessingInfo(result, result.hasErrorMessages()));
		}
	}
}