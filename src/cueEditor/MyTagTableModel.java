package cueEditor;

import javax.swing.table.AbstractTableModel;

public class MyTagTableModel extends MyTableModel{
	
	public MyTagTableModel(Object[][] track){
		super(track);
		column = new String[]{"曲序","總共","歌名","演出者","作曲者","分鐘","秒數","碼率"};
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
		if(col == 5 || col == 6 || col == 7)
			return false;
		return true;
	}
	
	@Override
	public Class getColumnClass(int columnIndex){
		return getValueAt(0,columnIndex).getClass();
	}
	
	@Override
	public void setValueAt(Object value, int rowIndex,int columnIndex){
		data[rowIndex][columnIndex] = value;
		changed = true;
	}
}
