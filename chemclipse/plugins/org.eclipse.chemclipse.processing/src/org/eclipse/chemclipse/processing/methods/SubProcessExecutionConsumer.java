/*******************************************************************************
 * Copyright (c) 2019, 2022 Lablicate GmbH.
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

import org.eclipse.chemclipse.processing.supplier.IProcessExecutionConsumer;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;
import org.eclipse.chemclipse.processing.supplier.IProcessorPreferences;

public final class SubProcessExecutionConsumer<T> implements IProcessExecutionConsumer<T> {

	private final IProcessExecutionConsumer<T> intercepted;
	private final SubProcess<T> subprocess;

	public SubProcessExecutionConsumer(IProcessExecutionConsumer<T> parent, SubProcess<T> subprocess) {
		this.intercepted = parent;
		this.subprocess = subprocess;
	}

	@Override
	public <X> void execute(IProcessorPreferences<X> preferences, ProcessExecutionContext context) throws Exception {

		ProcessExecutionContext ctx2;
		if(intercepted.canExecute(preferences)) {
			context.setWorkRemaining(2);
			ProcessExecutionContext ctx1 = context.split();
			ctx1.setContextObject(IProcessExecutionConsumer.class, intercepted);
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
	public IProcessExecutionConsumer<T> withResult(Object initialResult) {

		IProcessExecutionConsumer<T> withResult = intercepted.withResult(initialResult);
		if(withResult == null) {
			return null;
		}
		return new SubProcessExecutionConsumer<T>(withResult, subprocess);
	}

	@FunctionalInterface
	public static interface SubProcess<SubType> {

		<SubX> void execute(IProcessorPreferences<SubX> preferences, IProcessExecutionConsumer<SubType> parent, ProcessExecutionContext subcontext);
	}
}
