package cueEditor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;

public class TagTrackInfoDialog extends JDialog {

	private final JPanel infoPanel = new JPanel();
	private JTextField trackTitleField;
	private boolean isTrackTitleChanged = false;
	private JTextField trackPerformerField;
	private boolean isTrackPerformerChanged = false;
	private JTextField albumTitleField;
	private boolean isAlbumTitleChanged = false;
	private JTextField albumPerformerField;
	private boolean isAlbumPerformerChanged = false;
	private JTextField trackGenerateField;
	private boolean isTrackGenerateChanged = false;
	private JTextField trackDateField;
	private boolean isTrackDateChanged = false;
	private JTextField trackOrderField;
	private boolean isTrackOrderChanged = false;
	private JTextField trackTotalField;
	private boolean isTrackTotalChanged = false;
	private int[] rowsNumber;
	private Object[][] trackInfo;
	private int languageNumber = 0;
	private final String[][] languagePack = {
			{"Title:","Artist:","Album:","Album Artist:","Generate:","Year:","No.","Info"},
			{"歌曲名稱:","歌曲演出者:","專輯名稱:","專輯演出者:","類型:","年份:","音軌","簡介"}
	};
	

	

	/**
	 * Create the dialog.
	 */
	public TagTrackInfoDialog(int[] rowsNumber, Object[][] trackInfo, int languageNumber) {
		this.rowsNumber = rowsNumber;
		this.trackInfo = trackInfo;
		this.languageNumber = languageNumber;
		go();
		setModal(true);
		setVisible(true);
	}

	private void go(){
		setTitle(languagePack[languageNumber][7]);
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
		trackTitleField.setText(""+showSomething(TagReadMachine.TRACK_TITLE));
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
		trackPerformerField.setText(""+showSomething(TagReadMachine.TRACK_PERFORMER));
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
		albumTitleField.setText(""+showSomething(TagReadMachine.TRACK_ALBUM_TITLE));
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
		albumPerformerField.setText(""+showSomething(TagReadMachine.TRACK_ALBUM_PERFORMER));
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
		FlowLayout flowLayout = (FlowLayout) albumOtherPad.getLayout();
		infoPanel.add(albumOtherPad);
		
		JLabel trackGenerateLabel = new JLabel(languagePack[languageNumber][4]);
		albumOtherPad.add(trackGenerateLabel);
		
		trackGenerateField = new JTextField();
		trackGenerateField.setColumns(9);
		albumOtherPad.add(trackGenerateField);
		trackGenerateField.setText(""+showSomething(TagReadMachine.TRACK_GENRE));
		trackGenerateField.getDocument().addDocumentListener(new DocumentListener(){
			public void changedUpdate(DocumentEvent e) {
				isTrackGenerateChanged = true;
			}
			public void removeUpdate(DocumentEvent e) {
				isTrackGenerateChanged = true;
			}
			public void insertUpdate(DocumentEvent e) {
				isTrackGenerateChanged = true;
			}
		});
		
		JLabel trackDateLabel = new JLabel(languagePack[languageNumber][5]);
		albumOtherPad.add(trackDateLabel);
		
		trackDateField = new JTextField();
		trackDateField.setColumns(5);
		albumOtherPad.add(trackDateField);
		trackDateField.setText(""+showSomething(TagReadMachine.TRACK_DATE));
		trackDateField.getDocument().addDocumentListener(new DocumentListener(){
			public void changedUpdate(DocumentEvent e) {
				isTrackDateChanged = true;
			}
			public void removeUpdate(DocumentEvent e) {
				isTrackDateChanged = true;
			}
			public void insertUpdate(DocumentEvent e) {
				isTrackDateChanged = true;
			}
		});
		
		JLabel trackOrderLabel = new JLabel(languagePack[languageNumber][6]);
		albumOtherPad.add(trackOrderLabel);
		
		trackOrderField = new JTextField();
		albumOtherPad.add(trackOrderField);
		trackOrderField.setColumns(3);
		trackOrderField.setText(""+showSomething(TagReadMachine.TRACK_ORDER));
		trackOrderField.getDocument().addDocumentListener(new DocumentListener(){
			public void changedUpdate(DocumentEvent e) {
				isTrackOrderChanged = true;
			}
			public void removeUpdate(DocumentEvent e) {
				isTrackOrderChanged = true;
			}
			public void insertUpdate(DocumentEvent e) {
				isTrackOrderChanged = true;
			}
		});
		
		JLabel trackTotalLabel = new JLabel("/");
		albumOtherPad.add(trackTotalLabel);
		
		trackTotalField = new JTextField();
		albumOtherPad.add(trackTotalField);
		trackTotalField.setColumns(3);
		trackTotalField.setText(""+showSomething(TagReadMachine.TRACK_TOTAL));
		trackTotalField.getDocument().addDocumentListener(new DocumentListener(){
			public void changedUpdate(DocumentEvent e) {
				isTrackTotalChanged = true;
			}
			public void removeUpdate(DocumentEvent e) {
				isTrackTotalChanged = true;
			}
			public void insertUpdate(DocumentEvent e) {
				isTrackTotalChanged = true;
			}
		});
		
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
			
		JButton okButton = new JButton("OK");
		okButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(isTrackTitleChanged){
					for(int i : rowsNumber)
						trackInfo[i][TagReadMachine.TRACK_TITLE] = trackTitleField.getText();
				}
				if(isTrackPerformerChanged){
					for(int i : rowsNumber)
						trackInfo[i][TagReadMachine.TRACK_PERFORMER] = trackPerformerField.getText();
				}
				if(isAlbumTitleChanged){
					for(int i : rowsNumber)
						trackInfo[i][TagReadMachine.TRACK_ALBUM_TITLE] = albumTitleField.getText();
				}
				if(isAlbumPerformerChanged){
					for(int i : rowsNumber)
						trackInfo[i][TagReadMachine.TRACK_ALBUM_PERFORMER] = albumPerformerField.getText();
				}
				if(isTrackGenerateChanged){
					for(int i : rowsNumber)
						trackInfo[i][TagReadMachine.TRACK_GENRE] = trackGenerateField.getText();
				}
				if(isTrackDateChanged){
					for(int i : rowsNumber)
						trackInfo[i][TagReadMachine.TRACK_DATE] = trackDateField.getText();
				}
				if(isTrackOrderChanged){
					for(int i : rowsNumber)
						trackInfo[i][TagReadMachine.TRACK_ORDER] = trackOrderField.getText();
				}
				if(isTrackTotalChanged){
					for(int i : rowsNumber)
						trackInfo[i][TagReadMachine.TRACK_TOTAL] = trackTotalField.getText();
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
	
	private Object showSomething(int thingsYouWant){
		Object result = trackInfo[rowsNumber[0]][thingsYouWant];
		
		for(int i = 1; i < rowsNumber.length;i++){
			if(! (""+trackInfo[rowsNumber[i]][thingsYouWant]).equalsIgnoreCase(""+result)){
				result = "[multi]";
				break;
			}
		}
		
		return result;
	}
	
	public boolean hasChanged(){
		boolean result = false;
		
		if(isTrackTitleChanged || isTrackPerformerChanged || isAlbumTitleChanged || isAlbumPerformerChanged || isTrackGenerateChanged || isTrackDateChanged || isTrackOrderChanged || isTrackTotalChanged )
			result = true;
		
		return result;
	}
}
