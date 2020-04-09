/*******************************************************************************
 * Copyright (c) 2012, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 * Alexander Kerner - Generics
 * Christoph Läubrich - fix generic
 *******************************************************************************/
package org.eclipse.chemclipse.converter.chromatogram;

import org.eclipse.chemclipse.converter.core.AbstractImportConverter;
import org.eclipse.chemclipse.model.core.IChromatogram;

public abstract class AbstractChromatogramImportConverter<R extends IChromatogram<?>> extends AbstractImportConverter implements IChromatogramImportConverter<R> {
}
