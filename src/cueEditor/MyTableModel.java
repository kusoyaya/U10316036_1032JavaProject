package cueEditor;

import javax.swing.table.AbstractTableModel;

public class MyTableModel extends AbstractTableModel{
	Object[][] data;
	private String[] column = {"曲序","歌名","演出者","分鐘","秒數","幀數"};
	
	public MyTableModel(Object[][] track){
		this.data = track;
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
		return true;
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
