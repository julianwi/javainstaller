package julianwi.javainstaller;

import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;

public class InstallList extends BaseAdapter {
	
	private List<LinearLayout> ll;
	
	public InstallList(List<LinearLayout> ll) {
		this.ll = ll;
	}

	@Override
	public int getCount() {
		return ll.size();
	}

	@Override
	public Object getItem(int position) {
		return ll.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		return ll.get(position);
	}

}
