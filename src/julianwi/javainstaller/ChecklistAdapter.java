package julianwi.javainstaller;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ChecklistAdapter extends BaseAdapter implements OnClickListener {
	
	public CheckPoint[] List;
	public Context mcontext;
	public MainActivity ma;
	
	public ChecklistAdapter(MainActivity ma, CheckPoint[] items) {
		mcontext = ma;
		this.ma = ma;
	    List = items;
	}

	@Override
	public int getCount() {
		return List.length+1;
	}

	@Override
	public Object getItem(int position) {
		if(position<List.length){
			return List[position];
		}
		return "button";
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(position<List.length){
			if(convertView == null){
				convertView = new LinearLayout(mcontext);
				((LinearLayout) convertView).setOrientation(LinearLayout.VERTICAL);
				TextView t1 = new TextView(mcontext);
				t1.setId(1);
			    ((LinearLayout) convertView).addView(t1);
			    LinearLayout ll = new LinearLayout(mcontext);
			    ll.setId(2);
			    ll.setOrientation(LinearLayout.HORIZONTAL);
			    Button b1 = new Button(mcontext);
			    b1.setId(0);
			    b1.setOnClickListener(List[position]);
			    Button b2 = new Button(mcontext);
			    b2.setText("change path");
			    b2.setId(1);
			    //disable change path for terminal
			    if(position == 0){
			    	b2.setEnabled(false);
			    	System.out.println(b2.getText());
			    }
			    b2.setOnClickListener(List[position]);
				ll.addView(b1);
				ll.addView(b2);
				((LinearLayout) convertView).addView(ll);
			}
			TextView tv1 = (TextView) convertView.findViewById(1);
			tv1.setText(List[position].text + "\npath:" + List[position].getPath());
			Button bt1 = (Button) convertView.findViewById(2).findViewById(0);
		    if(List[position].installed){
		    	bt1.setText("uninstall");;
		    }
		    else{
		    	bt1.setText("install");
		    }
		    
		    return convertView;
		}
		if(convertView==null){
			convertView = new Button(MainActivity.context);
			((Button) convertView).setText("run jar file");
			((Button) convertView).setOnClickListener(this);
		}
		return convertView;
	}

	@Override
	public void onClick(View v) {
		ma.choosefile("application/java-archive");
		/*Intent intent = new Intent(MainActivity.context, RunActivity.class);
	    MainActivity.context.startActivity(intent);*/
	}

}
