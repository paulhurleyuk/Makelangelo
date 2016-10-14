package com.marginallyclever.makelangelo.preferences;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.prefs.BackingStoreException;
import java.util.prefs.InvalidPreferencesFormatException;
import java.util.prefs.Preferences;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import com.marginallyclever.gcode.GCodeFile;
import com.marginallyclever.makelangelo.Log;
import com.marginallyclever.makelangelo.Makelangelo;
import com.marginallyclever.makelangelo.Translator;
import com.marginallyclever.util.PreferencesHelper;

public class MakelangeloAppPreferences implements ActionListener {
	transient private Makelangelo app;
	
	transient private JPanel panel; 
	transient private JButton buttonExport;
	transient private JButton buttonImport;
	transient private JButton buttonReset;
	
	public MakelangeloAppPreferences(Makelangelo arg0) {
		app=arg0;
	}
	
	public void run() {
		panel = new JPanel(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();  
		c.gridx=0;
		c.gridy=0;
		c.gridwidth=1;
		c.anchor=GridBagConstraints.WEST;

		buttonImport = new JButton(Translator.get("Import"));
		panel.add(buttonImport,c);
		c.gridx++;

		buttonExport = new JButton(Translator.get("Export"));
		panel.add(buttonExport,c);
		c.gridx++;

		buttonReset = new JButton(Translator.get("Reset"));
		panel.add(buttonReset,c);
		c.gridx++;

		c.gridy=1;
		c.gridx=0;
		c.gridwidth=3;
		
		JTabbedPane pane = new JTabbedPane();
		pane.setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
		panel.add(pane,c);
		pane.add(Translator.get("MenuSoundsTitle"), SoundPreferences.buildPanel());
		pane.add(Translator.get("MenuGraphicsTitle"), GFXPreferences.buildPanel());
		pane.add(Translator.get("MenuLanguageTitle"), LanguagePreferences.buildPanel());

		
		int result = JOptionPane.showConfirmDialog(app.getMainFrame(), panel, Translator.get("MenuPreferences"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
		if (result == JOptionPane.OK_OPTION) {
			SoundPreferences.save();
			GFXPreferences.save();
			LanguagePreferences.save();
			app.getRobot().getControlPanel().updateButtonAccess();
			// refresh look & feel
			GCodeFile f = app.getRobot().getGCode();
			if( f != null ) f.emptyNodeBuffer();
		} else {
			SoundPreferences.cancel();
			GFXPreferences.cancel();
			LanguagePreferences.cancel();
		}
	}


	@Override
	public void actionPerformed(ActionEvent e) {
		Object subject = e.getSource();
		if (subject == buttonExport) exportPreferences();
		if (subject == buttonImport) importPreferences();
		if (subject == buttonReset) resetPreferences();
	}
	
	@SuppressWarnings("deprecation")
	private void exportPreferences() {
		final JFileChooser fc = new JFileChooser();
		int returnVal = fc.showSaveDialog(app.getMainFrame());
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			final File file = fc.getSelectedFile();
			try (final OutputStream fileOutputStream = new FileOutputStream(file)) {
				Preferences prefs = PreferencesHelper.getPreferenceNode(PreferencesHelper.MakelangeloPreferenceKey.LEGACY_MAKELANGELO_ROOT);
				prefs.exportSubtree(fileOutputStream);
			} catch (IOException | BackingStoreException pe) {
				Log.error(pe.getMessage());
			}
		}
	}
	

	@SuppressWarnings("deprecation")
	private void importPreferences() {
		final JFileChooser fc = new JFileChooser();
		int returnVal = fc.showOpenDialog(app.getMainFrame());
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			final File file = fc.getSelectedFile();
			try (final InputStream fileInputStream = new FileInputStream(file)) {
				Preferences prefs = PreferencesHelper.getPreferenceNode(PreferencesHelper.MakelangeloPreferenceKey.LEGACY_MAKELANGELO_ROOT);
				prefs.flush();
				Preferences.importPreferences(fileInputStream);
				prefs.flush();
			} catch (IOException | InvalidPreferencesFormatException | BackingStoreException pe) {
				Log.error(pe.getMessage());
			}
		}
	}
	

	@SuppressWarnings("deprecation")
	private void resetPreferences() {
		int dialogResult = JOptionPane.showConfirmDialog(app.getMainFrame(), Translator.get("MenuResetMachinePreferencesWarning"), Translator.get("MenuResetMachinePreferencesWarningHeader"), JOptionPane.YES_NO_OPTION);
		if(dialogResult == JOptionPane.YES_OPTION){
			try {
				Preferences prefs = PreferencesHelper.getPreferenceNode(PreferencesHelper.MakelangeloPreferenceKey.LEGACY_MAKELANGELO_ROOT);
				prefs.removeNode();
				Preferences.userRoot().flush();
			} catch (BackingStoreException e1) {
				Log.error(e1.getMessage());
			}
		}
	}
}
