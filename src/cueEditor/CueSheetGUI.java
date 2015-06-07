package cueEditor;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.Image;
import java.awt.Point;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.AbstractTableModel;
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
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;

public class CueSheetGUI extends JFrame {

	private JPanel contentPane;
	private JButton loadButton;
	private JComboBox<String> encodeBox;
	private ReadMachine cueInput;
	private TagReadMachine tagInput;
	private ServiceMachine service = new ServiceMachine();
	private JTextField albumTitleField;
	private JTextField albumPerformerField;
	private JTextField albumFileField;
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
	private JButton testButton;
	private JPanel albumGeneratePad;
	private JLabel albumGenerateLabel;
	private JTextField albumGenerateField;
	private JPanel controlPad;
	private JPanel albumDatePad;
	private JLabel albumDateLabel;
	private JTextField albumDateField;
	private JButton albumFileButton;
	private boolean isCue;
	private boolean hasSomethingChanged = false;
	private boolean hasCover = false;
	private JPanel albumInfoArea;
	private ImageIcon albumCoverIcon;
	private JLabel albumCoverLabel;
	private JPanel albumCoverArea;
	private JButton coverLastButton;
	private JButton coverNextButton;
	private JButton coverSaveButton;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					CueSheetGUI frame = new CueSheetGUI();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public CueSheetGUI() {
		setTitle("SimpleCueEditor v0.7");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
		
		loadButton = new JButton("讀取檔案");
		controlPad.add(loadButton);
		
		encodeBox = new JComboBox<String>();
		encodeBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(isCue){
					cueInput = new ReadMachine(filePath,(String)encodeBox.getSelectedItem());
					albumInfo = cueInput.getAlbumInfo();
					trackInfo = cueInput.getTrackInfo();
					fileFormat = cueInput.getAudioFormat();
					setTable();
				}
			}
		});
		controlPad.add(encodeBox);
		
		controlPad.add(Box.createGlue());
		
		testButton = new JButton("存入檔案");
		controlPad.add(testButton);
		testButton.setEnabled(false);
		testButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(trackTableModel.hasChanged()){
					hasSomethingChanged = true;
				}
				if(hasSomethingChanged){
					if(JOptionPane.showConfirmDialog(contentPane, "你已經做了一些改動，確定要儲存了嗎？") == JOptionPane.YES_OPTION){
						if(isCue){
							new WriteMachine(fileDirectory+"/"+fileName,albumInfo,trackInfo,fileFormat);
						}else{
							TagReadMachine.writeTag(trackInfo);
						}
					}
				}
			}
		});
		
		loadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File[] selectedFiles = null;
				
				JFileChooser fileChooser = new JFileChooser();
				FileFilter filterCue = new FileNameExtensionFilter("Cue Sheet","cue");
				FileFilter filterTag = new FileNameExtensionFilter("Audio File","m4a","mp3","flac","ogg");
				fileChooser.addChoosableFileFilter(filterCue);
				fileChooser.addChoosableFileFilter(filterTag);
				fileChooser.setAcceptAllFileFilterUsed(false);
				fileChooser.setMultiSelectionEnabled(true);
				int result = fileChooser.showOpenDialog(loadButton);
				if (result == JFileChooser.APPROVE_OPTION) {
					selectedFiles = fileChooser.getSelectedFiles();
				}
				if(selectedFiles[0] != null){
					if(selectedFiles[0].getName().split("\\.")[selectedFiles[0].getName().split("\\.").length-1].equalsIgnoreCase("cue")){
						isCue = true;
						encodeBox.setEnabled(true);
						testButton.setEnabled(true);
						cueInput = new ReadMachine(selectedFiles[0].getAbsolutePath(),(String)encodeBox.getSelectedItem());
						filePath = selectedFiles[0].getAbsolutePath();
						fileName = selectedFiles[0].getName();
						fileDirectory = selectedFiles[0].getParent();
						setTitle(getTitle()+" - "+selectedFiles[0].getName());
						albumInfo = cueInput.getAlbumInfo();
						trackInfo = cueInput.getTrackInfo();
						fileFormat = cueInput.getAudioFormat();
						setAlbumField();
						setTable();
					}else{
						isCue = false;
						testButton.setEnabled(true);
						String[] paths = new String[selectedFiles.length];
						for(int i = 0; i < selectedFiles.length; i++)
							paths[i] = selectedFiles[i].getAbsolutePath();
						tagInput = new TagReadMachine(paths);
						albumInfo = tagInput.getAlbum();
						trackInfo = tagInput.getTrack();
						setAlbumField();
						setTagTable();
						encodeBox.setEnabled(false);
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
		
		albumCoverLabel = new JLabel("");
		albumCoverLabel.setMaximumSize(new Dimension(300, 300));
		albumCoverArea.add(albumCoverLabel);
		albumCoverLabel.setPreferredSize(new Dimension(300, 300));
		albumCoverLabel.setIcon(albumCoverIcon);
		albumCoverIcon.setImageObserver(albumCoverLabel);
		
		coverLastButton = new JButton("last");
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
		
		coverNextButton = new JButton("next");
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
		
		coverSaveButton = new JButton("save");
		coverSaveButton.setPreferredSize(new Dimension(117, 20));
		coverSaveButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e){
				Thread coverProcess = new Thread(new Runnable(){
					public void run(){
						if(isCue && hasCover){
							try{
								File output = new File(fileDirectory+"/cover.png");
								ImageIO.write(service.getNowAlbumCover(false), "png", output);
								JOptionPane.showMessageDialog(contentPane, "儲存完成");
							}catch(Exception e){
								JOptionPane.showMessageDialog(contentPane, "寫入錯誤", "發生錯誤", JOptionPane.ERROR_MESSAGE);
							}
						}else if(!isCue && !hasCover){
							try{
								hasCover = service.hasCover(albumInfo[ReadMachine.ALBUM_PERFORMER]+" "+albumInfo[ReadMachine.ALBUM_TITLE]);
								if(hasCover){
									setLoadingGif();
									albumCoverLabel.setIcon(new ImageIcon(service.getNextAlbumCover(true)));
									coverSaveButton.setText("還原");
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
		
		JLabel albumTitleLabel = new JLabel("專輯名稱:");
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
		
		JLabel albumPefromerLabel = new JLabel("專輯演出者:");
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
		
		JLabel albumFileLabel = new JLabel("來源檔案:");
		albumFilePad.add(albumFileLabel);
		
		albumFileField = new JTextField();
		albumFileField.setEditable(false);
		albumFilePad.add(albumFileField);
		albumFileField.setColumns(20);
		
		albumFileButton = new JButton("關聯檔案");
		albumFileButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(isCue){
					File selectedFile = null;
					
					JFileChooser fileChooser = new JFileChooser();
					FileFilter filter = new FileNameExtensionFilter("Audio File","wav","ape","m4a","mp3","tak","tta");
					fileChooser.setFileFilter(filter);
					int result = fileChooser.showOpenDialog(loadButton);
					if (result == JFileChooser.APPROVE_OPTION) {
						selectedFile = fileChooser.getSelectedFile();
					}
					if(selectedFile != null){
						fileFormat = ReadMachine.getAudioFormat(selectedFile.getName());
						albumFileField.setText(selectedFile.getName());
					}
				}
			}
		});
		albumFilePad.add(albumFileButton);
		
		
		albumGeneratePad = new JPanel();
		FlowLayout flowLayout_3 = (FlowLayout) albumGeneratePad.getLayout();
		flowLayout_3.setAlignment(FlowLayout.LEFT);
		
		albumGenerateLabel = new JLabel("專輯類型:");
		albumGeneratePad.add(albumGenerateLabel);
		
		albumGenerateField = new JTextField();
		albumGenerateField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(isCue){
					albumInfo[ReadMachine.ALBUM_GENRE] = albumGenerateField.getText();
				}else{
					for(Object[] oa : trackInfo)
						oa[TagReadMachine.TRACK_GENRE] = albumGenerateField.getText();
				}
			}
		});
		albumGenerateField.setColumns(10);
		albumGeneratePad.add(albumGenerateField);
		
		albumDatePad = new JPanel();
		FlowLayout flowLayout_4 = (FlowLayout) albumDatePad.getLayout();
		flowLayout_4.setAlignment(FlowLayout.LEFT);
		
		albumDateLabel = new JLabel("專輯年份:");
		albumDatePad.add(albumDateLabel);
		
		albumDateField = new JTextField();
		albumDateField.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(isCue){
					albumInfo[ReadMachine.ALBUM_DATE] = albumDateField.getText();
				}else{
					for(Object[] oa : trackInfo)
						oa[TagReadMachine.TRACK_DATE] = albumDateField.getText();
				}
			}
		});
		albumDateField.setColumns(5);
		albumDatePad.add(albumDateField);
		
		for(String s : encode)
			encodeBox.addItem(s);
		
		albumInfoArea.add(Box.createGlue());
		albumInfoArea.add(albumTItilePad);
		albumInfoArea.add(albumPerformerPad);
		albumInfoArea.add(albumFilePad);
		albumInfoArea.add(albumGeneratePad);
		albumInfoArea.add(albumDatePad);
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
						
						System.out.println(rowNumber);
						
						showPopUp(e,rowNumber);
					}
					showPopUp(e,trackTable.getSelectedRows());
				}
			}
		});
		trackArea.setViewportView(trackTable);
		
		
	}
	
	private void setAlbumField(){
		albumTitleField.setText(albumInfo[ReadMachine.ALBUM_TITLE]);
		albumPerformerField.setText(albumInfo[ReadMachine.ALBUM_PERFORMER]);
		albumFileField.setText(albumInfo[ReadMachine.ALBUM_FILE]);
		albumGenerateField.setText(albumInfo[ReadMachine.ALBUM_GENRE]);
		albumDateField.setText(albumInfo[ReadMachine.ALBUM_DATE]);
	}

	private void setTable(){
		trackTableModel = new MyTableModel(trackInfo);
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
		trackTableModel = new MyTagTableModel(trackInfo);
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
		JMenuItem menuInfo = new JMenuItem("簡介");
		menuInfo.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				if(isCue){
					TrackInfoDialog td = new TrackInfoDialog(rowNumbers,albumInfo,trackInfo);
					if(td.hasChanged())
						hasSomethingChanged = true;
				}else{
					TagTrackInfoDialog ttd = new TagTrackInfoDialog(rowNumbers,trackInfo);
					if(ttd.hasChanged())
						hasSomethingChanged = true;
					albumInfo = TagReadMachine.getAlbum(trackInfo);
				}
				setAlbumField();
			}
		});
		JMenuItem menuPlay = new JMenuItem("播放");
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
		JMenuItem menuReset = new JMenuItem("重新載入");
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
				JOptionPane.showMessageDialog(null, "Simple Cue Sheet Editor \nby NasirHo", "About", JOptionPane.INFORMATION_MESSAGE);
			}
		});

		macApplication.setPreferencesHandler(new PreferencesHandler(){
			public void handlePreferences(PreferencesEvent arg0) {
				
		
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
	
	public void someThingChanged(){
		this.hasSomethingChanged = true;
	}
	
	
}
