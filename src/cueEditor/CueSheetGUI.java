package cueEditor;

import java.awt.BorderLayout;
import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.JScrollPane;
import javax.swing.JTable;

import java.awt.Dimension;
import java.awt.Rectangle;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.BoxLayout;
import javax.swing.JInternalFrame;
import javax.swing.JToolBar;
import javax.swing.JButton;
import javax.swing.JComboBox;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.io.File;

public class CueSheetGUI extends JFrame {

	private JPanel contentPane;
	private JButton loadButton;
	private JComboBox<String> encodeBox;
	private ReadMachine input;
	private JTextField albumTitleField;
	private JTextField albumPerformerField;
	private JTextField albumFileField;
	private JScrollPane trackArea;
	private JTable trackTable;
	private MyTableModel trackTableModel;
	private String filePath = "";
	private String[] albumInfo;
	private Object[][] trackInfo;
	private String[] row = {"曲序","歌名","演出者","分鐘","秒數","幀數"};
	private String[] encode = {"UTF-8","Big5","GBK","Shift JIS"};
	private boolean isLoadFile = false;
	private JButton testButton;

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
		setTitle("SimpleCueEditor v0.1");
		setResizable(false);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 600);
		contentPane = new JPanel();
		contentPane.setBounds(new Rectangle(0, 0, 600, 800));
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		
		JPanel albumArea = new JPanel();
		contentPane.add(albumArea);
		
		loadButton = new JButton("讀取檔案");
		loadButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				File selectedFile = null;
				
				JFileChooser fileChooser = new JFileChooser();
				FileFilter filter = new FileNameExtensionFilter("Cue Sheet","cue");
				fileChooser.setFileFilter(filter);
				int result = fileChooser.showOpenDialog(loadButton);
				if (result == JFileChooser.APPROVE_OPTION) {
					selectedFile = fileChooser.getSelectedFile();
				}
				if(selectedFile != null){
					input = new ReadMachine(selectedFile.getAbsolutePath(),(String)encodeBox.getSelectedItem());
					isLoadFile = true;
					filePath = selectedFile.getAbsolutePath();
					albumInfo = input.getAlbumInfo();
					trackInfo = input.getTrackInfo();
					setTable();
				}
			}
		});
		albumArea.add(loadButton);
		
		encodeBox = new JComboBox<String>();
		encodeBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				if(isLoadFile){
					input = new ReadMachine(filePath,(String)encodeBox.getSelectedItem());
					albumInfo = input.getAlbumInfo();
					trackInfo = input.getTrackInfo();
					setTable();
				}
			}
		});
		for(String s : encode)
			encodeBox.addItem(s);
		albumArea.add(encodeBox);
		
		JPanel albumTItilePad = new JPanel();
		albumArea.add(albumTItilePad);
		
		JLabel albumTitleLabel = new JLabel("專輯名稱:");
		albumTItilePad.add(albumTitleLabel);
		
		albumTitleField = new JTextField();
		albumTItilePad.add(albumTitleField);
		albumTitleField.setColumns(20);
		
		JPanel albumPerformerPad = new JPanel();
		albumArea.add(albumPerformerPad);
		
		JLabel albumPefromerLabel = new JLabel("專輯演出者:");
		albumPerformerPad.add(albumPefromerLabel);
		
		albumPerformerField = new JTextField();
		albumPerformerPad.add(albumPerformerField);
		albumPerformerField.setColumns(20);
		
		JPanel albumFilePad = new JPanel();
		albumArea.add(albumFilePad);
		
		JLabel albumFileLabel = new JLabel("源檔案");
		albumFilePad.add(albumFileLabel);
		
		albumFileField = new JTextField();
		albumFilePad.add(albumFileField);
		albumFileField.setColumns(20);
		
		testButton = new JButton("Just for Test");
		testButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				WriteMachine testWrite = new WriteMachine("/Users/nasirho/Desktop/testWrite.cue",albumInfo,trackInfo);
				
			}
		});
		albumArea.add(testButton);
		
		trackArea = new JScrollPane();
		contentPane.add(trackArea);
		
		trackTable = new JTable();
		trackArea.setViewportView(trackTable);
	}

	public void setTable(){
		albumTitleField.setText(albumInfo[0]);
		albumPerformerField.setText(albumInfo[1]);
		albumFileField.setText(albumInfo[2]);
		trackTableModel = new MyTableModel(trackInfo);
		trackTable.setModel(trackTableModel);
		trackTable.setPreferredScrollableViewportSize(new Dimension(600,400));
		
		trackTable.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
		trackTable.getColumnModel().getColumn(0).setMaxWidth(50);
		trackTable.getColumnModel().getColumn(3).setMaxWidth(70);
		trackTable.getColumnModel().getColumn(4).setMaxWidth(70);
		trackTable.getColumnModel().getColumn(5).setMaxWidth(70);
		
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment( JLabel.CENTER );
		trackTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);
		trackTable.getColumnModel().getColumn(2).setCellRenderer(centerRenderer);
		centerRenderer = (DefaultTableCellRenderer)trackTable.getTableHeader().getDefaultRenderer();
		centerRenderer.setHorizontalAlignment( JLabel.CENTER );
		trackTable.getTableHeader().setDefaultRenderer(centerRenderer);
	    
		trackArea.setViewportView(trackTable);
	}
}
