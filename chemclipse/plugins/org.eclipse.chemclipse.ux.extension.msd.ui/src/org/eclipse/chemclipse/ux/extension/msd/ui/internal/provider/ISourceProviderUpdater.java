/*******************************************************************************
 * Copyright (c) 2008, 2016 Philip (eselmeister) Wenig.
 * 
 * All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Philip (eselmeister) Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.msd.ui.internal.provider;

import org.eclipse.ui.AbstractSourceProvider;

import org.eclipse.chemclipse.msd.model.notifier.IUpdater;

public interface ISourceProviderUpdater extends IUpdater {

	/**
	 * Sets the parent source provider.
	 * 
	 * @param parent
	 */
	void setParent(AbstractSourceProvider parent);
}
