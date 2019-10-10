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
package org.eclipse.chemclipse.processing.supplier;

import java.util.function.Consumer;

import org.eclipse.chemclipse.processing.core.MessageConsumer;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.core.runtime.IProgressMonitor;

public interface ProcessExecutionContext extends ProcessSupplierContext, MessageConsumer {

	IProgressMonitor getProgressMonitor();

	ProcessExecutionContext withMonitor(IProgressMonitor monitor);

	static ProcessExecutionContext create(ProcessSupplierContext context, MessageConsumer consumer, IProgressMonitor monitor) {

		return new ProcessExecutionContext() {

			@Override
			public void addMessage(String description, String message, Throwable t, MessageType type) {

				consumer.addMessage(description, message, t, type);
			}

			@Override
			public <T> IProcessSupplier<T> getSupplier(String id) {

				return context.getSupplier(id);
			}

			@Override
			public IProgressMonitor getProgressMonitor() {

				return monitor;
			}

			@Override
			public ProcessExecutionContext withMonitor(IProgressMonitor monitor) {

				return ProcessExecutionContext.create(context, consumer, monitor);
			}

			@Override
			public void visitSupplier(Consumer<? super IProcessSupplier<?>> consumer) {

				context.visitSupplier(consumer);
			}
		};
	}
}
