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
import org.eclipse.chemclipse.ux.extension.xxd.ui.methods.SettingsWizard;
import org.eclipse.chemclipse.xxd.process.support.IChromatogramSelectionProcessTypeSupplier;
import org.eclipse.chemclipse.xxd.process.support.IProcessSupplier;
import org.eclipse.chemclipse.xxd.process.support.IProcessTypeSupplier;
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
				Object settings = SettingsWizard.getSettings(shell, processorSupplier);
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

	public void updateResult(Shell shell, MessageProvider result) {

		if(result != null) {
			shell.getDisplay().asyncExec(() -> ProcessingInfoViewSupport.updateProcessingInfo(result, result.hasErrorMessages()));
		}
	}
}