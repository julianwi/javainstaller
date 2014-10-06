package julianwi.javainstaller;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;

public class MainList extends BaseAdapter implements OnClickListener {

	private MainActivity ma;

	public MainList(MainActivity mainActivity) {
		ma = mainActivity;
	}

	@Override
	public int getCount() {
		return 4;
	}

	@Override
	public Object getItem(int position) {
		return position;
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Button b = (Button) convertView;
		if(b == null){
			b = new Button(ma);
			b.setOnClickListener(this);
		}
		switch (position){
			case 0:	b.setText("install java runtime comandline only");
			break;
			case 1: b.setText("install java runtime with awt graphic librarys");
			break;
			case 2: b.setText("view package list");
			break;
			case 3: b.setText("run jar file");
			break;
		}
		b.setId(position);
		return b;
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case 0:
			int packages = 0;
			for (int i = 0; i < 7; i++) {
				packages = packages+(((MainActivity.checks[i].installed)?0:1)<<(i+1));
			}
			Intent intent = new Intent(ma, InstallActivity.class);
			Bundle b = new Bundle();
			b.putInt("packages", packages);
			intent.putExtras(b);
			ma.startActivity(intent);
			break;
		case 2:
			ma.lv2 = new ListView(ma);
			ma.listenAdapter = new ChecklistAdapter(ma, MainActivity.checks);
			ma.lv2.setAdapter(ma.listenAdapter);
			ma.lv2.setOnItemClickListener(ma.listenAdapter);
			ma.setContentView(ma.lv2);
			ma.state = 1;
			break;
		case 3:
			ma.choosefile("application/java-archive");
			break;
		}
	}

}
