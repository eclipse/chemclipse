/*******************************************************************************
 * Copyright (c) 2018, 2026 Lablicate GmbH.
 *
 * This program and the accompanying materials are made
 * available under the terms of the Eclipse Public License 2.0
 * which is available at https://www.eclipse.org/legal/epl-2.0/
 *
 * SPDX-License-Identifier: EPL-2.0
 * 
 * Contributors:
 * Philip Wenig - initial API and implementation
 * Christoph Läubrich - add even number value
 * Matthias Mailänder - add even/odd validation
 *******************************************************************************/
package org.eclipse.chemclipse.support.settings;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.fasterxml.jackson.annotation.JacksonAnnotation;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@JacksonAnnotation
public @interface IntSettingsProperty {

	int step() default 1;

	int minValue() default Integer.MIN_VALUE;

	int maxValue() default Integer.MAX_VALUE;

	Validation validation() default Validation.NONE;

	boolean useSpinner() default false;

	public enum Validation {
		ODD_NUMBER_INCLUDING_ZERO, ODD_NUMBER, EVEN_NUMBER, NONE;
	}
}
