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
 * Philip Wenig - code style
 *******************************************************************************/
package org.eclipse.chemclipse.processing.supplier;

public interface IProcessExecutor {

	<X> void execute(IProcessorPreferences<X> preferences, ProcessExecutionContext context) throws Exception;
}
