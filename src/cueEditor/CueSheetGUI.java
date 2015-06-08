package cueEditor;

import java.awt.EventQueue;
import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Point;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.SwingUtilities;

import com.apple.eawt.AboutHandler;
import com.apple.eawt.AppEvent.AboutEvent;
import com.apple.eawt.AppEvent.PreferencesEvent;
import com.apple.eawt.Application;
import com.apple.eawt.PreferencesHandler;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.Properties;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.Font;

public class CueSheetGUI extends JFrame {

	private JPanel contentPane;
	private JPanel controlPad;
	private JButton loadButton;
	private JComboBox<String> encodeBox;
	private JButton testButton;
	private JPanel albumInfoArea;
	private ImageIcon albumCoverIcon;
	private JLabel albumCoverLabel;
	private JPanel albumCoverArea;
	private JButton coverLastButton;
	private JButton coverNextButton;
	private JButton coverSaveButton;
	private JLabel albumTitleLabel;
	private JTextField albumTitleField;
	private JLabel albumPefromerLabel;
	private JTextField albumPerformerField;
	private JLabel albumFileLabel;
	private JTextField albumFileField;
	private JButton albumFileButton;
	private JPanel albumGenerateAndDatePad;
	private JLabel albumGenerateLabel;
	private JTextField albumGenerateField;
	private JLabel albumDateLabel;
	private JTextField albumDateField;
	private JPanel albumCommentPad;
	private JLabel albumCommentLabel;
	private JTextField albumCommentField;
	private JScrollPane trackArea;
	private JTable trackTable;
	private MyTableModel trackTableModel;
	
	private String fileName = "";
	private String filePath = "";
	private String fileDirectory = "";
	private int fileFormat;
	private String[] albumInfo;
	private Object[][] trackInfo;
	private String[] encode = {"UTF-8","Big5","GBK","Shift JIS"};
	private boolean isCue;
	private boolean hasSomethingChanged = false;
	private boolean hasCover = false;
	private Properties properties;
	private int languageNumber = 0;
	private final String[][] languagePack = {
			{"Some changes haven't saved yet, are you sure to close?","Load File","Load Error","Write File","You have done something changes, are you sure to save them?","Write successful!","Write Error!","Last","Next","Save Cover","Save completed","Redo",
				"Album:","Album Artist:","Source File:","Connect File","Album Generate:","Album Year:","Info","Play","Reload","Album Comment:","Some changes haven't saved yet, are you sure to open another file?"},
			{"你做了一些改動尚未儲存，確定要關閉了嗎？","讀取檔案","讀取錯誤","存入檔案","你已經做了一些改動，確定要儲存了嗎？","寫入完成！","寫入錯誤!","上一張","下一張","儲存封面","儲存完成","還原",
				"專輯名稱:","專輯演出者:","來源檔案:","關聯檔案","專輯類型:","專輯年份:","簡介","播放","重新載入","專輯註記:","你做了一些改動尚未儲存，確定要載入另外一個文件了嗎？"}
			};
	
	private ReadMachine cueInput;
	private TagReadMachine tagInput;
	private ServiceMachine service = new ServiceMachine();

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		Properties properties = new Properties();
		try{
			properties.load(new FileInputStream("Config.properties"));
		}catch(Exception e){
			properties = createDefaultProperties();
		}
		CueSheetGUI frame = new CueSheetGUI(properties);
		frame.setVisible(true);	
	}
	
	public static Properties createDefaultProperties (){
		Properties properties = new Properties();
		
		properties.setProperty("language", "English");
		properties.setProperty("saveCoverName", "cover");
		return properties;
	}

	/**
	 * Create the frame.
	 */
	public CueSheetGUI(Properties userProperties) {
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
		
		setTitle("SimpleCueEditor v0.9");
		setResizable(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e){
				if(testButton.isEnabled() && trackTableModel.hasChanged())
					hasSomethingChanged = true;
				if(hasSomethingChanged){
					if(JOptionPane.showConfirmDialog(contentPane, languagePack[languageNumber][0]) == JOptionPane.YES_OPTION){
						System.exit(0);
					}
				}else{
					System.exit(0);
				}
			}
		});
		setBounds(100, 100, 800, 600);
		
		setMacThing();
		
		contentPane = new JPanel();
		contentPane.setBounds(new Rectangle(0, 0, 600, 800));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		
		controlPad = new JPanel();
		contentPane.add(controlPad);
		controlPad.setLayout(new BoxLayout(controlPad, BoxLayout.X_AXIS));
		
		controlPad.add(Box.createHorizontalStrut(10));
		
		loadButton = new JButton(languagePack[languageNumber][1]);
		controlPad.add(loadButton);
		loadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(testButton.isEnabled() && trackTableModel.hasChanged())
					hasSomethingChanged = true;
				if(hasSomethingChanged){
					if(JOptionPane.showConfirmDialog(contentPane, languagePack[languageNumber][22]) == JOptionPane.YES_OPTION)
						loadFile();
				}else{
					loadFile();
				}
			}
		});
		
		encodeBox = new JComboBox<String>();
		for(String s : encode)
			encodeBox.addItem(s);
		encodeBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(isCue){
					try {
						cueInput = new ReadMachine(filePath,(String)encodeBox.getSelectedItem());
						albumInfo = cueInput.getAlbumInfo();
						trackInfo = cueInput.getTrackInfo();
						setTable();
					} catch (Exception e1) {
						JOptionPane.showMessageDialog(contentPane, e1.getMessage(), languagePack[languageNumber][2], JOptionPane.ERROR_MESSAGE);
					}
					
				}
			}
		});
		controlPad.add(encodeBox);
		
		controlPad.add(Box.createGlue());
		
		testButton = new JButton(languagePack[languageNumber][3]);
		controlPad.add(testButton);
		testButton.setEnabled(false);
		testButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(trackTableModel.hasChanged()){
					hasSomethingChanged = true;
				}
				if(hasSomethingChanged){
					if(JOptionPane.showConfirmDialog(contentPane, languagePack[languageNumber][4]) == JOptionPane.YES_OPTION){
						if(isCue){
							WriteMachine wm = null;
							try {
								wm = new WriteMachine(fileDirectory+"/"+fileName,albumInfo,trackInfo,fileFormat);
								JOptionPane.showMessageDialog(contentPane, languagePack[languageNumber][5]);
								hasSomethingChanged = false;
							} catch (Exception e1) {
								JOptionPane.showMessageDialog(contentPane, e1.getMessage(), languagePack[languageNumber][6], JOptionPane.ERROR_MESSAGE);
							} finally{
								wm.close();
							}
						}else{
							try {
								TagReadMachine.writeTag(trackInfo);
								JOptionPane.showMessageDialog(contentPane, languagePack[languageNumber][5]);
								hasSomethingChanged = false;
							} catch (Exception e1) {
								JOptionPane.showMessageDialog(contentPane, e1.getMessage(), languagePack[languageNumber][6], JOptionPane.ERROR_MESSAGE);
							}
						}
					}
				}
			}
		});
		
		
		JPanel albumArea = new JPanel();
		albumArea.setPreferredSize(new Dimension(600, 360));
		contentPane.add(albumArea);
		
		albumArea.add(Box.createGlue());
		
		albumArea.setLayout(new BoxLayout(albumArea, BoxLayout.X_AXIS));
		albumCoverIcon = new ImageIcon(CueSheetGUI.class.getResource("/loading.gif"));
		albumCoverIcon.getImage().flush();
		
		albumCoverArea = new JPanel();
		albumCoverArea.setMaximumSize(new Dimension(300, 360));
		albumCoverArea.setPreferredSize(new Dimension(300, 360));
		albumArea.add(albumCoverArea);
		
		albumArea.add(Box.createGlue());
		
		albumCoverLabel = new JLabel();
		albumCoverLabel.setMaximumSize(new Dimension(300, 300));
		albumCoverArea.add(albumCoverLabel);
		albumCoverLabel.setPreferredSize(new Dimension(300, 300));
		albumCoverLabel.setIcon(albumCoverIcon);
		albumCoverIcon.setImageObserver(albumCoverLabel);
		
		coverLastButton = new JButton(languagePack[languageNumber][7]);
		coverLastButton.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		coverLastButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setLoadingGif();
				
				Thread coverChangeProcess = new Thread(new Runnable(){
					public void run() {
						if(isCue && hasCover){
							try{
								albumCoverLabel.setIcon(new ImageIcon(service.getLastAlbumCover(true)));
							}catch(Exception e){
								albumCoverLabel.setIcon(new ImageIcon(CueSheetGUI.class.getResource("/fail.png")));
							}
						}else if(!isCue && !hasCover){
							albumCoverLabel.setIcon(new ImageIcon(tagInput.getLastCover(true)));
						}else if(!isCue && hasCover){
							try{
								albumCoverLabel.setIcon(new ImageIcon(service.getNextAlbumCover(true)));
							}catch(Exception e){
								albumCoverLabel.setIcon(new ImageIcon(CueSheetGUI.class.getResource("/fail.png")));
							}
						}
					}
				});
				
				coverChangeProcess.start();
			}
		});
		coverLastButton.setPreferredSize(new Dimension(75, 20));
		albumCoverArea.add(coverLastButton);
		
		coverNextButton = new JButton(languagePack[languageNumber][8]);
		coverNextButton.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		coverNextButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				setLoadingGif();
				Thread coverChangeProcess = new Thread(new Runnable(){
					public void run(){
						if(isCue && hasCover){
							try{
								albumCoverLabel.setIcon(new ImageIcon(service.getNextAlbumCover(true)));
							}catch(Exception e){
								albumCoverLabel.setIcon(new ImageIcon(CueSheetGUI.class.getResource("/fail.png")));
							}
						}else if(!isCue && !hasCover){
							albumCoverLabel.setIcon(new ImageIcon(tagInput.getNextCover(true)));
						}else if(!isCue && hasCover){
							try{
								albumCoverLabel.setIcon(new ImageIcon(service.getNextAlbumCover(true)));
							}catch(Exception e){
								albumCoverLabel.setIcon(new ImageIcon(CueSheetGUI.class.getResource("/fail.png")));
							}
						}
					}
				});
				coverChangeProcess.start();
			}
		});
		coverNextButton.setPreferredSize(new Dimension(75, 20));
		albumCoverArea.add(coverNextButton);
		
		coverSaveButton = new JButton(languagePack[languageNumber][9]);
		coverSaveButton.setFont(new Font("Lucida Grande", Font.PLAIN, 11));
		coverSaveButton.setPreferredSize(new Dimension(117, 20));
		coverSaveButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Thread coverProcess = new Thread(new Runnable(){
					public void run(){
						if(isCue && hasCover){
							
								File output = null;
								try {
									output = new File(fileDirectory+"/"+java.net.URLDecoder.decode(properties.getProperty("saveCoverName"), "UTF-8")+".png");
								} catch (UnsupportedEncodingException e1) {
									output = new File(fileDirectory+"/cover.png");
								}
								try {
									ImageIO.write(service.getNowAlbumCover(false), "png", output);
									JOptionPane.showMessageDialog(contentPane, languagePack[languageNumber][10]);
								} catch (Exception e) {
									JOptionPane.showMessageDialog(contentPane, "", languagePack[languageNumber][7], JOptionPane.ERROR_MESSAGE);
								}
						}else if(!isCue && !hasCover){
							try{
								hasCover = service.hasCover(albumInfo[ReadMachine.ALBUM_PERFORMER]+" "+albumInfo[ReadMachine.ALBUM_TITLE]);
								if(hasCover){
									setLoadingGif();
									albumCoverLabel.setIcon(new ImageIcon(service.getNextAlbumCover(true)));
									coverSaveButton.setText(languagePack[languageNumber][11]);
								}
								
							}catch(Exception e){
								
							}
						}else if(!isCue && hasCover){
							hasCover = false;
							coverSaveButton.setText("Google Search");
							albumCoverLabel.setIcon(new ImageIcon(tagInput.getNextCover(true)));
						}
					}
				});
				coverProcess.start();
			}
		});
		albumCoverArea.add(coverSaveButton);
		
		albumInfoArea = new JPanel();
		albumArea.add(albumInfoArea);
		albumInfoArea.setLayout(new BoxLayout(albumInfoArea, BoxLayout.Y_AXIS));
		
		
		JPanel albumTItilePad = new JPanel();
		FlowLayout flowLayout = (FlowLayout) albumTItilePad.getLayout();
		flowLayout.setAlignment(FlowLayout.LEFT);
		
		albumTitleLabel = new JLabel(languagePack[languageNumber][12]);
		albumTItilePad.add(albumTitleLabel);
		
		albumTitleField = new JTextField();
		albumTItilePad.add(albumTitleField);
		albumTitleField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(isCue){
					albumInfo[ReadMachine.ALBUM_TITLE] = albumTitleField.getText();
				}else{
					for(Object[] oa : trackInfo)
						oa[TagReadMachine.TRACK_ALBUM_TITLE] = albumTitleField.getText();
				}
				hasSomethingChanged = true;
			}
		});
		albumTitleField.setColumns(20);
		
		JPanel albumPerformerPad = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) albumPerformerPad.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		
		albumPefromerLabel = new JLabel(languagePack[languageNumber][13]);
		albumPerformerPad.add(albumPefromerLabel);
		
		albumPerformerField = new JTextField();
		albumPerformerField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(isCue){
					albumInfo[ReadMachine.ALBUM_PERFORMER] = albumPerformerField.getText();
				}else{
					for(Object[] oa : trackInfo)
						oa[TagReadMachine.TRACK_ALBUM_PERFORMER] = albumPerformerField.getText();
				}
				hasSomethingChanged = true;
			}
		});
		albumPerformerPad.add(albumPerformerField);
		albumPerformerField.setColumns(20);
		
		JPanel albumFilePad = new JPanel();
		FlowLayout flowLayout_2 = (FlowLayout) albumFilePad.getLayout();
		flowLayout_2.setAlignment(FlowLayout.LEFT);
		
		albumFileLabel = new JLabel(languagePack[languageNumber][14]);
		albumFilePad.add(albumFileLabel);
		
		albumFileField = new JTextField();
		albumFileField.setEditable(false);
		albumFilePad.add(albumFileField);
		albumFileField.setColumns(20);
		
		albumFileButton = new JButton(languagePack[languageNumber][15]);
		albumFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(isCue){
					File[] selectedFiles = null;
					
					FileDialog fileChooser = new FileDialog(new Frame(),languagePack[languageNumber][1],FileDialog.LOAD);
					fileChooser.setFilenameFilter(new FilenameFilter(){
						public boolean accept(File dir, String name){
							return (name.endsWith(".wav") || name.endsWith(".ape") || name.endsWith(".mp3") || name.endsWith(".flac") 
									|| name.endsWith(".ogg")|| name.endsWith(".m4a")|| name.endsWith(".tak")|| name.endsWith(".tta"));
						}
					});
					fileChooser.setMultipleMode(false);
					fileChooser.setModal(true);
					fileChooser.setVisible(true);
					selectedFiles = fileChooser.getFiles();
					if(selectedFiles.length > 0 ){
						albumFileField.setText(selectedFiles[0].getName());
					}
				}
			}
		});
		albumFilePad.add(albumFileButton);
		
		
		albumGenerateAndDatePad = new JPanel();
		FlowLayout flowLayout_3 = (FlowLayout) albumGenerateAndDatePad.getLayout();
		flowLayout_3.setAlignment(FlowLayout.LEFT);
		
		albumGenerateLabel = new JLabel(languagePack[languageNumber][16]);
		albumGenerateAndDatePad.add(albumGenerateLabel);
		
		albumGenerateField = new JTextField();
		albumGenerateField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(isCue){
					albumInfo[ReadMachine.ALBUM_GENRE] = albumGenerateField.getText();
				}else{
					for(Object[] oa : trackInfo)
						oa[TagReadMachine.TRACK_GENRE] = albumGenerateField.getText();
				}
				hasSomethingChanged = true;
			}
		});
		albumGenerateField.setColumns(10);
		albumGenerateAndDatePad.add(albumGenerateField);
		
		albumDateLabel = new JLabel(languagePack[languageNumber][17]);
		albumGenerateAndDatePad.add(albumDateLabel);
		
		albumDateField = new JTextField();
		albumDateField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(isCue){
					albumInfo[ReadMachine.ALBUM_DATE] = albumDateField.getText();
				}else{
					for(Object[] oa : trackInfo)
						oa[TagReadMachine.TRACK_DATE] = albumDateField.getText();
				}
				hasSomethingChanged = true;
			}
		});
		albumDateField.setColumns(5);
		albumGenerateAndDatePad.add(albumDateField);
		
		albumCommentPad = new JPanel();
		FlowLayout flowLayout_4 = (FlowLayout) albumCommentPad.getLayout();
		flowLayout_4.setAlignment(FlowLayout.LEFT);
		
		albumCommentLabel = new JLabel(languagePack[languageNumber][21]);
		albumCommentPad.add(albumCommentLabel);
		
		albumCommentField = new JTextField();
		albumCommentField.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(isCue){
					albumInfo[ReadMachine.ALBUM_COMMENT] = albumCommentField.getText();
				}else{
					for(Object[] oa: trackInfo)
						oa[TagReadMachine.TRACK_COMMENT] = albumCommentField.getText();
				}
				hasSomethingChanged = true;
			}
		});
		albumCommentField.setColumns(15);
		albumCommentPad.add(albumCommentField);
		
		albumInfoArea.add(Box.createGlue());
		albumInfoArea.add(albumTItilePad);
		albumInfoArea.add(albumPerformerPad);
		albumInfoArea.add(albumFilePad);
		albumInfoArea.add(albumGenerateAndDatePad);
		albumInfoArea.add(albumCommentPad);
		albumInfoArea.add(Box.createGlue());
		
		trackArea = new JScrollPane();
		contentPane.add(trackArea);
		
		trackTable = new JTable();
		trackTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(SwingUtilities.isRightMouseButton(e)){
					if(trackTable.getSelectedRows().length == 0){
						Point p = e.getPoint();
						
						int[] rowNumber = {trackTable.rowAtPoint(p)};
						
						trackTable.setRowSelectionInterval(rowNumber[0], rowNumber[rowNumber.length-1]);
					}
					showPopUp(e,trackTable.getSelectedRows());
				}
			}
		});
		trackArea.setViewportView(trackTable);
		
		
	}
	
	private void loadFile(){
		File[] selectedFiles = null;
		
		FileDialog fileChooser = new FileDialog(new Frame(),languagePack[languageNumber][1],FileDialog.LOAD);
		fileChooser.setFilenameFilter(new FilenameFilter(){
			public boolean accept(File dir, String name){
				return (name.endsWith(".cue") || name.endsWith(".m4a") || name.endsWith(".mp3") || name.endsWith(".flac") || name.endsWith(".ogg"));
			}
		});
		fileChooser.setMultipleMode(true);
		fileChooser.setModal(true);
		fileChooser.setVisible(true);
		selectedFiles = fileChooser.getFiles();
		if(selectedFiles.length > 0){
			if(selectedFiles[0].getName().split("\\.")[selectedFiles[0].getName().split("\\.").length-1].equalsIgnoreCase("cue")){
				try {
					cueInput = new ReadMachine(selectedFiles[0].getAbsolutePath(),(String)encodeBox.getSelectedItem());
					isCue = true;
					encodeBox.setEnabled(true);
					testButton.setEnabled(true);
					albumFileButton.setEnabled(true);
					hasSomethingChanged = false;
					
					filePath = selectedFiles[0].getAbsolutePath();
					fileName = selectedFiles[0].getName();
					fileDirectory = selectedFiles[0].getParent();
					
					albumInfo = cueInput.getAlbumInfo();
					trackInfo = cueInput.getTrackInfo();
					setTitle(getTitle()+" - "+fileName);
					setAlbumField();
					setTable();
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(contentPane, e1.getMessage(), languagePack[languageNumber][2], JOptionPane.ERROR_MESSAGE);
				}
				
			}else{
				String[] paths = new String[selectedFiles.length];
				for(int i = 0; i < selectedFiles.length; i++)
					paths[i] = selectedFiles[i].getAbsolutePath();
				try {
					tagInput = new TagReadMachine(paths);
					isCue = false;
					hasSomethingChanged = false;
					testButton.setEnabled(true);
					trackInfo = tagInput.getTrack();
					albumInfo = TagReadMachine.getAlbum(trackInfo);
					setAlbumField();
					setTagTable();
					encodeBox.setEnabled(false);
					albumFileButton.setEnabled(false);
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(contentPane, e1.getMessage(), languagePack[languageNumber][2], JOptionPane.ERROR_MESSAGE);
					e1.printStackTrace();
				}
			}
			
		}
	}
	
	private void setAlbumField(){
		albumTitleField.setText(albumInfo[ReadMachine.ALBUM_TITLE]);
		albumPerformerField.setText(albumInfo[ReadMachine.ALBUM_PERFORMER]);
		albumFileField.setText(albumInfo[ReadMachine.ALBUM_FILE]);
		albumGenerateField.setText(albumInfo[ReadMachine.ALBUM_GENRE]);
		albumDateField.setText(albumInfo[ReadMachine.ALBUM_DATE]);
		albumCommentField.setText(albumInfo[ReadMachine.ALBUM_COMMENT]);
	}

	private void setTable(){
		trackTableModel = new MyTableModel(trackInfo,languageNumber);
		trackTable.setModel(trackTableModel);
		trackTable.setPreferredScrollableViewportSize(new Dimension(600,300));
		
		trackTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		trackTable.getColumnModel().getColumn(ReadMachine.TRACK_ORDER).setMaxWidth(50);
		trackTable.getColumnModel().getColumn(ReadMachine.TRACK_MINUTEINDEX).setMaxWidth(70);
		trackTable.getColumnModel().getColumn(ReadMachine.TRACK_SECONDINDEX).setMaxWidth(70);
		trackTable.getColumnModel().getColumn(ReadMachine.TRACK_FRAMEINDEX).setMaxWidth(70); //設定一些 column 的寬度
		
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment( JLabel.CENTER );
		trackTable.getColumnModel().getColumn(ReadMachine.TRACK_TITLE).setCellRenderer(centerRenderer);
		trackTable.getColumnModel().getColumn(ReadMachine.TRACK_PERFORMER).setCellRenderer(centerRenderer);
		centerRenderer = (DefaultTableCellRenderer)trackTable.getTableHeader().getDefaultRenderer();
		centerRenderer.setHorizontalAlignment( JLabel.CENTER );
		trackTable.getTableHeader().setDefaultRenderer(centerRenderer); //設定column文字顯示置中
	    
		trackArea.setViewportView(trackTable);
		
		coverSaveButton.setText("save");
		
		setLoadingGif();
		
		Thread getCoverProcess = new Thread(new Runnable(){
			public void run() {
				hasCover= service.hasCover(albumInfo[ReadMachine.ALBUM_PERFORMER]+albumInfo[ReadMachine.ALBUM_TITLE]);
				if(hasCover)
					try{
						albumCoverLabel.setIcon(new ImageIcon(service.getNextAlbumCover(true)));
					}catch(Exception e){
						albumCoverLabel.setIcon(new ImageIcon(CueSheetGUI.class.getResource("/fail.png")));
					}
			}
		});
		
		getCoverProcess.start();
		
	}
	
	private void setTagTable(){
		trackTableModel = new MyTagTableModel(trackInfo,languageNumber);
		trackTable.setModel(trackTableModel);
		trackTable.setPreferredScrollableViewportSize(new Dimension(600,300));
		
		trackTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		trackTable.getColumnModel().getColumn(TagReadMachine.TRACK_ORDER).setMaxWidth(50);
		trackTable.getColumnModel().getColumn(TagReadMachine.TRACK_TOTAL).setMaxWidth(50);
		trackTable.getColumnModel().getColumn(TagReadMachine.TRACK_MINUTE).setMaxWidth(60);
		trackTable.getColumnModel().getColumn(TagReadMachine.TRACK_SECOND).setMaxWidth(60);
		trackTable.getColumnModel().getColumn(TagReadMachine.TRACK_RATE).setMaxWidth(60);
		
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment( JLabel.CENTER );
		trackTable.getColumnModel().getColumn(TagReadMachine.TRACK_TITLE).setCellRenderer(centerRenderer);
		trackTable.getColumnModel().getColumn(TagReadMachine.TRACK_PERFORMER).setCellRenderer(centerRenderer);
		trackTable.getColumnModel().getColumn(TagReadMachine.TRACK_COMPOSER).setCellRenderer(centerRenderer);
		centerRenderer = (DefaultTableCellRenderer)trackTable.getTableHeader().getDefaultRenderer();
		centerRenderer.setHorizontalAlignment( JLabel.CENTER );
		trackTable.getTableHeader().setDefaultRenderer(centerRenderer);
		
		trackArea.setViewportView(trackTable);
		
		coverSaveButton.setText("Google Search");
		
		setLoadingGif();
		
		Thread getCoverProcess = new Thread(new Runnable(){
			public void run() {
				tagInput.setCoverArray();
				albumCoverLabel.setIcon(new ImageIcon(tagInput.getNextCover(true)));
			}
		});
		
		getCoverProcess.start();
		
	}
	
	private void showPopUp(MouseEvent e , int[] rowNumbers){
		JPopupMenu popupMenu = new JPopupMenu();
		JMenuItem menuInfo = new JMenuItem(languagePack[languageNumber][18]);
		menuInfo.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(isCue){
					TrackInfoDialog td = new TrackInfoDialog(rowNumbers,albumInfo,trackInfo,languageNumber);
					if(td.hasChanged())
						hasSomethingChanged = true;
				}else{
					TagTrackInfoDialog ttd = new TagTrackInfoDialog(rowNumbers,trackInfo,languageNumber);
					if(ttd.hasChanged())
						hasSomethingChanged = true;
					albumInfo = TagReadMachine.getAlbum(trackInfo);
				}
				setAlbumField();
			}
		});
		JMenuItem menuPlay = new JMenuItem(languagePack[languageNumber][19]);
		menuPlay.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(isCue){
					String location = ""+(Integer.parseInt(""+trackInfo[rowNumbers[0]][ReadMachine.TRACK_MINUTEINDEX]) * 60 + Integer.parseInt(""+trackInfo[rowNumbers[0]][ReadMachine.TRACK_SECONDINDEX]));
					ServiceMachine.playWithAppleScript(fileDirectory+"/"+albumInfo[ReadMachine.ALBUM_FILE], location);
				}else{
					ServiceMachine.playWithAppleScript(""+trackInfo[rowNumbers[0]][TagReadMachine.FILE_PATH], "0");
				}
			}
		});
		JMenuItem menuReset = new JMenuItem(languagePack[languageNumber][20]);
		menuReset.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				JOptionPane.showMessageDialog(contentPane, "Coming sooooooon~");
			}
		});
		popupMenu.add(menuInfo);
		if(rowNumbers.length == 1)
			popupMenu.add(menuPlay);
		popupMenu.add(menuReset);
		
		if(e.isPopupTrigger()){
			popupMenu.show(e.getComponent(), e.getX(), e.getY());
		}
	}
	
	private void setMacThing(){
		Application macApplication = Application.getApplication();
		 
		macApplication.setAboutHandler(new AboutHandler() {
			public void handleAbout(AboutEvent e) {
				JOptionPane.showMessageDialog(contentPane, "Simple Cue Sheet Editor \nby NasirHo", "About", JOptionPane.INFORMATION_MESSAGE);
			}
		});

		macApplication.setPreferencesHandler(new PreferencesHandler(){
			public void handlePreferences(PreferencesEvent arg0) {
				PreferenceDialog pd = new PreferenceDialog(properties);
				properties = pd.getProperties();
				setLanguage();
			}
		});

		try{
			Image icon = ImageIO.read(CueSheetGUI.class.getResourceAsStream("/icon.png"));
			macApplication.setDockIconImage(icon);
		}catch(Exception ex){
			ex.printStackTrace();
		}

	}
	
	private void setLoadingGif(){
		albumCoverLabel.setIcon(albumCoverIcon);
		albumCoverIcon.getImage().flush();
	}
	
	private void setLanguage(){
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
		loadButton.setText(languagePack[languageNumber][1]);
		testButton.setText(languagePack[languageNumber][3]);
		coverLastButton.setText(languagePack[languageNumber][7]);
		coverNextButton.setText(languagePack[languageNumber][8]);
		coverSaveButton.setText(languagePack[languageNumber][9]);
		albumTitleLabel.setText(languagePack[languageNumber][12]);
		albumPefromerLabel.setText(languagePack[languageNumber][13]);
		albumFileLabel.setText(languagePack[languageNumber][14]);
		albumFileButton.setText(languagePack[languageNumber][15]);
		albumGenerateLabel.setText(languagePack[languageNumber][16]);
		albumDateLabel.setText(languagePack[languageNumber][17]);
		albumCommentLabel.setText(languagePack[languageNumber][21]);
	}
}
