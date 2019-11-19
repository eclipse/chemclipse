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

import java.io.IOException;

import org.eclipse.chemclipse.processing.methods.ProcessEntryContainer;

/**
 * 
 * A {@link ExecutionResultTransformer} can transform the consumer of a {@link ProcessEntryContainer} child invocation
 */
public interface ExecutionResultTransformer<SettingType> extends IProcessSupplier<SettingType> {

	<T> ProcessExecutionConsumer<T> transform(ProcessExecutionConsumer<T> consumer, ProcessorPreferences<SettingType> processorPreferences, ProcessExecutionContext context) throws IOException;
}
