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
package org.eclipse.chemclipse.processing.procedures;

import org.eclipse.chemclipse.processing.Processor;
import org.eclipse.chemclipse.processing.ProcessorCategory;
import org.eclipse.chemclipse.processing.supplier.IProcessExecutionConsumer;
import org.eclipse.chemclipse.processing.supplier.ProcessExecutionContext;

public interface Procedure<SettingType> extends Processor<SettingType> {

	<ResultType> IProcessExecutionConsumer<ResultType> createConsumer(SettingType settings, IProcessExecutionConsumer<ResultType> currentConsumer, ProcessExecutionContext context);

	@Override
	default ProcessorCategory[] getProcessorCategories() {

		return new ProcessorCategory[]{};
	}
}
