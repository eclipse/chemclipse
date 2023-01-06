/*******************************************************************************
 * Copyright (c) 2015, 2023 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.ux.extension.xxd.ui.messages;

import org.eclipse.chemclipse.support.l10n.Messages;
import org.eclipse.chemclipse.ux.extension.xxd.ui.Activator;

public class ExtensionMessages implements IExtensionMessages {

	private static Messages messages;

	/**
	 * Returns the messages instance to get a translation.
	 * 
	 * @return {@link Messages}
	 */
	public static Messages INSTANCE() {

		if(messages == null) {
			messages = new Messages(Activator.getContext().getBundle());
		}
		return messages;
	}
}
