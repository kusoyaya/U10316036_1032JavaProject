package cueEditor;

import java.awt.BorderLayout;
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

public class TrackInfoDialog extends JDialog {

	private final JPanel infoPanel = new JPanel();
	private JTextField trackTitleField;
	private boolean isTrackTitleChanged = false;
	private JTextField trackPerformerField;
	private boolean isTrackPerformerChanged = false;
	private JTextField albumTitleField;
	private JTextField albumPerformerField;
	private JTextField albumGenerateField;
	private JTextField albumDateField;
	private int[] rowsNumber;
	private String[] albumInfo;
	private Object[][] trackInfo;
	

	

	/**
	 * Create the dialog.
	 */
	public TrackInfoDialog(int[] rowsNumber, String[] albumInfo,Object[][] trackInfo) {
		this.rowsNumber = rowsNumber;
		this.albumInfo = albumInfo;
		this.trackInfo = trackInfo;
		go();
		setVisible(true);
	}

	private void go(){
		setBounds(100, 100, 450, 300);
		getContentPane().setLayout(new BorderLayout());
		infoPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(infoPanel, BorderLayout.CENTER);
		infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
		
		JPanel trackTitlePad = new JPanel();
		infoPanel.add(trackTitlePad);
			
		JLabel trackTitleLabel = new JLabel("歌曲名稱:");
		trackTitlePad.add(trackTitleLabel);
			
			
		trackTitleField = new JTextField();
		trackTitlePad.add(trackTitleField);
		trackTitleField.setColumns(10);
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
			
		JLabel trackPerformerLabel = new JLabel("歌曲演出者:");
		trackPerformerPad.add(trackPerformerLabel);
			
			
		trackPerformerField = new JTextField();
		trackPerformerField.setColumns(10);
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
			
		JLabel albumTitleLabel = new JLabel("專輯名稱:");
		albumTitlePad.add(albumTitleLabel);
			
			
		albumTitleField = new JTextField();
		albumTitleField.setColumns(10);
		albumTitlePad.add(albumTitleField);
		albumTitleField.setText(""+showSomething(ReadMachine.ALBUM_TITLE,true));
		
		
		JPanel albumPerformerPad = new JPanel();
		infoPanel.add(albumPerformerPad);
			
		JLabel albumPerformerLabel = new JLabel("專輯演出者:");
		albumPerformerPad.add(albumPerformerLabel);
			
			
		albumPerformerField = new JTextField();
		albumPerformerField.setColumns(10);
		albumPerformerPad.add(albumPerformerField);
		albumPerformerField.setText(""+showSomething(ReadMachine.ALBUM_PERFORMER,true));
		
		
		JPanel albumOtherPad = new JPanel();
		infoPanel.add(albumOtherPad);
		
		JLabel albumGenerateLabel = new JLabel("專輯類型:");
		albumOtherPad.add(albumGenerateLabel);
		
		albumGenerateField = new JTextField();
		albumGenerateField.setColumns(5);
		albumOtherPad.add(albumGenerateField);
		albumGenerateField.setText(""+showSomething(ReadMachine.ALBUM_GENRE,true));
		
		JLabel albumDateLabel = new JLabel("專輯年份:");
		albumOtherPad.add(albumDateLabel);
		
		albumDateField = new JTextField();
		albumDateField.setColumns(5);
		albumOtherPad.add(albumDateField);
		albumDateField.setText(""+showSomething(ReadMachine.ALBUM_DATE,true));
		
		
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
			}
		});
		okButton.setActionCommand("OK");
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
			
			
		JButton cancelButton = new JButton("Cancel");
		cancelButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
			}
		});
		cancelButton.setActionCommand("Cancel");
		buttonPane.add(cancelButton);
			
	}
	
	private Object showSomething(int thingsYouWant, boolean albumOrNot){
		Object result = null;
		
		if(rowsNumber.length != 1){
			if(albumOrNot){
				result = albumInfo[thingsYouWant];
			}else{
				result = trackInfo[rowsNumber[0]][thingsYouWant];
				for(int i = 0; i < rowsNumber.length-1;i++){
					if(!trackInfo[rowsNumber[i]][thingsYouWant].equals(result)){
						result = "[multi]";
						break;
					}
				}
			}
		}else{
			if(albumOrNot){
				result = albumInfo[thingsYouWant];
			}else{
				result = trackInfo[rowsNumber[0]][thingsYouWant];
			}
		}
		
		return result;
	}
}
