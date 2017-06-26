/*******************************************************************************
 * Copyright (c) 2017 Jan Holy.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Jan Holy - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.model;

import java.util.List;

public interface IGroup extends ISample {

	/**
	 * @link {@link #setPcaResult(List)}
	 * @param pcaResults
	 */
	void setPcaResult(IPcaResults pcaResults);

	/**
	 * Set values in PcaResult. Values are set as mean of samples which contain same group name as this object
	 * You have to set group name before calling this method
	 *
	 * @param sammples
	 */
	void setPcaResult(List<ISample> samples);
}
