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
			case 0:	b.setText((packages(false)!=0)?"install java runtime comandline only":"uninstall java runtime");
			break;
			case 1: b.setText((packages(true)!=0)?"install java runtime with awt graphic librarys":"uninstall awt graphic librarys");
			break;
			case 2: b.setText("view package list"+((Update.udate)?" (updates available)":""));
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
			int packages = packages(false);
			if(packages==0)packages=255-6;
			Intent intent = new Intent(ma, InstallActivity.class);
			Bundle b = new Bundle();
			b.putInt("packages", packages);
			intent.putExtras(b);
			ma.startActivity(intent);
			break;
		case 1:
			int packages1 = packages(true);
			if(packages1==0)packages1=1793;
			Intent intent1 = new Intent(ma, InstallActivity.class);
			Bundle b1 = new Bundle();
			b1.putInt("packages", packages1);
			intent1.putExtras(b1);
			ma.startActivity(intent1);
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
	
	public int packages(boolean awt){
		int packages = 0;
		for (int i = 0; i < ((awt)?10:7); i++) {
			packages = packages+(((MainActivity.checks[i].installed)?0:1)<<(i+1));
		}
		return packages;
	}

}
