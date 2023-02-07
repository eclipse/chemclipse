/*******************************************************************************
 * Copyright (c) 2023 Lablicate GmbH.
 *
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 * Matthias Mailänder - initial API and implementation
 *******************************************************************************/
package org.eclipse.chemclipse.support.l10n;

import java.util.Locale;

import org.eclipse.chemclipse.support.Activator;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.translation.TranslationProviderFactory;
import org.eclipse.e4.core.services.translation.TranslationService;

public class TranslationSupport {

	public static TranslationService getTranslationService() {

		IEclipseContext context = Activator.getDefault().getEclipseContext();
		TranslationService service = context.get(TranslationService.class);
		if(service == null) {
			if(context.get(TranslationService.LOCALE) == null) {
				context.set(TranslationService.LOCALE, Locale.getDefault());
			}
			service = TranslationProviderFactory.bundleTranslationService(context);
			context.set(TranslationService.class, service);
		}
		return service;
	}
}
