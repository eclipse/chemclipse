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
import java.util.function.BiConsumer;
import java.util.function.Supplier;

import org.eclipse.chemclipse.processing.core.DefaultProcessingResult;
import org.eclipse.chemclipse.processing.core.MessageProvider;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplier.SupplierType;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionConsumer;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.chemclipse.processing.supplier.ProcessSupplierContext;
import org.eclipse.chemclipse.processing.supplier.ProcessorPreferences;
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
	private final Supplier<ProcessExecutionConsumer<?>> supplier;
	private final BiConsumer<IRunnableWithProgress, Shell> executionConsumer;
	private final ProcessSupplierContext context;

	public ProcessorSupplierMenuEntry(Supplier<ProcessExecutionConsumer<?>> executionSupplier, BiConsumer<IRunnableWithProgress, Shell> executionConsumer, IProcessSupplier<T> processorSupplier, ProcessSupplierContext context) {
		this.supplier = executionSupplier;
		this.executionConsumer = executionConsumer;
		this.processorSupplier = processorSupplier;
		this.context = context;
	}

	@Override
	public String getCategory() {

		return processorSupplier.getCategory();
	}

	@Override
	public String getName() {

		String name = processorSupplier.getName().replace("&", "&&");
		if(processorSupplier.getType() == SupplierType.INTERACTIVE) {
			return name + " ...";
		}
		return name;
	}

	@Override
	public String getToolTipText() {

		return processorSupplier.getDescription();
	}

	@Override
	public void execute(Shell shell, ScrollableChart scrollableChart) {

		ProcessExecutionConsumer<?> consumer = supplier.get();
		if(consumer != null) {
			try {
				ProcessorPreferences<T> settings = SettingsWizard.getSettings(shell, SettingsWizard.getWorkspacePreferences(processorSupplier));
				if(settings == null) {
					return;
				}
				executionConsumer.accept(new IRunnableWithProgress() {

					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

						DefaultProcessingResult<Object> msgs = new DefaultProcessingResult<>();
						IProcessSupplier.applyProcessor(settings, consumer, new ProcessExecutionContext(monitor, msgs, context));
						updateResult(shell, msgs);
					}
				}, shell);
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