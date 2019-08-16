/*******************************************************************************
 * Copyright (c) 2019 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.ui.swt.columns;

import java.util.Collection;

/**
 * Interface for column definitions providing items
 * 
 * @author Christoph Läubrich
 *
 */
public interface ColumnDefinitionProvider {

	Collection<? extends ColumnDefinition<?, ?>> getColumnDefinitions();
}
