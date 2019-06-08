import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;

import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiEvent;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.Sequence;
import javax.sound.midi.Track;
import javax.swing.JOptionPane;

public class ReaderWriter {
	ReaderWriter(MuData h){
		//System.out.println(h.moveX+","+h.moveY+","+h.moveZ);
		//System.out.println(h.filename+","+h.motiontype+",");
		reader(h);
		writer(h);
	}
	void reader(MuData h){
		//オープン
		try {
			Sequence s = MidiSystem.getSequence(new File(h.filename));
			//System.out.println("四分音符1拍は"+s.getResolution()+"tickです");
			h.resolution=s.getResolution();
			
			if(s.getDivisionType()!=0.0){
				JOptionPane.showMessageDialog(h.frame, "時間単位が正常でない可能性があります");
			}

			//イベントトラック選出
			//tempo格納も同時にやる
			int i0=0;
			boolean flag_tempo=false;
			do{
				Track t = s.getTracks()[i0];
				//System.out.println("\ntrack"+i0);
				
				for(int i1 = 0; i1 < t.size() ;i1++){
					MidiEvent e = t.get(i1);
					MidiMessage m = e.getMessage();
					byte[] bytes = m.getMessage();
					
					
					if (bytes[0] != -112 && bytes[1] == 81){
						//System.out.println("☆tempo");
						int t1=bytes[3] &0xFF;
						int t2=bytes[4] &0xFF;
						int t3=bytes[5] &0xFF;
						int tem=t1*65536+t2*256+t3;
						//System.out.println("◆tem:"+tem);
						h.tempo=tem;//最後のテンポが反映される仕様
						flag_tempo=true;//ループ抜け用
					}
				}
				i0++;//ループ抜け用
			}while(/*i0<s.getTracks().length &&*/ flag_tempo==false);
			
			//ノートトラック選出/キーナンバー格納/ティック格納/ベロシティ格納
			int numofnoteon=0;
			i0=0;
			
			
			do{
				Track t = s.getTracks()[i0];	//i0:track number
				for(int i1 = 0; i1 < t.size() ;i1++){	//i1:event counter
					
					MidiEvent e = t.get(i1);
					long tick =e.getTick();
					MidiMessage m = e.getMessage();
					byte[] bytes = m.getMessage();
					if (-112<=bytes[0] && bytes[0]<=-97){//チャンネル1(0):112
						h.tick.add(tick);
						h.key.add(bytes[1]);
						h.vel.add(bytes[2]);
						numofnoteon++;
						
					}
				}
				i0++;//ループ抜け用
			}while(i0<s.getTracks().length && numofnoteon==0);
			
			h.numofframe=numofnoteon;
		} catch (InvalidMidiDataException | IOException e) {
			e.printStackTrace();
		}
	}
	
	void writer(MuData h){
		
		File outputFile = new File(h.outputfilename);
		try {
		      h.bos = new BufferedOutputStream(new FileOutputStream(outputFile)); 
		    } catch(Exception e) {} 
		
		//Vocaloid Motion Data 0002\0(30バイト)
	    byte[] data = new byte[30];
	    Arrays.fill(data, (byte)0);
	    byte[] headder;
		try {
			headder = "Vocaloid Motion Data 0002\0".getBytes("Shift_JIS");
			System.arraycopy(headder, 0, data, 0, headder.length);
		} catch (UnsupportedEncodingException e2) {}
		
	    
		
		
	    //モデル名HanedenStation(20バイト)
	    byte[] data2 = new byte[20];
	    Arrays.fill(data2, (byte)253);
	    byte[] modelname;
		try {
			modelname = "HanedenStation\0".getBytes("Shift_JIS");
			System.arraycopy(modelname, 0, data2, 0, modelname.length);
		} catch (UnsupportedEncodingException e1) {}
		
		
		//キーフレーム数(タイプ分岐考慮)
		byte[] fnum = new byte[4];
		Arrays.fill(fnum, (byte)0);
		
		int numofframe=0;
		switch(h.motiontype){
			case 0:numofframe=h.numofframe*3+12; break;//ゼロ→vel対応→ゼロ
			case 1:numofframe=h.numofframe+1; break;
			case 2:numofframe=h.numofframe*3+12; break;
			default:numofframe=h.numofframe+1; break;
		}
		
		fnum[1]=(byte)(numofframe/256);
		fnum[0]=(byte)(numofframe%256);
	    
		 try {
			h.bos.write(data);
			h.bos.write(data2);
			h.bos.write(fnum);
		 }catch (IOException e) {}
		 


		//ボーンデータの書き込み：タイプ分岐で別メソッドに渡す
		 switch(h.motiontype){
			case 0:writer0(h); break;//ゼロ→vel対応→ゼロ
			case 1:writer1(h); break;
			case 2:writer2(h); break;
			default: break;
		}
		 
		 
		//ストリームクローズ
		try {
			h.bos.flush();
			h.bos.close();
		} catch (IOException e) {}
		
		
	}
	
	void writer0(MuData h){//motiontype=0,12キー,回転ベロシティ影響
		String bonename;
		for(int j=0;j<12;j++){
			bonename= NumToKey.oneOct((int)j) + "\0";
			petitwriter(h,0,bonename, (byte)0);
		}
		for(int i=0;i<h.numofframe;i++){
			bonename=NumToKey.oneOct((byte)h.key.get(i)) + "\0";
			petitwriter(h, h.tick.get(i),bonename, (byte)0);
			petitwriter(h, h.tick.get(i)+30,bonename,h.vel.get(i));
			petitwriter(h, h.tick.get(i)+60,bonename,(byte)0);
		}
	}
	
	void writer1(MuData h){//motiontype=1
		petitwriter(h,0,h.bonename, (byte)0);
		for(int i=0;i<h.numofframe;i++){
			petitwriter(h, h.tick.get(i),h.bonename,h.key.get(i));
		}
	}
	
	void writer2(MuData h){//motiontype=2,12キー,変位一定
		String bonename;
		for(int j=0;j<12;j++){
			bonename= NumToKey.oneOct((int)j) + "\0";
			petitwriter(h,0,bonename, (byte)0);
		}
		for(int i=0;i<h.numofframe;i++){
			bonename=NumToKey.oneOct((byte)h.key.get(i)) + "\0";
			petitwriter(h, h.tick.get(i),bonename, (byte)0);
			petitwriter(h, h.tick.get(i)+30,bonename,(byte)127);
			petitwriter(h, h.tick.get(i)+60,bonename,(byte)0);
		}
	}
	
	void petitwriter(MuData h, long tick, String bonename, byte width){
		//1フレームぶんのデータを書くメソッドです。
		//確認用
		//System.out.printf("tick:%d, Keynum:%s, Vel:%d\n",tick,bonename,width);
		
		
		//ボーン名
		byte[] bone = new byte[15];
		Arrays.fill(bone, (byte)253);
		byte[] str;
		try {
			str = bonename.getBytes("Shift_JIS");
			System.arraycopy(str, 0, bone, 0, str.length);
		} catch (UnsupportedEncodingException e1) {}
		
		//キーフレーム番号
		byte[] framenum = new byte[4];
		Arrays.fill(framenum, (byte)0);
		long keyframenum=(long)(tick*h.tempo/h.resolution)*h.fps/1000000 ;

		//確認用
		//System.out.println(keyframenum);
		
		framenum[3]=(byte)(keyframenum/16777216);
		framenum[2]=(byte)(keyframenum/65536);
		framenum[1]=(byte)(keyframenum/256);
		framenum[0]=(byte)(keyframenum%256);

		//パラメータ
		byte[] mainparam = new byte[28];
		Arrays.fill(mainparam, (byte)0);
		
		
		byte movex=(byte)(width * h.moveX*0.01);//ベロシティ反映倍率指定
		int bits_velx=Float.floatToIntBits((float)movex);
		mainparam[0]=(byte)(bits_velx & 0xff);
		mainparam[1]=(byte)(bits_velx>>8 & 0xff);
		mainparam[2]=(byte)(bits_velx>>16 & 0xff);
		mainparam[3]=(byte)(bits_velx>>24 & 0xff);
		
		byte movey=(byte)(width * h.moveY*0.01);//ベロシティ反映倍率指定
		int bits_vely=Float.floatToIntBits((float)movey);
		mainparam[4]=(byte)(bits_vely & 0xff);
		mainparam[5]=(byte)(bits_vely>>8 & 0xff);
		mainparam[6]=(byte)(bits_vely>>16 & 0xff);
		mainparam[7]=(byte)(bits_vely>>24 & 0xff);
		
		byte movez=(byte)(width * h.moveZ*0.01);//ベロシティ反映倍率指定
		int bits_velz=Float.floatToIntBits((float)movez);
		mainparam[8]=(byte)(bits_velz & 0xff);
		mainparam[9]=(byte)(bits_velz>>8 & 0xff);
		mainparam[10]=(byte)(bits_velz>>16 & 0xff);
		mainparam[11]=(byte)(bits_velz>>24 & 0xff);

		//クォータニオンに変換して格納
		float rx=h.rotateX;
		float ry=h.rotateY;
		float rz=h.rotateZ;
		float rw=width * h.rotateW /127;
		
		
		float qx=(float) Math.sin(rw*rx/2);
		float qy=(float) Math.sin(rw*ry/2);
		float qz=(float) Math.sin(rw*rz/2);
		float qw=(float) Math.cos(rw/2);
		
		int bits_velrx=Float.floatToIntBits(qx);
		mainparam[12]=(byte)(bits_velrx & 0xff);
		mainparam[13]=(byte)(bits_velrx>>8 & 0xff);
		mainparam[14]=(byte)(bits_velrx>>16 & 0xff);
		mainparam[15]=(byte)(bits_velrx>>24 & 0xff);
		
		int bits_velry=Float.floatToIntBits(qy);
		mainparam[16]=(byte)(bits_velry & 0xff);
		mainparam[17]=(byte)(bits_velry>>8 & 0xff);
		mainparam[18]=(byte)(bits_velry>>16 & 0xff);
		mainparam[19]=(byte)(bits_velry>>24 & 0xff);
		
		int bits_velrz=Float.floatToIntBits(qz);
		mainparam[20]=(byte)(bits_velrz & 0xff);
		mainparam[21]=(byte)(bits_velrz>>8 & 0xff);
		mainparam[22]=(byte)(bits_velrz>>16 & 0xff);
		mainparam[23]=(byte)(bits_velrz>>24 & 0xff);
		
		int bits_velrw=Float.floatToIntBits(qw);
		mainparam[24]=(byte)(bits_velrw & 0xff);
		mainparam[25]=(byte)(bits_velrw>>8 & 0xff);
		mainparam[26]=(byte)(bits_velrw>>16 & 0xff);
		mainparam[27]=(byte)(bits_velrw>>24 & 0xff);
		
		
		byte[] hokanparam = new byte[64];
		Arrays.fill(hokanparam, (byte)20);
		
		
		try{
			h.bos.write(bone);
			h.bos.write(framenum);
			h.bos.write(mainparam);
			h.bos.write(hokanparam);
			
		} catch (IOException e) {}
	}
}
