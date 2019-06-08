import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


public class Control implements ActionListener, ChangeListener{

	MuData muData;
	JLabel lb1b;
	JLabel lb4cx, lb4cy, lb4cz, lb5c;
	JSlider sl4dx, sl4dy, sl4dz, sl5c;
	JRadioButton ch5bx, ch5by, ch5bz;
	
	JComboBox<String> cb1;
	JTextField tf3;
	
	
	Control(){
		muData = new MuData();
		muData.moveX=5;
		MyFrame();//フレームからRWが呼ばれる。
	}

	
	void MyFrame(){
				
		
		JFrame fr= new JFrame("Midi2Vmd");
		muData.frame=fr;//フレームにRWからメッセージを出す用

		//label
		JLabel lb1a = new JLabel("変換元ファイル:");
		       lb1b = new JLabel("選択されていません");
		JLabel lb2 = new JLabel("モーションタイプ:");
		JLabel lb3 = new JLabel("ボーン名(1ボーン選択時):");
		JLabel lb4a = new JLabel("移動方向:");
		JLabel lb4bx = new JLabel("x:");
		JLabel lb4by = new JLabel("y:");
		JLabel lb4bz = new JLabel("z:");
		       lb4cx = new JLabel("0");
		       lb4cx.setPreferredSize(new Dimension(25,12));
		       lb4cy = new JLabel("0");
		       lb4cz = new JLabel("0");
		JLabel lb5a = new JLabel("回転:");
		       lb5c = new JLabel("最大角度:0");
		
		//button
		JButton btn1 = new JButton("選択");
		btn1.addActionListener(this);
		
		JButton btn6 = new JButton("変換する");
		btn6.addActionListener(this);
		
		//conbobox
		cb1 = new JComboBox<String>();
		cb1.addItem("12ボーン,velocity→変位");//index:上から順に012…
		cb1.addItem("1ボーン,key→変位");
		cb1.addItem("12ボーン,変位一定");
		
		//Slider
		sl4dx= new JSlider(-100,100);
		sl4dx.addChangeListener(this);
		sl4dy= new JSlider(-100,100);
		sl4dy.addChangeListener(this);
		sl4dz= new JSlider(-100,100);
		sl4dz.addChangeListener(this);
		sl5c = new JSlider(-180,180);
		sl5c.addChangeListener(this);
		//textfield
		tf3= new JTextField(6);
		tf3.setText("センター");
		
		//radiobutton
		ch5bx= new JRadioButton("x軸");
		ch5by= new JRadioButton("y軸");
		ch5bz= new JRadioButton("z軸");
		ButtonGroup group = new ButtonGroup();
		group.add(ch5bx); group.add(ch5by); group.add(ch5bz); 
		
		//panel 4*
		JPanel pnl4a = new JPanel();
		pnl4a.add(lb4a);
		
		JPanel pnl4b = new JPanel();
		pnl4b.setLayout(new GridLayout(3,1));
		pnl4b.add(lb4bx); pnl4b.add(lb4by); pnl4b.add(lb4bz);
		
		JPanel pnl4c = new JPanel();
		pnl4c.setLayout(new GridLayout(3,1));
		pnl4c.add(lb4cx); pnl4c.add(lb4cy); pnl4c.add(lb4cz);
		
		JPanel pnl4d = new JPanel();
		pnl4d.setLayout(new GridLayout(3,1));
		pnl4d.add(sl4dx); pnl4d.add(sl4dy); pnl4d.add(sl4dz);
		
		//panel 5*
		JPanel pnl5a = new JPanel();
		pnl5a.add(lb5a);
		
		JPanel pnl5b = new JPanel();
		pnl5b.setLayout(new GridLayout(3,1));
		pnl5b.add(ch5bx); pnl5b.add(ch5by); pnl5b.add(ch5bz);
		
		JPanel pnl5c = new JPanel();
		pnl5c.setLayout(new GridLayout(3,1));
		 pnl5c.add(lb5c); pnl5c.add(sl5c);
		
		//panel *
		JPanel pnl1 = new JPanel();
		pnl1.add(lb1a); pnl1.add(lb1b); pnl1.add(btn1);
		pnl1.setBackground(Color.WHITE);
		
		JPanel pnl2 = new JPanel();
		pnl2.add(lb2); pnl2.add(cb1);
		pnl2.setSize(500, 40);
		
		JPanel pnl3 = new JPanel();
		pnl3.add(lb3); pnl3.add(tf3);
		
		JPanel pnl4 = new JPanel();
		pnl4.setBackground(Color.WHITE);
		pnl4.add(pnl4a); pnl4.add(pnl4b);
		pnl4.add(pnl4c); pnl4.add(pnl4d);
		
		JPanel pnl5 = new JPanel();
		pnl5.setBackground(Color.WHITE);
		pnl5.add(pnl5a); pnl5.add(pnl5b); pnl5.add(pnl5c);
		
		JPanel pnl6 = new JPanel();
		pnl6.add(btn6);
		
		//frame
		fr.setLayout(new FlowLayout(FlowLayout.LEFT));
		fr.add(pnl1); fr.add(pnl2); fr.add(pnl3);
		fr.add(pnl4); fr.add(pnl5); fr.add(pnl6);
		
		try {		/* 見た目をWindows Likeに */
			UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			SwingUtilities.updateComponentTreeUI(fr);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		
		
		fr.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		fr.setSize(350,400);
        fr.setVisible(true);
	}

	public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("選択")){

			JFileChooser filechooser = new JFileChooser(".");

		    int selected = filechooser.showOpenDialog(null);
		    if (selected == JFileChooser.APPROVE_OPTION){
		      File file = filechooser.getSelectedFile();
		      lb1b.setText(file.getName());
		    }else if (selected == JFileChooser.CANCEL_OPTION){
		     lb1b.setText("キャンセル");
		    }else if (selected == JFileChooser.ERROR_OPTION){
		      lb1b.setText("エラーです");
		    }
		}else if(e.getActionCommand().equals("変換する")){
			
			if(lb1b.getText().endsWith(".mid")||lb1b.getText().endsWith(".MID")){
				//指定した内容をhakoに格納してからライタを呼びます
				muData.filename=lb1b.getText();
				muData.motiontype=cb1.getSelectedIndex();
				muData.moveX=sl4dx.getValue();
				muData.moveY=sl4dy.getValue();
				muData.moveZ=sl4dz.getValue();
				muData.rotateW=(float) (sl5c.getValue()*Math.PI/180);
				
				if(ch5bx.isSelected()){
					muData.rotateX=1;
				}
				if(ch5by.isSelected()){
					muData.rotateY=1;
				}
				if(ch5bz.isSelected()){
					muData.rotateZ=1;
				}
				
				muData.bonename=tf3.getText()+"\0";
				
				new ReaderWriter(muData);
				lb1b.setText("実行しました");
			}else{
				lb1b.setText("midi以外はだめです");
			}
			
		}
	}

	public void stateChanged(ChangeEvent e) {
		lb4cx.setText(""+sl4dx.getValue());
		lb4cy.setText(""+sl4dy.getValue());
		lb4cz.setText(""+sl4dz.getValue());
		lb5c.setText("最大角度:"+sl5c.getValue());
	}
	
	

}
