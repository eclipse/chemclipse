package org.eclipse.chemclipse.ux.extension.xxd.ui.handlers;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.function.BiFunction;

import javax.inject.Named;

import org.eclipse.chemclipse.model.methods.ProcessMethod;
import org.eclipse.chemclipse.processing.DataCategory;
import org.eclipse.chemclipse.processing.methods.IProcessMethod;
import org.eclipse.chemclipse.rcp.app.ui.handlers.OpenSnippetHandler;
import org.eclipse.chemclipse.ux.extension.xxd.ui.editors.ProcessMethodEditor;
import org.eclipse.chemclipse.ux.extension.xxd.ui.wizards.ProcessMethodTypeSelectionWizard;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.di.annotations.Execute;
import org.eclipse.e4.ui.model.application.ui.basic.MPart;
import org.eclipse.e4.ui.services.IServiceConstants;
import org.eclipse.swt.widgets.Shell;

public class CreateProcessMethodHandler {

	@Execute
	public void execute(@Named(IServiceConstants.ACTIVE_SHELL) Shell shell, IEclipseContext eclipseContext) {

		DataCategory[] categories = ProcessMethodTypeSelectionWizard.open(shell);
		OpenSnippetHandler.openSnippet(ProcessMethodEditor.SNIPPET_ID, eclipseContext, new BiFunction<IEclipseContext, MPart, Runnable>() {

			@Override
			public Runnable apply(IEclipseContext context, MPart part) {

				context.set(IProcessMethod.class, new ProcessMethod(EnumSet.copyOf(Arrays.asList(categories))));
				return null;
			}
		});
	}
}