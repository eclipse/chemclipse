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
import org.eclipse.chemclipse.model.supplier.IChromatogramSelectionProcessSupplier;
import org.eclipse.chemclipse.processing.core.DefaultProcessingResult;
import org.eclipse.chemclipse.processing.core.MessageProvider;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;
import org.eclipse.chemclipse.ux.extension.xxd.ui.methods.SettingsWizard;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swtchart.extensions.core.ScrollableChart;
import org.eclipse.swtchart.extensions.menu.AbstractChartMenuEntry;
import org.eclipse.swtchart.extensions.menu.IChartMenuEntry;

public class ProcessorSupplierMenuEntry<T> extends AbstractChartMenuEntry implements IChartMenuEntry {

	private final IProcessSupplier<T> processorSupplier;
	private final Supplier<IChromatogramSelection<?, ?>> supplier;
	private final BiConsumer<IRunnableWithProgress, Shell> executionConsumer;

	public ProcessorSupplierMenuEntry(Supplier<IChromatogramSelection<?, ?>> chromatogramSupplier, BiConsumer<IRunnableWithProgress, Shell> executionConsumer, IProcessSupplier<T> processorSupplier) {
		this.supplier = chromatogramSupplier;
		this.executionConsumer = executionConsumer;
		this.processorSupplier = processorSupplier;
	}

	@Override
	public String getCategory() {

		return processorSupplier.getCategory();
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
				T settings = SettingsWizard.getSettings(shell, SettingsWizard.getWorkspacePreferences(processorSupplier));
				executionConsumer.accept(new IRunnableWithProgress() {

					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

						DefaultProcessingResult<Object> msgs = new DefaultProcessingResult<>();
						IChromatogramSelectionProcessSupplier.applyProcessor(chromatogramSelection, processorSupplier, settings, msgs, monitor);
						updateResult(shell, msgs);
					}
				}, shell);
			} catch(CancellationException e) {
				// user has canceled so cancel the action as well
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