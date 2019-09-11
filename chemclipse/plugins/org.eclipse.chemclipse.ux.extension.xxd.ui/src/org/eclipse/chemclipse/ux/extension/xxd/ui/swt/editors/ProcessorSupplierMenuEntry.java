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
import java.util.concurrent.CancellationException;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import org.eclipse.chemclipse.model.selection.IChromatogramSelection;
import org.eclipse.chemclipse.model.settings.IProcessSettings;
import org.eclipse.chemclipse.processing.core.DefaultProcessingResult;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.MessageProvider;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;
import org.eclipse.chemclipse.support.settings.SystemSettingsStrategy;
import org.eclipse.chemclipse.support.settings.parser.SettingsClassParser;
import org.eclipse.chemclipse.support.settings.parser.SettingsParser;
import org.eclipse.chemclipse.ux.extension.xxd.ui.methods.SettingsPreferencesWizard;
import org.eclipse.chemclipse.xxd.process.support.IChromatogramSelectionProcessTypeSupplier;
import org.eclipse.chemclipse.xxd.process.support.IProcessSupplier;
import org.eclipse.chemclipse.xxd.process.support.IProcessTypeSupplier;
import org.eclipse.chemclipse.xxd.process.support.ProcessorPreferences;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtchart.extensions.core.ScrollableChart;
import org.eclipse.swtchart.extensions.menu.AbstractChartMenuEntry;
import org.eclipse.swtchart.extensions.menu.IChartMenuEntry;

public class ProcessorSupplierMenuEntry extends AbstractChartMenuEntry implements IChartMenuEntry {

	private IProcessTypeSupplier typeSupplier;
	private IProcessSupplier<?> processorSupplier;
	private Supplier<IChromatogramSelection<?, ?>> supplier;
	private BiConsumer<IRunnableWithProgress, Shell> executionConsumer;

	public ProcessorSupplierMenuEntry(Supplier<IChromatogramSelection<?, ?>> chromatogramSupplier, BiConsumer<IRunnableWithProgress, Shell> executionConsumer, IProcessTypeSupplier typeSupplier, IProcessSupplier<?> processorSupplier) {
		this.supplier = chromatogramSupplier;
		this.executionConsumer = executionConsumer;
		this.typeSupplier = typeSupplier;
		this.processorSupplier = processorSupplier;
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
			try {
				Object settings = getSettings(shell, processorSupplier);
				executionConsumer.accept(new IRunnableWithProgress() {

					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

						IProcessSettings processSettings;
						if(settings instanceof IProcessSettings) {
							processSettings = (IProcessSettings)settings;
						} else {
							processSettings = null;
						}
						if(typeSupplier instanceof IChromatogramSelectionProcessTypeSupplier) {
							IProcessingInfo<?> result = ((IChromatogramSelectionProcessTypeSupplier)typeSupplier).applyProcessor(chromatogramSelection, processorSupplier.getId(), processSettings, monitor);
							updateResult(shell, result);
						}
					}
				}, shell);
			} catch(CancellationException e) {
				// user has canceld so cance the action as well
				return;
			} catch(IOException e) {
				DefaultProcessingResult<Object> result = new DefaultProcessingResult<>();
				result.addErrorMessage(processorSupplier.getName(), "can't process settings", e);
				updateResult(shell, result);
			}
		}
	}

	/**
	 * Obtain the settings from the user, maybe asking for input
	 * 
	 * @param shell
	 * @param processorSupplier
	 * @return
	 * @throws IOException
	 */
	public static <T> T getSettings(Shell shell, IProcessSupplier<T> processorSupplier) throws IOException {

		Class<T> settingsClass = processorSupplier.getSettingsClass();
		if(settingsClass == null) {
			return null;
		} else {
			SettingsParser parser = new SettingsClassParser(settingsClass);
			ProcessorPreferences<T> preferences = processorSupplier.getPreferences();
			if(preferences.isAskForSettings()) {
				if(!parser.getInputValues().isEmpty()) {
					if(!SettingsPreferencesWizard.openWizard(shell, parser, processorSupplier)) {
						throw new CancellationException("user has canceled the wizard");
					}
				}
			}
			if(preferences.isUseSystemDefaults()) {
				if(parser.getSystemSettingsStrategy() == SystemSettingsStrategy.NEW_INSTANCE) {
					try {
						return settingsClass.newInstance();
					} catch(InstantiationException | IllegalAccessException e) {
						throw new IOException("can't create new instance", e);
					}
				} else {
					return null;
				}
			} else {
				return preferences.getUserSettings();
			}
		}
	}

	public void updateResult(Shell shell, MessageProvider result) {

		if(result != null) {
			shell.getDisplay().asyncExec(() -> ProcessingInfoViewSupport.updateProcessingInfo(result, result.hasErrorMessages()));
		}
	}
}