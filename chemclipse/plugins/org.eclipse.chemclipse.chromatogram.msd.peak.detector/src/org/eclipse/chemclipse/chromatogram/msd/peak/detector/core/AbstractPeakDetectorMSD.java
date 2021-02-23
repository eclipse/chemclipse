/*******************************************************************************
 * Copyright (c) 2008, 2021 Lablicate GmbH.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.chromatogram.msd.peak.detector.core;

import org.eclipse.chemclipse.chromatogram.peak.detector.core.AbstractPeakDetector;
import org.eclipse.chemclipse.model.core.IChromatogram;
import org.eclipse.chemclipse.model.core.IPeak;

/**
 * Extend this class to implement a valid peak detector.r
 */
public abstract class AbstractPeakDetectorMSD<P extends IPeak, C extends IChromatogram<P>, R> extends AbstractPeakDetector<P, C, R> implements IPeakDetectorMSD<P, C, R> {
}
