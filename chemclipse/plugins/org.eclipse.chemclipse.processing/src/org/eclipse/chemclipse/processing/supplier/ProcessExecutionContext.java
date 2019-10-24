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

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

import org.eclipse.chemclipse.processing.core.MessageConsumer;
import org.eclipse.chemclipse.processing.core.MessageType;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubMonitor;

public class ProcessExecutionContext implements ProcessSupplierContext, MessageConsumer {

	private static final int WORK_UNIT = 100;
	private final SubMonitor subMonitor;
	private final ProcessSupplierContext context;
	private final MessageConsumer consumer;
	private ProcessExecutionContext parent;
	private final Map<Class<?>, Object> contextMap = new IdentityHashMap<>();

	public ProcessExecutionContext(IProgressMonitor monitor, MessageConsumer rootConsumer, ProcessSupplierContext rootContext) {
		this(monitor, rootConsumer, rootContext, null);
	}

	private ProcessExecutionContext(IProgressMonitor monitor, MessageConsumer rootConsumer, ProcessSupplierContext rootContext, ProcessExecutionContext parent) {
		this.consumer = rootConsumer;
		this.context = rootContext;
		this.parent = parent;
		subMonitor = SubMonitor.convert(monitor, WORK_UNIT);
	}

	public IProgressMonitor getProgressMonitor() {

		return subMonitor;
	}

	public ProcessExecutionContext getParent() {

		return parent;
	}

	public <T> ProcessExecutionContext getParent(Class<T> clazz, Predicate<T> predicate) {

		if(parent != null) {
			T contextObject = parent.getContextObject(clazz);
			if(predicate.test(contextObject)) {
				return parent;
			} else {
				return parent.getParent(clazz, predicate);
			}
		}
		return parent;
	}

	@Override
	public void addMessage(String description, String message, String details, Throwable t, MessageType type) {

		consumer.addMessage(description, message, details, t, type);
	}

	@Override
	public <T> IProcessSupplier<T> getSupplier(String id) {

		IProcessSupplier<T> supplier = context.getSupplier(id);
		if(supplier == null && parent != null) {
			return parent.getSupplier(id);
		}
		return supplier;
	}

	@Override
	public void visitSupplier(Consumer<? super IProcessSupplier<?>> consumer) {

		context.visitSupplier(consumer);
		if(parent != null) {
			parent.visitSupplier(consumer);
		}
	}

	public void setWorkRemaining(int workRemaining) {

		subMonitor.setWorkRemaining(workRemaining * WORK_UNIT);
	}

	public ProcessExecutionContext split() {

		return split(context);
	}

	public ProcessExecutionContext split(ProcessSupplierContext childContext) {

		return new ProcessExecutionContext(subMonitor.split(WORK_UNIT), consumer, childContext, this);
	}

	@SuppressWarnings("unchecked")
	public <T> T setContextObject(Class<T> type, T object) {

		if(object == null) {
			return (T)contextMap.remove(type);
		} else {
			return (T)contextMap.put(type, object);
		}
	}

	public <T> T getContextObject(Class<T> type) {

		Object object = contextMap.get(type);
		if(type.isInstance(object)) {
			return type.cast(object);
		}
		if(parent != null) {
			return parent.getContextObject(type);
		}
		return null;
	}
}
