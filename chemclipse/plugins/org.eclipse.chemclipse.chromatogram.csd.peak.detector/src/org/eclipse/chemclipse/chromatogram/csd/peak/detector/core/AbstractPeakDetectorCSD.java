/*******************************************************************************
 * Copyright (c) 2014, 2021 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.csd.peak.detector.core;

import org.eclipse.chemclipse.chromatogram.peak.detector.core.AbstractPeakDetector;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;

public abstract class AbstractPeakDetectorCSD<P extends IPeak, C extends IChromatogram<P>, R> extends AbstractPeakDetector<P, C, R> implements IPeakDetectorCSD<P, C, R> {
}
