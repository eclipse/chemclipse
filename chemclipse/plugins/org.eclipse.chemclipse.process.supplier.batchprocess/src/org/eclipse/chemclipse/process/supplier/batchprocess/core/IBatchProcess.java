/*******************************************************************************
 * Copyright (c) 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 *
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.process.supplier.batchprocess.core;

import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.supplier.IProcessSupplierContext;
import org.eclipse.core.runtime.IProgressMonitor;

/**
 * Executes a batch job for one kind of measurement. Batch jobs of every data
 * type share the *.obj format, hence the implementation that runs a given job
 * is selected by its {@link DataType}, see
 * {@link BatchProcessSupport#getBatchProcess(DataType)}.
 * <p>
 * Implementations are contributed as OSGi declarative services and must be
 * stateless, as one instance serves every job of its data types.
 */
public interface IBatchProcess {

	/**
	 * Returns whether this batch process is able to run jobs of the given data type.
	 *
	 * @param dataType
	 * @return <code>true</code> if the data type is supported
	 */
	boolean isValidFor(DataType dataType);

	/**
	 * Runs the given batch job.
	 *
	 * @param batchProcessJob
	 * @param processSupplierContext
	 * @param monitor
	 * @return the collected messages of the run
	 */
	IProcessingInfo<?> execute(BatchProcessJob batchProcessJob, IProcessSupplierContext processSupplierContext, IProgressMonitor monitor);
}
