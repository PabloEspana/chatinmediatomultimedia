package com.android.wondercom.Entities;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.net.InetAddress;
import java.util.Arrays;
import java.util.Objects;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.util.Log;

@SuppressWarnings("serial")
public class Message implements Serializable{
	private static final String TAG = "Message";
	public static final int TEXT_MESSAGE = 1;
	public static final int IMAGE_MESSAGE = 2;
	public static final int VIDEO_MESSAGE = 3;
	public static final int AUDIO_MESSAGE = 4;
	public static final int FILE_MESSAGE = 5;
	public static final int DRAWING_MESSAGE = 6;


	@Override
	public String toString() {
		return null+","+chatName+","+byteArray+","+senderAddress+","+fileName+","+fileSize+","+filePath+","+isMine+
				","+tiempoEnvio()+","+ tiempo_recibo()+","+macOrigen+","+macDestino+","+activador+","+mText;
	}

	private int mType;
	private String mText;

	public Message(int mType, String mText, String chatName, byte[] byteArray, InetAddress senderAddress, String fileName, long fileSize, String filePath, boolean isMine, long mili_envio, long mili_recibo, String macOrigen, String macDestino, Boolean activador) {
		this.mType = mType;
		this.mText = mText;
		this.chatName = chatName;
		this.byteArray = byteArray;
		this.senderAddress = senderAddress;
		this.fileName = fileName;
		this.fileSize = fileSize;
		this.filePath = filePath;
		this.isMine = isMine;
		this.mili_envio = mili_envio;
		this.mili_recibo = mili_recibo;
		this.macOrigen = macOrigen;
		this.macDestino = macDestino;
		this.activador = activador;
	}

	private String chatName;
	private byte[] byteArray;
	private InetAddress senderAddress;
	private String fileName;
	private long fileSize;
	private String filePath;
	private boolean isMine;
	public long mili_envio;
	public long mili_recibo;
	public String macOrigen="null";
	public String macDestino="null";
	public Boolean activador=true;

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (!(o instanceof Message)) return false;
		Message message = (Message) o;
		return getmType() == message.getmType() &&
				getFileSize() == message.getFileSize() &&
				isMine() == message.isMine() &&
				mili_envio == message.mili_envio &&
				mili_recibo == message.mili_recibo &&
				Objects.equals(getmText(), message.getmText()) &&
				Objects.equals(getChatName(), message.getChatName()) &&
				Arrays.equals(getByteArray(), message.getByteArray()) &&
				Objects.equals(getSenderAddress(), message.getSenderAddress()) &&
				Objects.equals(getFileName(), message.getFileName()) &&
				Objects.equals(getFilePath(), message.getFilePath()) &&
				Objects.equals(getMacOrigen(), message.getMacOrigen()) &&
				Objects.equals(getMacDestino(), message.getMacDestino()) &&
				Objects.equals(getActivador(), message.getActivador());
	}

	@Override
	public int hashCode() {

		int result = Objects.hash(getmType(), getmText(), getChatName(), getSenderAddress(), getFileName(), getFileSize(), getFilePath(), isMine(), mili_envio, mili_recibo, getMacOrigen(), getMacDestino(), getActivador());
		result = 31 * result + Arrays.hashCode(getByteArray());
		return result;
	}

	//Getters and Setters
	public int getmType() { return mType; }
	public void setmType(int mType) { this.mType = mType; }
	public String getmText() { return mText; }
	public void setmText(String mText) { this.mText = mText; }
	public String getChatName() { return chatName; }
	public void setChatName(String chatName) { this.chatName = chatName; }
	public byte[] getByteArray() { return byteArray; }
	public void setByteArray(byte[] byteArray) { this.byteArray = byteArray; }
	public InetAddress getSenderAddress() { return senderAddress; }
	public void setSenderAddress(InetAddress senderAddress) { this.senderAddress = senderAddress; }
	public String getFileName() { return fileName; }
	public void setFileName(String fileName) { this.fileName = fileName; }
	public long getFileSize() { return fileSize; }
	public void setFileSize(long fileSize) { this.fileSize = fileSize; }
	public String getFilePath() { return filePath; }
	public void setFilePath(String filePath) { this.filePath = filePath; }
	public boolean isMine() { return isMine; }
	public void setMine(boolean isMine) { this.isMine = isMine; }

	public void setMili_envio(long milisegundo){this.mili_envio=milisegundo;}
	public void setMili_recibo(long miliseundo){this.mili_recibo=miliseundo;}

	public long tiempo_recibo(){return this.mili_recibo;}

	public long tiempoEnvio(){return mili_recibo-mili_envio;}
	
	public void setMacOrigen(String origen){this.macOrigen=origen;}
	public void setMacDestino(String destino){this.macDestino=destino;}

	public String getMacOrigen(){return this.macOrigen;}
	public String getMacDestino(){return this.macDestino;}

	public void setActivador(Boolean activado){this.activador=activado;}
	public Boolean getActivador(){return this.activador;}

	public Message(int type, String text, InetAddress sender, String name){
		mType = type;
		mText = text;
		senderAddress = sender;
		chatName = name;
	}

	public Bitmap byteArrayToBitmap(byte[] b){
		Log.v(TAG, "Convert byte array to image (bitmap)");
		return BitmapFactory.decodeByteArray(b, 0, b.length);
	}
	
	public void saveByteArrayToFile(Context context){
		Log.v(TAG, "Save byte array to file");
		switch(mType){
			case Message.AUDIO_MESSAGE:
				filePath = context.getExternalFilesDir(Environment.DIRECTORY_MUSIC).getAbsolutePath()+"/"+fileName;
				break;
			case Message.VIDEO_MESSAGE:
				filePath = context.getExternalFilesDir(Environment.DIRECTORY_MOVIES).getAbsolutePath()+"/"+fileName;
				break;
			case Message.FILE_MESSAGE:
				filePath = context.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath()+"/"+fileName;
				break;
			case Message.DRAWING_MESSAGE:
				filePath = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath()+"/"+fileName;
				break;
		}
		
		File file = new File(filePath);

		if (file.exists()) {
			file.delete();
		}

		try {
			FileOutputStream fos=new FileOutputStream(file.getPath());

			fos.write(byteArray);
			fos.close();
			Log.v(TAG, "Write byte array to file DONE !");
		}
		catch (java.io.IOException e) {
			e.printStackTrace();
			Log.e(TAG, "Write byte array to file FAILED !");
		}
	}
}
