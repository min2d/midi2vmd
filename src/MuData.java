import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;


public class MuData {

	String filename;
	int motiontype;
	int moveX;
	int moveY;
	int moveZ;
	int rotateX=0;//回転軸3つと回転角度（ラジアン？）
	int rotateY=0;
	int rotateZ=0;
	float rotateW=0;
	
	int resolution;
	int tempo;
	int numofframe;
	int fps = 30;
	boolean setbonename=false;
	String bonename;
	
	//UIのアドレス
	JFrame frame;
	
	//配列
	List<Long> tick;
	List<Byte> key;
	List<Byte> vel;
	
	String outputfilename="output.vmd";
	FileOutputStream fos;
	BufferedOutputStream bos;
	
	//リスト実体化など。
	MuData(){
		tick=new ArrayList<Long>();
		key=new ArrayList<Byte>();
		vel=new ArrayList<Byte>();
	}
}
