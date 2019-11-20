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
package org.eclipse.chemclipse.processing.methods;

import org.eclipse.chemclipse.processing.supplier.ProcessExecutionConsumer;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.chemclipse.processing.supplier.ProcessorPreferences;

public final class SubProcessExecutionConsumer<T> implements ProcessExecutionConsumer<T> {

	private final ProcessExecutionConsumer<T> intercepted;
	private final SubProcess<T> subprocess;

	public SubProcessExecutionConsumer(ProcessExecutionConsumer<T> parent, SubProcess<T> subprocess) {
		this.intercepted = parent;
		this.subprocess = subprocess;
	}

	@Override
	public <X> void execute(ProcessorPreferences<X> preferences, ProcessExecutionContext context) throws Exception {

		ProcessExecutionContext ctx2;
		if(intercepted.canExecute(preferences)) {
			context.setWorkRemaining(2);
			ProcessExecutionContext ctx1 = context.split();
			ctx1.setContextObject(ProcessExecutionConsumer.class, intercepted);
			intercepted.execute(preferences, ctx1);
			ctx2 = context.split();
		} else {
			ctx2 = context;
		}
		subprocess.execute(preferences, intercepted, ctx2);
	}

	@Override
	public T getResult() {

		return intercepted.getResult();
	}

	@Override
	public ProcessExecutionConsumer<T> withResult(Object initialResult) {

		ProcessExecutionConsumer<T> withResult = intercepted.withResult(initialResult);
		if(withResult == null) {
			return null;
		}
		return new SubProcessExecutionConsumer<T>(withResult, subprocess);
	}

	@FunctionalInterface
	public static interface SubProcess<SubType> {

		<SubX> void execute(ProcessorPreferences<SubX> preferences, ProcessExecutionConsumer<SubType> parent, ProcessExecutionContext subcontext);
	}
}
