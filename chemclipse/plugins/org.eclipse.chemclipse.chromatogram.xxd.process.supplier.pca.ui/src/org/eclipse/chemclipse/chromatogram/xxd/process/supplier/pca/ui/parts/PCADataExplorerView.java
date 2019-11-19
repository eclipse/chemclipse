package org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.parts;

import java.util.Collections;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.eclipse.chemclipse.chromatogram.xxd.process.supplier.pca.ui.Activator;
import org.eclipse.chemclipse.model.types.DataType;
import org.eclipse.chemclipse.ux.extension.ui.swt.DataExplorerUI;
import org.eclipse.chemclipse.ux.extension.xxd.ui.part.support.SupplierEditorSupport;
import org.eclipse.e4.core.contexts.IEclipseContext;
import org.eclipse.e4.core.services.events.IEventBroker;
import org.eclipse.swt.widgets.Composite;

public class PCADataExplorerView {

	private DataExplorerUI dataExplorerUI;
	@Inject
	private IEventBroker broker;
	@Inject
	IEclipseContext context;

	@Inject
	public PCADataExplorerView() {
	}

	@PostConstruct
	public void postConstruct(Composite parent) {

		dataExplorerUI = new DataExplorerUI(parent, broker, Activator.getDefault().getPreferenceStore());
		dataExplorerUI.setSupplierFileIdentifier(Collections.singletonList(new SupplierEditorSupport(DataType.MSD, () -> context)));
		dataExplorerUI.expandLastDirectoryPath();
	}
}