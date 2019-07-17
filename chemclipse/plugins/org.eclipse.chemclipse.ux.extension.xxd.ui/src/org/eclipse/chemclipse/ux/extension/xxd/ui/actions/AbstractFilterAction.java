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
package org.eclipse.chemclipse.ux.extension.xxd.ui.actions;

import java.lang.reflect.InvocationTargetException;
import java.util.function.Consumer;

import org.eclipse.chemclipse.processing.core.DefaultProcessingResult;
import org.eclipse.chemclipse.processing.core.MessageConsumer;
import org.eclipse.chemclipse.processing.filter.Filter;
import org.eclipse.chemclipse.processing.ui.support.ProcessingInfoViewSupport;
import org.eclipse.core.runtime.Adapters;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Widget;

public abstract class AbstractFilterAction<FilterType extends Filter<?>, ResultType> extends Action {

	protected FilterType filter;
	private Consumer<ResultType> resultConsumer;

	public AbstractFilterAction(FilterType filter, Consumer<ResultType> resultConsumer) {
		this.filter = filter;
		this.resultConsumer = resultConsumer;
		setToolTipText(filter.getDescription());
		setId(filter.getID());
		setText(filter.getName());
		setImageDescriptor(Adapters.adapt(filter, ImageDescriptor.class));
	}

	@Override
	public void runWithEvent(Event event) {

		Widget widget = event.widget;
		Shell shell;
		if(widget instanceof MenuItem) {
			shell = ((MenuItem)widget).getParent().getShell();
		} else if(widget instanceof Control) {
			shell = ((Control)widget).getShell();
		} else {
			shell = null;
		}
		executeAction(shell);
	}

	public void executeAction(Shell shell) {

		DefaultProcessingResult<Object> consumer = new DefaultProcessingResult<>();
		ProgressMonitorDialog monitorDialog = new ProgressMonitorDialog(shell);
		monitorDialog.setCancelable(true);
		try {
			monitorDialog.run(true, true, new IRunnableWithProgress() {

				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {

					ResultType result = computeResult(consumer, monitor);
					if(result != null) {
						resultConsumer.accept(result);
					}
				}
			});
			ProcessingInfoViewSupport.updateProcessingInfo(consumer);
		} catch(InvocationTargetException e) {
			ProcessingInfoViewSupport.updateProcessingInfoError(filter.getName(), "Processing failed", e.getTargetException());
		} catch(InterruptedException e) {
			// user canceled
			return;
		}
	}

	protected abstract ResultType computeResult(MessageConsumer messageConsumer, IProgressMonitor progressMonitor);
}
