/*******************************************************************************
 * Copyright (c) 2019, 2020 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Christoph Läubrich - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.ui.editors;

import org.eclipse.jface.resource.ImageDescriptor;

public interface EditorDescriptor {

	/**
	 * 
	 * @return the {@link ImageDescriptor} for this editor or <code>null</code> if the default should be used
	 */
	default ImageDescriptor getImageDescriptor() {

		return null;
	}
}
