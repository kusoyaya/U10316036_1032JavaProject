package cueEditor;

import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;

public class MyTableModel extends AbstractTableModel{
	Object[][] data;
	protected String[] column;
	protected boolean changed = false;
	
	protected MyTableModel(Object[][] track){
		this.data = track;
	}
	
	public MyTableModel(Object[][] track, int languageNumber){
		this.data = track;
		switch(languageNumber){
		case 0:
			column = new String[]{"No.","Title","Artist","Composer","Minute","Second","Frame"};
			break;
		case 1:
			column = new String[]{"曲序","歌名","演出者","作曲者","分鐘","秒數","幀數"};
			break;
		default:
			column = new String[]{"No.","Title","Artist","Composer","Minute","Second","Frame"};
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
		if(col == 0)
			return false;
		return true;
	}
	
	@Override
	public Class getColumnClass(int columnIndex){
		return getValueAt(0,columnIndex).getClass();
	}
	
	@Override
	public void setValueAt(Object value, int rowIndex,int columnIndex){
		if(columnIndex ==6 && (int)value >= 75){
			data[rowIndex][columnIndex] = (int)value % 75;
			data[rowIndex][columnIndex-1] = (int)data[rowIndex][columnIndex-1] + (int)value / 75;
			if((int)data[rowIndex][columnIndex-1] >= 60){
				data[rowIndex][columnIndex -1] = (int)data[rowIndex][columnIndex-1] % 60;
				data[rowIndex][columnIndex -2] = (int)data[rowIndex][columnIndex-2]+(int)data[rowIndex][columnIndex-1] / 60;
			}
		}else if(columnIndex ==5 && (int)value >= 60){
			data[rowIndex][columnIndex] = (int)value % 60;
			data[rowIndex][columnIndex -1] = (int)data[rowIndex][columnIndex-1]+(int)value / 60;
		}else{
			data[rowIndex][columnIndex] = value;
		}
		changed =true;
	}
	
	public boolean hasChanged(){
		return changed;
	}
}
