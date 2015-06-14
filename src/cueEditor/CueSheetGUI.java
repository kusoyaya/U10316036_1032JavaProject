package cueEditor;

import java.awt.FileDialog;
import java.awt.Frame;
import java.awt.Image;
import java.awt.Toolkit;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.Box;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.KeyStroke;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;

import com.apple.eawt.AboutHandler;
import com.apple.eawt.AppEvent.AboutEvent;
import com.apple.eawt.AppEvent.PreferencesEvent;
import com.apple.eawt.Application;
import com.apple.eawt.PreferencesHandler;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import java.awt.FlowLayout;
import java.awt.event.WindowAdapter;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
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
	private JButton coverNineButton;
	private JButton coverNextButton;
	private JButton coverSaveButton;
	private JButton coverReloadButton;
	private JLabel albumTitleLabel;
	private JTextField albumTitleField;
	private JLabel albumTitleFocusLabel;
	private JLabel albumPefromerLabel;
	private JTextField albumPerformerField;
	private JLabel albumPerformerFocusLabel;
	private JLabel albumFileLabel;
	private JTextField albumFileField;
	private JButton albumFileButton;
	private JPanel albumGenerateAndDatePad;
	private JLabel albumGenerateLabel;
	private JTextField albumGenerateField;
	private JLabel albumGenerateFocusLabel;
	private JLabel albumDateLabel;
	private JTextField albumDateField;
	private JLabel albumDateFocusLabel;
	private JPanel albumCommentPad;
	private JLabel albumCommentLabel;
	private JTextField albumCommentField;
	private JLabel albumCommentFocusLabel;
	private JScrollPane trackArea;
	private JTable trackTable;
	private MyTableModel trackTableModel;
	
	private int fileType = 0;
	private int coverType = 0;
	private boolean hasCover = false;
	private String fileName = "";
	private String filePath = "";
	private String fileDirectory = "";
	private String[] albumInfo;
	private Object[][] trackInfo;
	private String[] encode = {"UTF-8","Big5","GBK","Shift_JIS"};
	private boolean hasSomethingChanged = false;
	private boolean isCoverSpecific = false;
	private boolean isAskSave = true;
	private Properties properties;
	private String saveCoverName = "cover";
	private int languageNumber = 0;
	private final int IS_NO_FILE = 0;
	private final int IS_CUE = 1;
	private final int IS_ID3 = 2;
	private final int IS_GOOGLE = 1;
	private final String[][] languagePack = {
			{"Some changes haven't saved yet, are you sure to close?","Load File","Load Error","Write File","You have done something changes, are you sure to save them?","Write successful!","Write Error!","Last","Next","Save Cover","Save completed",
				"Read ID3 Cover","Album:","Album Artist:","Source File:","Connect File","Album Generate:","Album Year:","Info","Play","Reload",
				"Album Comment:","Some changes haven't saved yet, are you sure to open another file?","Unsupported cue sheet","Generate:","Year:","Comment:","Are you sure to save this cover to audio file?","Press Enter to save","Saved","Unsave yet",
				"Encode error or Cue Sheet Error\nError message: "},
			{"你做了一些改動尚未儲存，確定要關閉了嗎？","讀取檔案","讀取錯誤","存入檔案","你已經做了一些改動，確定要儲存了嗎？","寫入完成！","寫入錯誤!","上一張","下一張","儲存封面","儲存完成",
					"讀取ID3封面","專輯名稱:","專輯演出者:","來源檔案:","關聯檔案","專輯類型:","專輯年份:","簡介","播放","重新載入",
					"專輯註記:","你做了一些改動尚未儲存，確定要載入另外一個文件了嗎？","不支援此Cue檔","類型:","年份:","註記:","確定要寫入此封面到檔案嗎？","按Enter來儲存"," 已儲存","尚未儲存",
					"編碼錯誤或Cue檔格式錯誤\n錯誤訊息代碼: "}
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
			properties.load(new FileInputStream(System.getProperty("user.home")+"/Library/Application Support/simpleCueEditor/config.properties"));
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
		properties.setProperty("isAskSave", ""+"true");
		return properties;
	}

	/**
	 * Create the frame.
	 */
	public CueSheetGUI(Properties userProperties) {
		this.properties = userProperties;
		try {
			saveCoverName = java.net.URLDecoder.decode(properties.getProperty("saveCoverName"),"UTF-8");
		} catch (UnsupportedEncodingException e2) {
			saveCoverName = "cover";
		}
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
		switch(properties.getProperty("isAskSave", "true")){
		case"true":
			isAskSave = true;
			break;
		case"false":
			isAskSave = false;
			break;
		default:
			isAskSave = true;
		}
		setTitle("SimpleCueEditor v0.97");
		setResizable(false);
		setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
		addWindowListener(new WindowAdapter(){
			@Override
			public void windowClosing(WindowEvent e){
				if(hasSomethingChanged && fileType != IS_NO_FILE){
					if(JOptionPane.showConfirmDialog(contentPane, languagePack[languageNumber][0]) == JOptionPane.YES_OPTION){
						System.exit(0);
					}
				}else{
					System.exit(0);
				}
			}
		});
		setBounds(100, 100, 800, 800);
		
		setMacThing();
		
		contentPane = new JPanel();
		contentPane.setBounds(new Rectangle(0, 0, 800, 800));
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
				if(hasSomethingChanged && fileType != IS_NO_FILE){
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
				if(fileType == IS_CUE){
					try {
						cueInput = new ReadMachine(filePath,(String)encodeBox.getSelectedItem());
						fileType = IS_CUE;
						coverType = IS_GOOGLE;
						hasCover = false;
						hasSomethingChanged = true;
						
						albumInfo = cueInput.getAlbumInfo();
						trackInfo = cueInput.getTrackInfo();
						
						setTitle(getTitle()+" - "+fileName);
						setAlbumField();
						setTable();
						encodeBox.setEnabled(true);
						setLoadingGif();
						Thread loadCoverProcess = new Thread(new Runnable(){
							public void run(){
								loadCover();
							}
						});
						loadCoverProcess.start();
					} catch(CueNotSupportException e2){
						JOptionPane.showMessageDialog(contentPane, e2.getMessage(), languagePack[languageNumber][23], JOptionPane.ERROR_MESSAGE);
					}catch (Exception e1) {
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
				if(hasSomethingChanged && fileType != IS_NO_FILE){
					if(JOptionPane.showConfirmDialog(contentPane, languagePack[languageNumber][4]) == JOptionPane.YES_OPTION){
						if(fileType == IS_CUE){
							String savePath = fileDirectory+"/"+fileName;
							if(!isAskSave){
								FileDialog fileChooser = new FileDialog(new Frame(),languagePack[languageNumber][3],FileDialog.SAVE);
								fileChooser.setDirectory(fileDirectory);
								fileChooser.setModal(true);
								fileChooser.setVisible(true);
								savePath = fileChooser.getDirectory()+fileChooser.getFile();
							}
							WriteMachine wm = null;
							try {
								wm = new WriteMachine(savePath,albumInfo,trackInfo,cueInput.getAlbumNotSupport(),cueInput.isMultiFileCue());
								JOptionPane.showMessageDialog(contentPane, languagePack[languageNumber][5]);
								hasSomethingChanged = false;
							} catch (Exception e1) {
								JOptionPane.showMessageDialog(contentPane, e1.getMessage(), languagePack[languageNumber][6], JOptionPane.ERROR_MESSAGE);
							} finally{
								wm.close();
							}
						}else if(fileType == IS_ID3){
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
		albumCoverArea.add(albumCoverLabel);
		albumCoverLabel.setPreferredSize(new Dimension(300, 300));
		albumCoverLabel.setIcon(albumCoverIcon);
		albumCoverIcon.setImageObserver(albumCoverLabel);
		albumCoverLabel.addMouseListener(new MouseAdapter(){
			public void mousePressed(MouseEvent e){
				if(hasCover && SwingUtilities.isLeftMouseButton(e) && e.getClickCount() ==2 ){
					if(isCoverSpecific){
						File[] selectedFiles = null;
						
						FileDialog fileChooser = new FileDialog(new Frame(),languagePack[languageNumber][1],FileDialog.LOAD);
						fileChooser.setFilenameFilter(new FilenameFilter(){
							public boolean accept(File dir, String name){
								return (name.endsWith(".jpg") || name.endsWith(".png"));
							}
						});
						fileChooser.setMultipleMode(false);
						fileChooser.setModal(true);
						fileChooser.setVisible(true);
						selectedFiles = fileChooser.getFiles();
						if(selectedFiles.length == 1 && JOptionPane.showConfirmDialog(albumCoverLabel, languagePack[languageNumber][27]) == JOptionPane.YES_OPTION){
							try {
								TagReadMachine.writeCover(""+trackInfo[trackTable.getSelectedRows()[0]][TagReadMachine.FILE_PATH], selectedFiles[0].getAbsolutePath());
								setLoadingGif();
								Thread coverProcess = new Thread(new Runnable(){
									public void run(){
										loadCover();
										albumCoverLabel.setIcon(new ImageIcon(tagInput.getSpecificCover(true, trackTable.getSelectedRows()[0])));
									}
								});
								coverProcess.start();
							} catch (Exception e1) {
								JOptionPane.showMessageDialog(contentPane, e1.getMessage(), languagePack[languageNumber][6], JOptionPane.ERROR_MESSAGE);
							}
						}
					}else if(coverType == IS_GOOGLE){
						try{
							JOptionPane.showMessageDialog(albumCoverLabel, new ImageIcon(service.getNowAlbumCover(false)),"",JOptionPane.PLAIN_MESSAGE);
						}catch(Exception ex){
							JOptionPane.showMessageDialog(albumCoverLabel, new ImageIcon(CueSheetGUI.class.getResource("/fail.png")));
						}
					}else if(coverType == IS_ID3){
						try{
							JOptionPane.showMessageDialog(albumCoverLabel, new ImageIcon(tagInput.getNowCover(false)),"",JOptionPane.PLAIN_MESSAGE);
						}catch(Exception ex){
							JOptionPane.showMessageDialog(albumCoverLabel, new ImageIcon(CueSheetGUI.class.getResource("/fail.png")));
						}
					}
					
				}
			}
		});
		
		coverLastButton = new JButton(new ImageIcon(CueSheetGUI.class.getResource("/last.png")));
		coverLastButton.setPreferredSize(new Dimension(40, 20));
		coverLastButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				setLoadingGif();
				
				Thread coverChangeProcess = new Thread(new Runnable(){
					public void run() {
						if(hasCover){
							if(coverType == IS_GOOGLE){
								try{
									albumCoverLabel.setIcon(new ImageIcon(service.getLastAlbumCover(true)));
								}catch(Exception e){
									albumCoverLabel.setIcon(new ImageIcon(CueSheetGUI.class.getResource("/fail.png")));
								}
							}else if(coverType == IS_ID3){
								albumCoverLabel.setIcon(new ImageIcon(tagInput.getLastCover(true)));
							}
						}
					}
				});
				
				coverChangeProcess.start();
			}
		});
		albumCoverArea.add(coverLastButton);
		
		coverNineButton = new JButton(new ImageIcon(CueSheetGUI.class.getResource("/nine.png")));
		coverNineButton.setPreferredSize(new Dimension(50, 20));
		coverNineButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(hasCover && coverType == IS_GOOGLE){
					coverDialog cd = new coverDialog(languageNumber);
					try {
						BufferedImage[] covers = null;
						covers = service.getFourAlbumCover();
						
						cd.setNine(covers,fileDirectory,saveCoverName);
					} catch (Exception ex) {
						cd.setFail();
					}
					
				}
			}
		});
		albumCoverArea.add(coverNineButton);
		
		coverNextButton = new JButton(new ImageIcon(CueSheetGUI.class.getResource("/next.png")));
		coverNextButton.setPreferredSize(new Dimension(40, 20));
		coverNextButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				setLoadingGif();
				Thread coverChangeProcess = new Thread(new Runnable(){
					public void run(){
						if(hasCover){
							if(coverType == IS_GOOGLE){
								try{
									albumCoverLabel.setIcon(new ImageIcon(service.getNextAlbumCover(true)));
								}catch(Exception e){
									albumCoverLabel.setIcon(new ImageIcon(CueSheetGUI.class.getResource("/fail.png")));
								}
							}else if(coverType == IS_ID3){
								albumCoverLabel.setIcon(new ImageIcon(tagInput.getNextCover(true)));
							}
						}
					}
				});
				coverChangeProcess.start();
			}
		});
		albumCoverArea.add(coverNextButton);
		
		coverSaveButton = new JButton(new ImageIcon(CueSheetGUI.class.getResource("/download.png")));
		coverSaveButton.setPreferredSize(new Dimension(50, 20));
		coverSaveButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Thread coverSaveProcess = new Thread(new Runnable(){
					public void run(){
						if(hasCover){
							if(coverType == IS_GOOGLE){
								try {
									File ouput = new File(fileDirectory+"/"+saveCoverName+".png");
									ImageIO.write(service.getNowAlbumCover(false), "png", ouput);
									JOptionPane.showMessageDialog(contentPane, languagePack[languageNumber][5]);
								} catch (Exception e) {
									JOptionPane.showMessageDialog(contentPane, e.getMessage(), languagePack[languageNumber][6], JOptionPane.ERROR_MESSAGE);
								}
							}else if(coverType == IS_ID3){
								try {
									File ouput = new File(fileDirectory+"/"+saveCoverName+".png");
									ImageIO.write(tagInput.getNowCover(false), "png", ouput);
									JOptionPane.showMessageDialog(contentPane, languagePack[languageNumber][5]);
								} catch (Exception e) {
									JOptionPane.showMessageDialog(contentPane, e.getMessage(), languagePack[languageNumber][6], JOptionPane.ERROR_MESSAGE);
								}
							}
						}
					}
				});
				coverSaveProcess.start();
			}
		});
		albumCoverArea.add(coverSaveButton);
		
		coverReloadButton = new JButton(new ImageIcon(CueSheetGUI.class.getResource("/reload.png")));
		coverReloadButton.setPreferredSize(new Dimension(50, 20));
		coverReloadButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				setLoadingGif();
				Thread coverProcess = new Thread(new Runnable(){
					public void run(){
						loadCover();
					}
				});
				coverProcess.start();
			}
		});
		albumCoverArea.add(coverReloadButton);
		
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
				if(fileType == IS_CUE){
					albumInfo[ReadMachine.ALBUM_TITLE] = albumTitleField.getText();
				}else if(fileType == IS_ID3){
					if(isCoverSpecific){
						trackInfo[trackTable.getSelectedRows()[0]][TagReadMachine.TRACK_ALBUM_TITLE] = albumTitleField.getText();
					}else{
						for(Object[] oa : trackInfo)
							oa[TagReadMachine.TRACK_ALBUM_TITLE] = albumTitleField.getText();
					}
				}	
				albumTitleFocusLabel.setText(languagePack[languageNumber][29]);
				hasSomethingChanged = true;
				testButton.setEnabled(true);
			}
		});
		albumTitleField.addFocusListener(new FocusListener(){
			@Override
			public void focusGained(FocusEvent e) {
				albumTitleFocusLabel.setText(languagePack[languageNumber][28]);
			}

			@Override
			public void focusLost(FocusEvent e) {
				if(fileType == IS_CUE){
					if(albumTitleField.getText().equals(""+albumInfo[ReadMachine.ALBUM_TITLE])){
						albumTitleFocusLabel.setText(languagePack[languageNumber][29]);
					}else{
						albumTitleFocusLabel.setText(languagePack[languageNumber][30]);	
					}
				}else if(fileType == IS_ID3){
					if(isCoverSpecific){
						if(albumTitleField.getText().equals(""+trackInfo[trackTable.getSelectedRows()[0]][TagReadMachine.TRACK_ALBUM_TITLE])){
							albumTitleFocusLabel.setText(languagePack[languageNumber][29]);
						}else{
							albumTitleFocusLabel.setText(languagePack[languageNumber][30]);	
						}
					}else{
						for(Object[] oa :trackInfo){
							if(!albumTitleField.getText().equals(""+oa[TagReadMachine.TRACK_ALBUM_TITLE])){
								albumTitleFocusLabel.setText(languagePack[languageNumber][30]);	
								break;
							}
						albumTitleFocusLabel.setText(languagePack[languageNumber][29]);
						}
					}
				}
			}
		});
		albumTitleField.setColumns(20);
		
		albumTitleFocusLabel = new JLabel();
		albumTitleFocusLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		albumTItilePad.add(albumTitleFocusLabel);
		
		JPanel albumPerformerPad = new JPanel();
		FlowLayout flowLayout_1 = (FlowLayout) albumPerformerPad.getLayout();
		flowLayout_1.setAlignment(FlowLayout.LEFT);
		
		albumPefromerLabel = new JLabel(languagePack[languageNumber][13]);
		albumPerformerPad.add(albumPefromerLabel);
		
		albumPerformerField = new JTextField();
		albumPerformerField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(fileType == IS_CUE){
					albumInfo[ReadMachine.ALBUM_PERFORMER] = albumPerformerField.getText();
				}else if(fileType == IS_ID3){
					if(isCoverSpecific){
						trackInfo[trackTable.getSelectedRows()[0]][TagReadMachine.TRACK_ALBUM_PERFORMER] = albumPerformerField.getText();
					}else{
						for(Object[] oa : trackInfo)
							oa[TagReadMachine.TRACK_ALBUM_PERFORMER] = albumPerformerField.getText();
					}
				}	
				albumPerformerFocusLabel.setText(languagePack[languageNumber][29]);
				hasSomethingChanged = true;
				testButton.setEnabled(true);
			}
		});
		albumPerformerField.addFocusListener(new FocusListener(){
			@Override
			public void focusGained(FocusEvent e) {
				albumPerformerFocusLabel.setText(languagePack[languageNumber][28]);
			}

			@Override
			public void focusLost(FocusEvent e) {
				if(fileType == IS_CUE){
					if(albumPerformerField.getText().equals(""+albumInfo[ReadMachine.ALBUM_PERFORMER])){
						albumPerformerFocusLabel.setText(languagePack[languageNumber][29]);
					}else{
						albumPerformerFocusLabel.setText(languagePack[languageNumber][30]);	
					}
				}else if(fileType == IS_ID3){
					if(isCoverSpecific){
						if(albumPerformerField.getText().equals(""+trackInfo[trackTable.getSelectedRows()[0]][TagReadMachine.TRACK_ALBUM_PERFORMER])){
							albumPerformerFocusLabel.setText(languagePack[languageNumber][29]);
						}else{
							albumPerformerFocusLabel.setText(languagePack[languageNumber][30]);	
						}
					}else{
						for(Object[] oa :trackInfo){
							if(!albumPerformerField.getText().equals(""+oa[TagReadMachine.TRACK_ALBUM_PERFORMER])){
								albumPerformerFocusLabel.setText(languagePack[languageNumber][30]);	
								break;
							}
						albumPerformerFocusLabel.setText(languagePack[languageNumber][29]);
						}
					}
				}
			}
			
		});
		albumPerformerPad.add(albumPerformerField);
		albumPerformerField.setColumns(20);
		
		albumPerformerFocusLabel = new JLabel();
		albumPerformerFocusLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		albumPerformerPad.add(albumPerformerFocusLabel);
		
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
				if(fileType == IS_CUE){
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
						hasSomethingChanged = true;
						testButton.setEnabled(true);
					}
				}else if(fileType == IS_ID3 && !isCoverSpecific){
					if(coverType == IS_ID3){
						albumFileButton.setEnabled(false);
						coverType = IS_GOOGLE;
						hasCover = false;
						setLoadingGif();
						Thread coverProcess = new Thread(new Runnable(){
							public void run(){
								loadCover();
								albumFileButton.setEnabled(true);
								albumFileButton.setText(languagePack[languageNumber][11]);
							}
						});
						coverProcess.start();
					}else if(coverType == IS_GOOGLE){
						albumFileButton.setEnabled(false);
						coverType = IS_ID3;
						hasCover = false;
						setLoadingGif();
						Thread coverProcess = new Thread(new Runnable(){
							public void run(){
								loadCover();
								albumFileButton.setEnabled(true);
								albumFileButton.setText("Google Search");
							}
						});
						coverProcess.start();
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
				if(fileType == IS_CUE){
					albumInfo[ReadMachine.ALBUM_GENRE] = albumGenerateField.getText();
				}else if(fileType == IS_ID3){
					if(isCoverSpecific){
						trackInfo[trackTable.getSelectedRows()[0]][TagReadMachine.TRACK_GENRE] = albumGenerateField.getText();
					}else{
						for(Object[] oa : trackInfo)
							oa[TagReadMachine.TRACK_GENRE] = albumGenerateField.getText();
					}
				}	
				albumGenerateFocusLabel.setText(languagePack[languageNumber][29]);
				hasSomethingChanged = true;
				testButton.setEnabled(true);
			}
		});
		albumGenerateField.addFocusListener(new FocusListener(){
			@Override
			public void focusGained(FocusEvent e) {
				albumGenerateFocusLabel.setText(languagePack[languageNumber][28]);
			}

			@Override
			public void focusLost(FocusEvent e) {
				if(fileType == IS_CUE){
					if(albumGenerateField.getText().equals(""+albumInfo[ReadMachine.ALBUM_GENRE])){
						albumGenerateFocusLabel.setText(languagePack[languageNumber][29]);
					}else{
						albumGenerateFocusLabel.setText(languagePack[languageNumber][30]);	
					}
				}else if(fileType == IS_ID3){
					if(isCoverSpecific){
						if(albumGenerateField.getText().equals(""+trackInfo[trackTable.getSelectedRows()[0]][TagReadMachine.TRACK_GENRE])){
							albumGenerateFocusLabel.setText(languagePack[languageNumber][29]);
						}else{
							albumGenerateFocusLabel.setText(languagePack[languageNumber][30]);	
						}
					}else{
						for(Object[] oa :trackInfo){
							if(!albumGenerateField.getText().equals(""+oa[TagReadMachine.TRACK_GENRE])){
								albumGenerateFocusLabel.setText(languagePack[languageNumber][30]);	
								break;
							}
						albumGenerateFocusLabel.setText(languagePack[languageNumber][29]);
						}
					}
				}
			}
			
		});
		albumGenerateField.setColumns(10);
		albumGenerateAndDatePad.add(albumGenerateField);
		
		albumGenerateFocusLabel = new JLabel();
		albumGenerateFocusLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		albumGenerateAndDatePad.add(albumGenerateFocusLabel);
		
		JPanel albumDatePad = new JPanel();
		FlowLayout flowLayout_7 = (FlowLayout) albumDatePad.getLayout();
		flowLayout_7.setAlignment(FlowLayout.LEFT);
		
		albumDateLabel = new JLabel(languagePack[languageNumber][17]);
		albumDatePad.add(albumDateLabel);
		
		albumDateField = new JTextField();
		albumDateField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(fileType == IS_CUE){
					albumInfo[ReadMachine.ALBUM_DATE] = albumDateField.getText();
				}else if(fileType == IS_ID3){
					if(isCoverSpecific){
						trackInfo[trackTable.getSelectedRows()[0]][TagReadMachine.TRACK_DATE] = albumDateField.getText();
					}else{
						for(Object[] oa : trackInfo)
							oa[TagReadMachine.TRACK_DATE] = albumDateField.getText();
					}
				}	
				albumDateFocusLabel.setText(languagePack[languageNumber][29]);
				hasSomethingChanged = true;
				testButton.setEnabled(true);
			}
		});
		albumDateField.addFocusListener(new FocusListener(){
			@Override
			public void focusGained(FocusEvent e) {
				albumDateFocusLabel.setText(languagePack[languageNumber][28]);
			}

			@Override
			public void focusLost(FocusEvent e) {
				if(fileType == IS_CUE){
					if(albumDateField.getText().equals(""+albumInfo[ReadMachine.ALBUM_DATE])){
						albumDateFocusLabel.setText(languagePack[languageNumber][29]);
					}else{
						albumDateFocusLabel.setText(languagePack[languageNumber][30]);	
					}
				}else if(fileType == IS_ID3){
					if(isCoverSpecific){
						if(albumDateField.getText().equals(""+trackInfo[trackTable.getSelectedRows()[0]][TagReadMachine.TRACK_DATE])){
							albumDateFocusLabel.setText(languagePack[languageNumber][29]);
						}else{
							albumDateFocusLabel.setText(languagePack[languageNumber][30]);	
						}
					}else{
						for(Object[] oa :trackInfo){
							if(!albumDateField.getText().equals(""+oa[TagReadMachine.TRACK_DATE])){
								albumDateFocusLabel.setText(languagePack[languageNumber][30]);	
								break;
							}
						albumDateFocusLabel.setText(languagePack[languageNumber][29]);
						}
					}
				}
			}
			
		});
		albumDateField.setColumns(5);
		albumDatePad.add(albumDateField);
		
		albumDateFocusLabel = new JLabel();
		albumDateFocusLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		albumDatePad.add(albumDateFocusLabel);
		
		
		albumCommentPad = new JPanel();
		FlowLayout flowLayout_4 = (FlowLayout) albumCommentPad.getLayout();
		flowLayout_4.setAlignment(FlowLayout.LEFT);
		
		albumCommentLabel = new JLabel(languagePack[languageNumber][21]);
		albumCommentPad.add(albumCommentLabel);
		
		albumCommentField = new JTextField();
		albumCommentField.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(fileType == IS_CUE){
					albumInfo[ReadMachine.ALBUM_COMMENT] = albumCommentField.getText();
				}else if(fileType == IS_ID3){
					if(isCoverSpecific){
						trackInfo[trackTable.getSelectedRows()[0]][TagReadMachine.TRACK_COMMENT] = albumCommentField.getText();
					}else{
						for(Object[] oa : trackInfo)
							oa[TagReadMachine.TRACK_COMMENT] = albumCommentField.getText();
					}
				}	
				albumCommentFocusLabel.setText(languagePack[languageNumber][29]);
				hasSomethingChanged = true;
				testButton.setEnabled(true);
			}
		});
		albumCommentField.addFocusListener(new FocusListener(){
			@Override
			public void focusGained(FocusEvent e) {
				albumCommentFocusLabel.setText(languagePack[languageNumber][28]);
			}

			@Override
			public void focusLost(FocusEvent e) {
				if(fileType == IS_CUE){
					if(albumCommentField.getText().equals(""+albumInfo[ReadMachine.ALBUM_COMMENT])){
						albumCommentFocusLabel.setText(languagePack[languageNumber][29]);
					}else{
						albumCommentFocusLabel.setText(languagePack[languageNumber][30]);	
					}
				}else if(fileType == IS_ID3){
					if(isCoverSpecific){
						if(albumCommentField.getText().equals(""+trackInfo[trackTable.getSelectedRows()[0]][TagReadMachine.TRACK_COMMENT])){
							albumCommentFocusLabel.setText(languagePack[languageNumber][29]);
						}else{
							albumCommentFocusLabel.setText(languagePack[languageNumber][30]);	
						}
					}else{
						for(Object[] oa :trackInfo){
							if(!albumCommentField.getText().equals(""+oa[TagReadMachine.TRACK_COMMENT])){
								albumCommentFocusLabel.setText(languagePack[languageNumber][30]);	
								break;
							}
						albumCommentFocusLabel.setText(languagePack[languageNumber][29]);
						}
					}
				}
			}
			
		});
		albumCommentField.setColumns(15);
		albumCommentPad.add(albumCommentField);
		
		albumCommentFocusLabel = new JLabel();
		albumCommentFocusLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 10));
		albumCommentPad.add(albumCommentFocusLabel);
		
		albumInfoArea.add(Box.createGlue());
		albumInfoArea.add(albumTItilePad);
		albumInfoArea.add(albumPerformerPad);
		albumInfoArea.add(albumFilePad);
		albumInfoArea.add(albumGenerateAndDatePad);
		albumInfoArea.add(albumDatePad);
		albumInfoArea.add(albumCommentPad);
		albumInfoArea.add(Box.createGlue());
		
		trackArea = new JScrollPane();
		contentPane.add(trackArea);
		trackArea.addMouseListener(new MouseAdapter(){
			@Override
			public void mousePressed(MouseEvent e){
				if(SwingUtilities.isLeftMouseButton(e) || trackTable.getSelectedRows().length != 0){
					trackTable.getSelectionModel().clearSelection();
					if(fileType == IS_ID3 && isCoverSpecific){
						setAlbumField();
						setLoadingGif();
						Thread coverProcess = new Thread(new Runnable(){
							public void run(){
								loadCover();
							}
						});
						coverProcess.start();
						isCoverSpecific = false;
					}
				}
			}
		});
		
		trackTable = new JTable();
		trackTable.addMouseListener(new MouseAdapter() {
			@Override
			public void mousePressed(MouseEvent e) {
				if(SwingUtilities.isLeftMouseButton(e)){
					if(e.getClickCount() == 1 && fileType == IS_ID3){
						if(trackTable.getSelectedRows().length == 1){
							int selectedRow = trackTable.getSelectedRows()[0];
							isCoverSpecific = true;
							albumGenerateLabel.setText(languagePack[languageNumber][24]);
							albumDateLabel.setText(languagePack[languageNumber][25]);
							albumCommentLabel.setText(languagePack[languageNumber][26]);
							albumTitleField.setText(""+trackInfo[selectedRow][TagReadMachine.TRACK_ALBUM_TITLE]);
							albumPerformerField.setText(""+trackInfo[selectedRow][TagReadMachine.TRACK_ALBUM_PERFORMER]);
							albumFileField.setText(""+trackInfo[selectedRow][TagReadMachine.FILE_PATH]);
							albumGenerateField.setText(""+trackInfo[selectedRow][TagReadMachine.TRACK_GENRE]);
							albumDateField.setText(""+trackInfo[selectedRow][TagReadMachine.TRACK_DATE]);
							albumCommentField.setText(""+trackInfo[selectedRow][TagReadMachine.TRACK_COMMENT]);
							albumCoverLabel.setIcon(new ImageIcon(tagInput.getSpecificCover(true, selectedRow)));
						}else{
							setAlbumField();
							setLoadingGif();
							Thread coverProcess = new Thread(new Runnable(){
								public void run(){
									loadCover();
								}
							});
							coverProcess.start();
							isCoverSpecific = false;
						}
						
					}else if(e.getClickCount() == 2){
						int[] rowNumbers = {trackTable.rowAtPoint(e.getPoint())};
						trackTable.setRowSelectionInterval(rowNumbers[0], rowNumbers[rowNumbers.length-1]);
						
						if(fileType == IS_CUE){
							TrackInfoDialog td = new TrackInfoDialog(rowNumbers,albumInfo,trackInfo,languageNumber);
							if(td.hasChanged()){
								hasSomethingChanged = true;
								testButton.setEnabled(true);
								setAlbumField();
							}
						}else if(fileType == IS_ID3){
							TagTrackInfoDialog ttd = new TagTrackInfoDialog(rowNumbers,trackInfo,languageNumber);
							if(ttd.hasChanged()){
								hasSomethingChanged = true;
								testButton.setEnabled(true);
								albumInfo = TagReadMachine.getAlbum(trackInfo);
								setAlbumField();
							}
						}
					}
				}else if(SwingUtilities.isRightMouseButton(e) && e.getClickCount() == 1){
					if(trackTable.getSelectedRows().length <= 1){
						int[] rowNumber = {trackTable.rowAtPoint(e.getPoint())};
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
					fileType = IS_CUE;
					coverType = IS_GOOGLE;
					hasCover = false;
					hasSomethingChanged = false;
					
					albumInfo = cueInput.getAlbumInfo();
					trackInfo = cueInput.getTrackInfo();
					filePath = selectedFiles[0].getAbsolutePath();
					fileName = selectedFiles[0].getName();
					fileDirectory = selectedFiles[0].getParent();
					
					setTitle("SimpleCueEditor v0.97 - "+fileName);
					setAlbumField();
					setTable();
					encodeBox.setEnabled(true);
					setLoadingGif();
					Thread loadCoverProcess = new Thread(new Runnable(){
						public void run(){
							loadCover();
						}
					});
					loadCoverProcess.start();
				} catch(CueNotSupportException e2){
					JOptionPane.showMessageDialog(contentPane, languagePack[languageNumber][31]+e2.getMessage(), languagePack[languageNumber][23], JOptionPane.ERROR_MESSAGE);
				}catch (Exception e1) {
					JOptionPane.showMessageDialog(contentPane, languagePack[languageNumber][31]+e1.getMessage(), languagePack[languageNumber][2], JOptionPane.ERROR_MESSAGE);
				}
				
			}else{
				String[] paths = new String[selectedFiles.length];
				for(int i = 0; i < selectedFiles.length; i++)
					paths[i] = selectedFiles[i].getAbsolutePath();
				try {
					tagInput = new TagReadMachine(paths);
					
					fileType = IS_ID3;
					coverType = IS_ID3;
					hasCover = false;
					hasSomethingChanged = false;
					
					trackInfo = tagInput.getTrack();
					albumInfo = TagReadMachine.getAlbum(trackInfo);
					filePath = selectedFiles[0].getAbsolutePath();
					fileName = selectedFiles[0].getName();
					fileDirectory = selectedFiles[0].getParent();

					setTitle("SimpleCueEditor v0.97 - "+fileName);
					setAlbumField();
					setTagTable();
					encodeBox.setEnabled(false);
					albumFileButton.setText("Google Search");
					setLoadingGif();
					Thread loadCoverProcess = new Thread(new Runnable(){
						public void run(){
							loadCover();
						}
					});
					loadCoverProcess.start();
				} catch (Exception e1) {
					JOptionPane.showMessageDialog(contentPane, e1.getMessage(), languagePack[languageNumber][2], JOptionPane.ERROR_MESSAGE);
				}
			}
			
		}
	}
	
	private void setAlbumField(){
		albumTitleLabel.setText(languagePack[languageNumber][12]);
		albumPefromerLabel.setText(languagePack[languageNumber][13]);
		albumFileLabel.setText(languagePack[languageNumber][14]);
		albumGenerateLabel.setText(languagePack[languageNumber][16]);
		albumDateLabel.setText(languagePack[languageNumber][17]);
		albumCommentLabel.setText(languagePack[languageNumber][21]);
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
		trackTable.setPreferredScrollableViewportSize(new Dimension(600,400));
		trackTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		trackTable.setColumnSelectionAllowed(false);
		trackTable.setRowSelectionAllowed(true);
		
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
	}
	
	private void setTagTable(){
		trackTableModel = new MyTagTableModel(trackInfo,languageNumber);
		trackTable.setModel(trackTableModel);
		trackTable.setPreferredScrollableViewportSize(new Dimension(600,400));
		trackTable.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
		trackTable.setColumnSelectionAllowed(false);
		trackTable.setRowSelectionAllowed(true);
		
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
	}
	
	private void setLoadingGif(){
		albumCoverIcon.getImage().flush();
		albumCoverLabel.setIcon(albumCoverIcon);
	}
	
	private void loadCover(){
		hasCover = false;
		if(coverType == IS_GOOGLE){
			hasCover= service.hasCover(albumInfo[ReadMachine.ALBUM_PERFORMER]+albumInfo[ReadMachine.ALBUM_TITLE]);
			if(hasCover){
				try{
					albumCoverLabel.setIcon(new ImageIcon(service.getNowAlbumCover(true)));
				}catch(Exception e){
					albumCoverLabel.setIcon(new ImageIcon(CueSheetGUI.class.getResource("/fail.png")));
			}
			}else{
				albumCoverLabel.setIcon(new ImageIcon(CueSheetGUI.class.getResource("/fail.png")));
			}
		}else if(coverType == IS_ID3){
			hasCover = tagInput.setCoverArray();
			if(hasCover){
				albumCoverLabel.setIcon(new ImageIcon(tagInput.getNowCover(true)));
			}else{
				albumCoverLabel.setIcon(new ImageIcon(CueSheetGUI.class.getResource("/fail.png")));
			}		
		}
	}
	
	private void showPopUp(MouseEvent e , int[] rowNumbers){
		JPopupMenu popupMenu = new JPopupMenu();
		JMenuItem menuInfo = new JMenuItem(languagePack[languageNumber][18]);
		menuInfo.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I,Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
		menuInfo.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(fileType == IS_CUE){
					TrackInfoDialog td = new TrackInfoDialog(rowNumbers,albumInfo,trackInfo,languageNumber);
					if(td.hasChanged()){
						hasSomethingChanged = true;
						testButton.setEnabled(true);
						setAlbumField();
					}
				}else if (fileType == IS_ID3){
					TagTrackInfoDialog ttd = new TagTrackInfoDialog(rowNumbers,trackInfo,languageNumber);
					if(ttd.hasChanged()){
						hasSomethingChanged = true;
						testButton.setEnabled(true);
						albumInfo = TagReadMachine.getAlbum(trackInfo);
						setAlbumField();
					}
				}
			}
		});
		JMenuItem menuPlay = new JMenuItem(languagePack[languageNumber][19]);
		menuPlay.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				if(fileType == IS_CUE){
					String location = ""+(Integer.parseInt(""+trackInfo[rowNumbers[0]][ReadMachine.TRACK_MINUTEINDEX]) * 60 + Integer.parseInt(""+trackInfo[rowNumbers[0]][ReadMachine.TRACK_SECONDINDEX]));
					ServiceMachine.playWithAppleScript(fileDirectory+"/"+albumInfo[ReadMachine.ALBUM_FILE], location);
				}else if(fileType == IS_ID3){
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
				JOptionPane.showMessageDialog(contentPane, "Simple Cue Editor V0.9 1139\nby NasirHo @ 2015\n\nJSON :http://json.org/ \nJAudioTagger :http://www.jthink.net/jaudiotagger/", "About", JOptionPane.INFORMATION_MESSAGE);
			}
		});

		macApplication.setPreferencesHandler(new PreferencesHandler(){
			public void handlePreferences(PreferencesEvent arg0) {
				PreferenceDialog pd = new PreferenceDialog(properties);
				properties = pd.getProperties();
				setLanguageAndElse();
			}
		});

		try{
			Image icon = ImageIO.read(CueSheetGUI.class.getResourceAsStream("/icon.png"));
			macApplication.setDockIconImage(icon);
		}catch(Exception ex){
			ex.printStackTrace();
		}

	}
	
	private void setLanguageAndElse(){
		try {
			saveCoverName = java.net.URLDecoder.decode(properties.getProperty("saveCoverName"),"UTF-8");
		} catch (UnsupportedEncodingException e2) {
			saveCoverName = "cover";
		}
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
		switch(properties.getProperty("isAskSave", "true")){
		case"true":
			isAskSave = true;
			break;
		case"false":
			isAskSave = false;
			break;
		default:
			isAskSave = true;
		}
		loadButton.setText(languagePack[languageNumber][1]);
		testButton.setText(languagePack[languageNumber][3]);
		albumTitleLabel.setText(languagePack[languageNumber][12]);
		albumPefromerLabel.setText(languagePack[languageNumber][13]);
		albumFileLabel.setText(languagePack[languageNumber][14]);
		if(fileType == IS_CUE){
			albumFileButton.setText(languagePack[languageNumber][15]);
		}else if(fileType == IS_ID3){
			if(coverType == IS_GOOGLE){
				albumFileButton.setText(languagePack[languageNumber][11]);
			}else if(coverType == IS_ID3){
				albumFileButton.setText("Google Search");
			}
		}
		albumGenerateLabel.setText(languagePack[languageNumber][16]);
		albumDateLabel.setText(languagePack[languageNumber][17]);
		albumCommentLabel.setText(languagePack[languageNumber][21]);
	}
}
