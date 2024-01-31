/*******************************************************************************
 * Copyright (c) 2019, 2024 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 * Philip Wenig - comments
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.part.support;

import jakarta.inject.Singleton;

import org.eclipse.chemclipse.model.core.IMeasurementResult;
import org.eclipse.e4.core.di.annotations.Creatable;

/*
 * This class is used in the MeasurementResultsPart to retrieve the ICustomPaintListener, e.g. to draw the analysis segments in the chromatogram.
 */
@Creatable
@Singleton
public class MeasurementResultNotification extends AbstractNotifications<IMeasurementResult<?>> {
}
