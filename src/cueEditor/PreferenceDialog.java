package cueEditor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.util.Properties;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.JCheckBox;
import javax.swing.JTextField;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class PreferenceDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private Properties properties;
	private JTextField coverSaveField;
	private String[] language = {"English","正體中文"};
	private int languageNumber = 0;
	private final String[][] languagePack = {
			{"Preference","Language:","Save Artwork named as "},
			{"偏好設定","語言:","將專輯封面儲存為 "}
	};

	
	public PreferenceDialog(Properties userProperties) {
		this.properties = userProperties;
		switch(properties.getProperty("language")){
		case"English":
			languageNumber = 0;
			break;
		case"Chinese":
			languageNumber = 1;
			break;
		default:
			languageNumber = 0;
		}
		
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		setAlwaysOnTop(true);
		setResizable(false);
		setTitle(languagePack[languageNumber][0]);
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new BoxLayout(contentPanel, BoxLayout.Y_AXIS));
		
		contentPanel.add(Box.createGlue());
		
		JPanel languagePanel = new JPanel();
		contentPanel.add(languagePanel);
		
		JLabel languageLabel = new JLabel(languagePack[languageNumber][1]);
		languagePanel.add(languageLabel);
		
		JComboBox<String> languageComboBox = new JComboBox<String>();
		for(String s : language)
			languageComboBox.addItem(s);
		languageComboBox.setSelectedIndex(languageNumber);
		languagePanel.add(languageComboBox);
		
		JPanel coverSavePanel = new JPanel();
		contentPanel.add(coverSavePanel);
		
		JLabel coverSaveLabel = new JLabel(languagePack[languageNumber][2]);
		coverSavePanel.add(coverSaveLabel);
		
		coverSaveField = new JTextField();
		coverSavePanel.add(coverSaveField);
		try {
			coverSaveField.setText(java.net.URLDecoder.decode(properties.getProperty("saveCoverName"), "UTF-8"));
		} catch (UnsupportedEncodingException e2) {
			coverSaveField.setText(properties.getProperty("saveCoverName","cover"));
		}
		coverSaveField.setColumns(10);
		
		JLabel coverSaveLabelB = new JLabel(".png");
		coverSavePanel.add(coverSaveLabelB);
		
		contentPanel.add(Box.createGlue());
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
			
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				switch(languageComboBox.getSelectedIndex()){
				case 0:
					properties.setProperty("language", "English");
					break;
				case 1:
					properties.setProperty("language", "Chinese");
					break;
				}
				try {
					properties.setProperty("saveCoverName", java.net.URLEncoder.encode(coverSaveField.getText(), "UTF-8"));
				} catch (UnsupportedEncodingException e1) {
					properties.setProperty("saveCoverName", "cover");
				}
				try {
					properties.store(new FileOutputStream("Config.properties"), "SimpleCue Config");
				} catch (IOException e1) {
					e1.printStackTrace();
				}
				setVisible(false);
				dispose();
			}
		});
		okButton.setActionCommand("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
			
			
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);
		
		setModal(true);
		setVisible(true);
	}
	
	public Properties getProperties(){
		return properties;
	}

}
