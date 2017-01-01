/*******************************************************************************
 * Copyright (c) 2012, 2017 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.xxd.integrator.processing;

import org.eclipse.chemclipse.chromatogram.xxd.integrator.result.IChromatogramIntegrationResults;
import org.eclipse.chemclipse.processing.core.IProcessingInfo;
import org.eclipse.chemclipse.processing.core.exceptions.TypeCastException;

public interface IChromatogramIntegratorProcessingInfo extends IProcessingInfo {

	IChromatogramIntegrationResults getChromatogramIntegrationResults() throws TypeCastException;

	void setChromatogramIntegrationResults(IChromatogramIntegrationResults chromatogramIntegrationResults);
}
