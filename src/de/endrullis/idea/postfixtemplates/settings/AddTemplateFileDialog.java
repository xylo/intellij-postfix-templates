package de.endrullis.idea.postfixtemplates.settings;

import com.intellij.openapi.fileChooser.FileChooser;
import com.intellij.openapi.fileChooser.FileChooserDescriptor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class AddTemplateFileDialog extends JDialog {
	private JPanel                 contentPane;
	private JButton                buttonOK;
	private JButton                buttonCancel;
	private JComboBox<CptFileType> typeField;
	private JTextField             urlField;
	private JButton                fileChooserButton;

	public AddTemplateFileDialog(Frame owner) {
		super(owner);

		setContentPane(contentPane);
		setModal(true);
		getRootPane().setDefaultButton(buttonOK);

		typeField.setModel(new DefaultComboBoxModel<>(CptFileType.values()));

		fileChooserButton.addActionListener(e -> onChooseFile());
		buttonOK.addActionListener(e -> onOK());
		buttonCancel.addActionListener(e -> onCancel());

		// call onCancel() when cross is clicked
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				onCancel();
			}
		});

		// call onCancel() on ESCAPE
		contentPane.registerKeyboardAction(e -> onCancel(), KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
	}

	private void onChooseFile() {
		FileChooser.chooseFile(new FileChooserDescriptor(true, false, false, false, false, false), null, null);
	}

	private void onOK() {
		// add your code here
		dispose();
	}

	private void onCancel() {
		// add your code here if necessary
		dispose();
	}

	public static void main(String[] args) {
		AddTemplateFileDialog dialog = new AddTemplateFileDialog(null);
		dialog.pack();
		dialog.setVisible(true);
		System.exit(0);
	}

}
