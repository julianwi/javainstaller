package julianwi.javainstaller;

import android.content.Context;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public class ChecklistAdapter extends BaseAdapter implements OnItemClickListener {
	
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
		return List.length;
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
		TextView tv = (TextView) convertView;
		if(tv == null){
			tv = new TextView(mcontext);
		}
		if(List[position].installed){
			tv.setText(Html.fromHtml("<h2>(installed) "+List[position].text+"</h2>"+List[position].source));
		}
		else{
			tv.setText(Html.fromHtml("<h2>"+List[position].text+"</h2>"+List[position].source));
		}
		return tv;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		LinearLayout ll = new LinearLayout(mcontext);
		ll.setOrientation(LinearLayout.VERTICAL);
		TextView tv = new TextView(mcontext);
		tv.setText(Html.fromHtml("<h2>"+List[position].text+"</h2>"+List[position].source+"<br>"+"path:"+List[position].getPath()+"<br>"+"source:"+List[position].getSource()));
		LinearLayout ll2 = new LinearLayout(mcontext);
		Button b = new Button(mcontext);
		b.setText("change path");
		b.setId(1);
		b.setOnClickListener(List[position]);
		Button b2 = new Button(mcontext);
		b2.setText("change source");
		b2.setId(2);
		b2.setOnClickListener(List[position]);
		ll2.addView(b);
		ll2.addView(b2);
		Button b1 = new Button(mcontext);
		b1.setText("install");
		b1.setId(0);
		b1.setOnClickListener(List[position]);
		ll.addView(tv);
		ll.addView(ll2);
		ll.addView(b1);
		ma.setContentView(ll);
		ma.state = 2;
	}

}
