package cueEditor;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.FlowLayout;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JTextField;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.lang.reflect.InvocationTargetException;

public class TrackInfoDialog extends JDialog {

	private final JPanel infoPanel = new JPanel();
	private JTextField trackTitleField;
	private boolean isTrackTitleChanged = false;
	private JTextField trackPerformerField;
	private boolean isTrackPerformerChanged = false;
	private JTextField albumTitleField;
	private boolean isAlbumTitleChanged = false;
	private JTextField albumPerformerField;
	private boolean isAlbumPerformerChanged = false;
	private JTextField albumGenerateField;
	private boolean isAlbumGenerateChanged = false;
	private JTextField albumDateField;
	private boolean isAlbumDateChanged = false;
	private int[] rowsNumber;
	private String[] albumInfo;
	private Object[][] trackInfo;
	private int languageNumber = 0;
	private final String[][] languagePack = {
			{"Title:","Artist:","Album:","Album Artist:","Album Generate:","Album Year:","Info"},
			{"歌曲名稱:","歌曲演出者:","專輯名稱:","專輯演出者:","專輯類型:","專輯年份:","簡介"}
	};

	

	/**
	 * Create the dialog.
	 */
	public TrackInfoDialog(int[] rowsNumber, String[] albumInfo,Object[][] trackInfo,int languageNumber) {
		this.rowsNumber = rowsNumber;
		this.albumInfo = albumInfo;
		this.trackInfo = trackInfo;
		this.languageNumber = languageNumber;
		go();
		setModal(true);
		setVisible(true);
	}

	private void go(){
		setTitle(languagePack[languageNumber][6]);
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		infoPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(infoPanel, BorderLayout.CENTER);
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
		
		JPanel trackTitlePad = new JPanel();
		infoPanel.add(trackTitlePad);
			
		JLabel trackTitleLabel = new JLabel(languagePack[languageNumber][0]);
		trackTitlePad.add(trackTitleLabel);
			
			
		trackTitleField = new JTextField();
		trackTitlePad.add(trackTitleField);
		trackTitleField.setColumns(20);
		trackTitleField.setText(""+showSomething(ReadMachine.TRACK_TITLE,false));
		trackTitleField.getDocument().addDocumentListener(new DocumentListener(){
			public void changedUpdate(DocumentEvent e) {
				isTrackTitleChanged = true;
			}
			public void removeUpdate(DocumentEvent e) {
				isTrackTitleChanged = true;
			}
			public void insertUpdate(DocumentEvent e) {
				isTrackTitleChanged = true;
			}
		});
		
		
		JPanel trackPerformerPad = new JPanel();
		infoPanel.add(trackPerformerPad);
			
		JLabel trackPerformerLabel = new JLabel(languagePack[languageNumber][1]);
		trackPerformerPad.add(trackPerformerLabel);
			
			
		trackPerformerField = new JTextField();
		trackPerformerField.setColumns(20);
		trackPerformerPad.add(trackPerformerField);
		trackPerformerField.setText(""+showSomething(ReadMachine.TRACK_PERFORMER,false));
		trackPerformerField.getDocument().addDocumentListener(new DocumentListener(){
			public void changedUpdate(DocumentEvent e) {
				isTrackPerformerChanged = true;
			}
			public void removeUpdate(DocumentEvent e) {
				isTrackPerformerChanged = true;
			}
			public void insertUpdate(DocumentEvent e) {
				isTrackPerformerChanged = true;
			}
		});
		
		
		JPanel albumTitlePad = new JPanel();
		infoPanel.add(albumTitlePad);
			
		JLabel albumTitleLabel = new JLabel(languagePack[languageNumber][2]);
		albumTitlePad.add(albumTitleLabel);
			
			
		albumTitleField = new JTextField();
		albumTitleField.setColumns(20);
		albumTitlePad.add(albumTitleField);
		albumTitleField.setText(""+showSomething(ReadMachine.ALBUM_TITLE,true));
		albumTitleField.getDocument().addDocumentListener(new DocumentListener(){
			public void changedUpdate(DocumentEvent e) {
				isAlbumTitleChanged = true;
			}
			public void removeUpdate(DocumentEvent e) {
				isAlbumTitleChanged = true;
			}
			public void insertUpdate(DocumentEvent e) {
				isAlbumTitleChanged = true;
			}
		});
		
		
		JPanel albumPerformerPad = new JPanel();
		infoPanel.add(albumPerformerPad);
			
		JLabel albumPerformerLabel = new JLabel(languagePack[languageNumber][3]);
		albumPerformerPad.add(albumPerformerLabel);
			
			
		albumPerformerField = new JTextField();
		albumPerformerField.setColumns(20);
		albumPerformerPad.add(albumPerformerField);
		albumPerformerField.setText(""+showSomething(ReadMachine.ALBUM_PERFORMER,true));
		albumPerformerField.getDocument().addDocumentListener(new DocumentListener(){
			public void changedUpdate(DocumentEvent e) {
				isAlbumPerformerChanged = true;
			}
			public void removeUpdate(DocumentEvent e) {
				isAlbumPerformerChanged = true;
			}
			public void insertUpdate(DocumentEvent e) {
				isAlbumPerformerChanged = true;
			}
		});
		
		
		JPanel albumOtherPad = new JPanel();
		infoPanel.add(albumOtherPad);
		
		JLabel albumGenerateLabel = new JLabel(languagePack[languageNumber][4]);
		albumOtherPad.add(albumGenerateLabel);
		
		albumGenerateField = new JTextField();
		albumGenerateField.setColumns(10);
		albumOtherPad.add(albumGenerateField);
		albumGenerateField.setText(""+showSomething(ReadMachine.ALBUM_GENRE,true));
		albumGenerateField.getDocument().addDocumentListener(new DocumentListener(){
			public void changedUpdate(DocumentEvent e) {
				isAlbumGenerateChanged = true;
			}
			public void removeUpdate(DocumentEvent e) {
				isAlbumGenerateChanged = true;
			}
			public void insertUpdate(DocumentEvent e) {
				isAlbumGenerateChanged = true;
			}
		});
		
		JLabel albumDateLabel = new JLabel(languagePack[languageNumber][5]);
		albumOtherPad.add(albumDateLabel);
		
		albumDateField = new JTextField();
		albumDateField.setColumns(5);
		albumOtherPad.add(albumDateField);
		albumDateField.setText(""+showSomething(ReadMachine.ALBUM_DATE,true));
		albumDateField.getDocument().addDocumentListener(new DocumentListener(){
			public void changedUpdate(DocumentEvent e) {
				isAlbumDateChanged = true;
			}
			public void removeUpdate(DocumentEvent e) {
				isAlbumDateChanged = true;
			}
			public void insertUpdate(DocumentEvent e) {
				isAlbumDateChanged = true;
			}
		});
		
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
			
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				albumInfo[ReadMachine.ALBUM_TITLE] = albumTitleField.getText();
				albumInfo[ReadMachine.ALBUM_PERFORMER] = albumPerformerField.getText();
				albumInfo[ReadMachine.ALBUM_GENRE] = albumGenerateField.getText();
				albumInfo[ReadMachine.ALBUM_DATE] = albumDateField.getText();
				if(isTrackTitleChanged){
					for(int i : rowsNumber)
						trackInfo[i][ReadMachine.TRACK_TITLE] = trackTitleField.getText();
				}
				if(isTrackPerformerChanged){
					for(int i : rowsNumber)
						trackInfo[i][ReadMachine.TRACK_PERFORMER] = trackPerformerField.getText();
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
			
	}
	
	private Object showSomething(int thingsYouWant, boolean albumOrNot){
		Object result = "";
		
		if(albumOrNot){
			result = albumInfo[thingsYouWant];
		}else{
			result = trackInfo[rowsNumber[0]][thingsYouWant];
			for(int i = 1; i < rowsNumber.length;i++){
				if(! (""+trackInfo[rowsNumber[i]][thingsYouWant]).equalsIgnoreCase(""+result)){
					result = "[multi]";
					break;
				}
			}
		}
		
		return result;
	}
	
	public boolean hasChanged(){
		boolean result = false;
		
		if(isTrackTitleChanged || isTrackPerformerChanged || isAlbumTitleChanged || isAlbumPerformerChanged || isAlbumGenerateChanged || isAlbumDateChanged)
			result = true;
		return result;
	}
}
