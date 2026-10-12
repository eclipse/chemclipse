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
import org.eclipse.chemclipse.process.supplier.batchprocess.Activator;

public class BatchProcessSupport {

	private BatchProcessSupport() {

	}

	/**
	 * Returns the batch process that is able to run jobs of the given data type.
	 *
	 * @param dataType
	 * @return the matching {@link IBatchProcess} or <code>null</code> if none is registered
	 */
	public static IBatchProcess getBatchProcess(DataType dataType) {

		for(IBatchProcess batchProcess : Activator.getBatchProcesses()) {
			if(batchProcess.isValidFor(dataType)) {
				return batchProcess;
			}
		}

		return null;
	}
}
