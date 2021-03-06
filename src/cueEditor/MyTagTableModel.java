package cueEditor;

import javax.swing.table.AbstractTableModel;

public class MyTagTableModel extends MyTableModel{
	
	public MyTagTableModel(Object[][] track,int languageNumber){
		super(track);
		switch(languageNumber){
		case 0:
			column = new String[]{"No.","Total","Title","Artist","Composer","Minute","Second","Bitrate"};
			break;
		case 1:
			column = new String[]{"曲序","總共","歌名","演出者","作曲者","分鐘","秒數","碼率"};
			break;
		default:
			column = new String[]{"No.","Total","Title","Artist","Composer","Minute","Second","Bitrate"};
		}
	}

	@Override
	public int getRowCount() {
		return data.length;
	}

	@Override
	public int getColumnCount() {
		return column.length;
	}

	@Override
	public Object getValueAt(int rowIndex, int columnIndex) {
		return data[rowIndex][columnIndex];
	}
	
	@Override
	public String getColumnName(int columnIndex){
		return column[columnIndex];
	}
	
	@Override
	public boolean isCellEditable(int row, int col){
		return false;
	}
	
	@Override
	public Class getColumnClass(int columnIndex){
		return getValueAt(0,columnIndex).getClass();
	}
	
	@Override
	public void setValueAt(Object value, int rowIndex,int columnIndex){
		data[rowIndex][columnIndex] = value;
	}
}
