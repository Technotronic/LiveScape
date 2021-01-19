package livescape.audio.util;

import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.UnsupportedAudioFileException;

import org.apache.commons.io.IOUtils;
import org.lwjgl.BufferUtils;
import org.lwjgl.openal.AL10;

public class AudioBuffer
{
	public static long createBufferData(int p, String filePath) throws UnsupportedAudioFileException, IOException
	{    
    	// Shortcut finals:
        final int MONO = 1, STEREO = 2;
        
        AudioInputStream stream = null;
        File soundFile = new File(filePath);
        stream = AudioSystem.getAudioInputStream(soundFile);
        
        AudioFormat format = stream.getFormat();
        if(format.isBigEndian()) throw new UnsupportedAudioFileException("Can't handle Big Endian formats yet");
        
        //load stream into byte buffer
        int openALFormat = -1;
        switch(format.getChannels()) {
            case MONO:
                switch(format.getSampleSizeInBits()) {
                    case 8:
                        openALFormat = AL10.AL_FORMAT_MONO8;
                        break;
                    case 16:
                        openALFormat = AL10.AL_FORMAT_MONO16;
                        break;
                }
                break;
            case STEREO:
                switch(format.getSampleSizeInBits()) {
                    case 8:
                        openALFormat = AL10.AL_FORMAT_STEREO8;
                        break;
                    case 16:
                        openALFormat = AL10.AL_FORMAT_STEREO16;
                        break;
                }
                break;
        }
        
        //load data into a byte buffer
        //I've elected to use IOUtils from Apache Commons here, but the core
        //notion is to load the entire stream into the byte array--you can
        //do this however you would like.
        byte[] b = IOUtils.toByteArray(stream);
        ByteBuffer data = BufferUtils.createByteBuffer(b.length).put(b);
        data.flip();
        
        //load audio data into appropriate system space....
        AL10.alBufferData(p, openALFormat, data, (int)format.getSampleRate());
        
        //and return the rough notion of length for the audio stream!
        return (long)(1000f * stream.getFrameLength() / format.getFrameRate());
    }
}
