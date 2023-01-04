/*******************************************************************************
 * Copyright (c) 2020, 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.methods;

import org.eclipse.chemclipse.model.methods.ListProcessEntryContainer;
import org.eclipse.chemclipse.processing.methods.IProcessEntry;
import org.eclipse.chemclipse.processing.methods.ProcessEntryContainer;

public class MethodSupport {

	/**
	 * 
	 * @param object
	 * @return the {@link ListProcessEntryContainer} for this object if available and not read-only, <code>null</code> otherwise
	 */
	public static final ListProcessEntryContainer getContainer(Object object) {

		if(object instanceof IProcessEntry processEntry) {
			ProcessEntryContainer parent = processEntry.getParent();
			if(parent instanceof ListProcessEntryContainer container) {
				if(!container.isReadOnly()) {
					return container;
				}
			}
		}
		return null;
	}
}
