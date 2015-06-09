package cueEditor;

import java.awt.BorderLayout;
import java.awt.FlowLayout;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.border.EmptyBorder;

import java.awt.GridLayout;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;

public class coverDialog extends JDialog {

	private final JPanel contentPanel = new JPanel();
	private JLabel[] loadLabel = new JLabel[4];
	private JButton okButton;
	private int languageNumber = 0;
	private final String[][] languagePack = {
			{"close","Save successful!","Save failed！"},
			{"關閉","儲存成功","儲存失敗"}
			};
	
	public coverDialog(int languageNumber) {
		this.languageNumber = languageNumber;
		setBounds(100, 100, 650, 650);
		setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(new BorderLayout());
		contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
		getContentPane().add(contentPanel, BorderLayout.CENTER);
		contentPanel.setLayout(new GridLayout(0, 2, 0, 2));
		
		ImageIcon loadIcon = new ImageIcon(coverDialog.class.getResource("/loading.gif"));
		for(int i = 0; i < 4 ; i ++){
			loadLabel[i] = new JLabel();
			loadIcon.getImage().flush();
			loadLabel[i].setIcon(loadIcon);
			contentPanel.add(loadLabel[i]);
		}
		
		JPanel buttonPane = new JPanel();
		buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
		getContentPane().add(buttonPane, BorderLayout.SOUTH);
			
		okButton = new JButton(languagePack[languageNumber][0]);
		okButton.setEnabled(false);
		okButton.addActionListener(new ActionListener(){
			public void actionPerformed(ActionEvent e) {
				setVisible(false);
				dispose();
			}
		});
		buttonPane.add(okButton);
		getRootPane().setDefaultButton(okButton);
			
		
		setVisible(true);
	}
	
	public void setNine(BufferedImage[] images,String fileDirectory,String saveCoverName){
		int i = 0;
		for(BufferedImage image : images){
			loadLabel[i].setIcon(new ImageIcon(ServiceMachine.resizeTo300(images[i])));
			loadLabel[i].addMouseListener(new MouseAdapter(){
				public void mousePressed(MouseEvent e){
					if(SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2){
						try {
							File ouput = new File(fileDirectory+"/"+saveCoverName+".png");
							ImageIO.write(image, "png", ouput);
							JOptionPane.showMessageDialog(contentPanel, languagePack[languageNumber][1]);
						} catch (Exception ex) {
							JOptionPane.showMessageDialog(contentPanel, ex.getMessage(), languagePack[languageNumber][2], JOptionPane.ERROR_MESSAGE);
							ex.printStackTrace();
						}
					}
				}
			});
			contentPanel.add(loadLabel[i]);
			i++;
		}
		okButton.setEnabled(true);
	}
	
	public void setFail(){
		for(int i = 0 ; i < 4 ; i++)
			loadLabel[i].setIcon(new ImageIcon(coverDialog.class.getResource("/fail.png")));
		okButton.setEnabled(true);
	}

}
