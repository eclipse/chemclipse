/*******************************************************************************
 * Copyright (c) 2015, 2018 Lablicate GmbH.
 * 
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Dr. Philip Wenig - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.messages;

import org.eclipse.chemclipse.support.Activator;
import org.eclipse.chemclipse.support.l10n.Messages;

public class SupportMessages implements ISupportMessages {

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
